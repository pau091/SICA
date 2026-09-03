package com.sica.infraestructura.adaptador.entrada.swing;

import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;
import com.sica.aplicacion.puerto.entrada.GestorIncidentes;
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

// Pantalla del Supervisor de Seguridad: es el rol que faltaba por implementar. El enunciado lo
// describe como quien reporta incidentes y quien puede restringir el acceso de una persona
// de inmediato ("no hay forma de marcar a una persona con una restricción de acceso...").
public class PantallaSupervisor {

    private final GestorIncidentes gestorIncidentes = FabricaServicios.crearGestorIncidentes();
    private final ValidadorAcceso validador = FabricaServicios.crearValidadorAcceso();

    public void mostrarMenu() {
        String[] opciones = {
            "Reportar Incidente",
            "Bloquear Persona",
            "Desbloquear Persona",
            "Ver Reportes",
            "Cerrar Sesión"
        };
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                null, "Panel de Supervisor de Seguridad", "SICA - Supervisión",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]
            );

            if (opcion == 0) {
                reportarIncidente();
            } else if (opcion == 1) {
                cambiarBloqueo(true);
            } else if (opcion == 2) {
                cambiarBloqueo(false);
            } else if (opcion == 3) {
                mostrarTablaReportes();
            } else {
                break;
            }
        }
    }

    private void reportarIncidente() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();
        String documento = JOptionPane.showInputDialog(null, "Documento de la persona involucrada:");
        if (documento == null || documento.isBlank()) return;

        String descripcion = JOptionPane.showInputDialog(null, "Descripción del incidente:");
        if (descripcion == null || descripcion.isBlank()) return;

        try {
            gestorIncidentes.reportarIncidente(actor, documento.trim(), descripcion.trim());
            JOptionPane.showMessageDialog(null, "Incidente registrado.");
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // "bloquear" = true para restringir el acceso, false para restaurarlo.
    private void cambiarBloqueo(boolean bloquear) {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();
        String documento = JOptionPane.showInputDialog(null,
            (bloquear ? "Documento de la persona a bloquear:" : "Documento de la persona a desbloquear:"));
        if (documento == null || documento.isBlank()) return;

        try {
            if (bloquear) {
                gestorIncidentes.bloquearPersona(actor, documento.trim());
                JOptionPane.showMessageDialog(null, "Acceso restringido para esa persona.");
            } else {
                gestorIncidentes.desbloquearPersona(actor, documento.trim());
                JOptionPane.showMessageDialog(null, "Acceso restaurado para esa persona.");
            }
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Mismo reporte que ve el Funcionario: el Supervisor también tiene el permiso "generar_reporte".
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