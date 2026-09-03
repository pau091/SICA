package com.sica.infraestructura.adaptador.entrada.swing;

import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;
import com.sica.dominio.modelo.Visita;
import com.sica.aplicacion.puerto.entrada.GestorAprobaciones;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.infraestructura.adaptador.salida.mysql.ConexionBD;
import com.sica.infraestructura.config.FabricaServicios;
import com.sica.infraestructura.config.SesionUsuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PantallaFuncionario {

    private final GestorAprobaciones gestorAprobaciones = FabricaServicios.crearGestorAprobaciones();
    private final ValidadorAcceso validador = FabricaServicios.crearValidadorAcceso();

    public void mostrarMenu() {
        String[] opciones = {"Pre-registrar Visita", "Aprobar Visita Excepcional", "Ver Reportes", "Cerrar Sesión"};
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                null, "Panel de Funcionarios", "SICA - Gestión Admin",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]
            );

            if (opcion == 0) {
                preRegistrarVisita();
            } else if (opcion == 1) {
                aprobarVisitaExcepcional();
            } else if (opcion == 2) {
                mostrarTablaReportes();
            } else {
                break;
            }
        }
    }

    // Flujo del "invitado pre-registrado": el Funcionario autoriza a alguien ANTES de que llegue.
    // Cuando esa persona se presente en la portería, el Guarda va a poder hacerle check-in directo.
    private void preRegistrarVisita() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();
        String documento = JOptionPane.showInputDialog(null, "Documento de la persona a pre-registrar:");
        if (documento == null || documento.isBlank()) return;

        try {
            gestorAprobaciones.preRegistrarVisita(actor, documento.trim());
            JOptionPane.showMessageDialog(null,
                "Visita pre-registrada. Cuando esta persona llegue, el Guarda podrá dejarla entrar directamente.");
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Antes este método solo hacía un SELECT para ver si la persona existía y decía "autorizada"
    // sin cambiar nada en la base de datos: no aprobaba nada de verdad. Ahora sí lista las visitas
    // que de verdad están en estado "Pendiente de Aprobacion" y permite aprobar o rechazar una.
    private void aprobarVisitaExcepcional() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();
        try {
            List<Visita> pendientes = gestorAprobaciones.listarPendientes(actor);
            if (pendientes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay visitas pendientes de aprobación.");
                return;
            }

            StringBuilder lista = new StringBuilder("Visitas pendientes de aprobación:\n\n");
            for (Visita v : pendientes) {
                lista.append("ID ").append(v.getId())
                     .append(" - ").append(v.getPersona().getNombre())
                     .append(" (").append(v.getPersona().getDocumento()).append(")")
                     .append(" - ").append(v.getPersona().getTipo())
                     .append(" - solicitado: ").append(v.getFechaIngreso())
                     .append("\n");
            }
            JOptionPane.showMessageDialog(null, lista.toString());

            String idTexto = JOptionPane.showInputDialog(null, "Ingrese el ID de la visita a resolver:");
            if (idTexto == null || idTexto.isBlank()) return;

            int visitaId;
            try {
                visitaId = Integer.parseInt(idTexto.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ese ID no es un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int opcion = JOptionPane.showConfirmDialog(null,
                "¿Aprobar esta visita? (elija 'No' para rechazarla)",
                "Resolver visita #" + visitaId, JOptionPane.YES_NO_CANCEL_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                gestorAprobaciones.aprobar(actor, visitaId);
                JOptionPane.showMessageDialog(null, "Visita aprobada.");
            } else if (opcion == JOptionPane.NO_OPTION) {
                gestorAprobaciones.rechazar(actor, visitaId);
                JOptionPane.showMessageDialog(null, "Visita rechazada.");
            }
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarTablaReportes() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();
        try {
            validador.validarPermiso(actor, "generar_reporte");
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] columnas = {"ID Visita", "Nombre", "Documento", "Tipo Persona", "Fecha Entrada", "Fecha Salida", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        // Antes esta consulta pedía una columna "v.estado" que no existe en la tabla "visitas"
        // (el estado ahí se guarda como "estado_visita_id", un número que apunta a otra tabla).
        // Por eso se une con "visita_estados" para mostrar el nombre real del estado.
        String sql = "SELECT v.id, p.nombre, p.documento_identidad, p.tipo_persona, " +
                     "       v.fecha_entrada, v.fecha_salida, ve.nombre_estado " +
                     "FROM visitas v " +
                     "JOIN personas p ON v.persona_id = p.id " +
                     "JOIN visita_estados ve ON v.estado_visita_id = ve.id " +
                     "ORDER BY v.fecha_entrada DESC";

        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("documento_identidad"),
                    rs.getString("tipo_persona"),
                    rs.getTimestamp("fecha_entrada"),
                    rs.getTimestamp("fecha_salida") != null ? rs.getTimestamp("fecha_salida") : "EN CAMPUS",
                    rs.getString("nombre_estado")
                };
                model.addRow(fila);
            }

            JTable tabla = new JTable(model);
            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setPreferredSize(new Dimension(750, 320));

            JOptionPane.showMessageDialog(null, scroll, "Reporte de Entradas y Salidas - SICA", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar los reportes: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }
}