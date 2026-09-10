package com.sica.infraestructura.adaptador.entrada.swing;

import com.sica.dominio.excepcion.ErrorDatoDuplicado;
import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorOperacionNoPermitida;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;
import com.sica.dominio.modelo.Visita;
import com.sica.aplicacion.puerto.entrada.GestorAdministracion;
import com.sica.aplicacion.puerto.entrada.GestorAprobaciones;
import com.sica.aplicacion.puerto.entrada.GestorDashboard;
import com.sica.aplicacion.puerto.entrada.GestorMantenimiento;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.infraestructura.adaptador.salida.mysql.ConexionBD;
import com.sica.infraestructura.config.FabricaServicios;
import com.sica.infraestructura.config.SesionUsuario;
import com.sica.dominio.modelo.ResumenAcceso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

// Pantalla exclusiva del Superusuario. Antes este rol compartía pantalla con el Funcionario
// (PantallaFuncionario) porque no tenía ninguna acción realmente propia. Ahora sí la tiene:
// administrar Usuarios, Personas y Empresas (crear/actualizar/eliminar) — lo que además cierra
// los 3 puntos de auditoría del enunciado sobre esas entidades.
public class PantallaAdmin {

    private final GestorAdministracion gestorAdministracion = FabricaServicios.crearGestorAdministracion();
    private final GestorAprobaciones gestorAprobaciones = FabricaServicios.crearGestorAprobaciones();
    private final GestorMantenimiento gestorMantenimiento = FabricaServicios.crearGestorMantenimiento();
    private final ValidadorAcceso validador = FabricaServicios.crearValidadorAcceso();

    public void mostrarMenu() {
        String[] opciones = {
            "Gestionar Usuarios", "Gestionar Personas", "Gestionar Empresas",
            "Pre-registrar Visita", "Aprobar Visita Excepcional", "Ver Reportes",
            "Limpiar Datos de Prueba", "Cerrar Sesión"
        };
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                null, "Panel de Superusuario", "SICA - Administración",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]
            );

            if (opcion == 0) {
                gestionarUsuarios();
            } else if (opcion == 1) {
                gestionarPersonas();
            } else if (opcion == 2) {
                gestionarEmpresas();
            } else if (opcion == 3) {
                preRegistrarVisita();
            } else if (opcion == 4) {
                aprobarVisitaExcepcional();
            } else if (opcion == 5) {
                mostrarTablaReportes();
            } else if (opcion == 6) {
                limpiarDatosDePrueba();
            } else {
                break;
            }
        }
    }

    // ---------- Submenús de administración ----------

    private void gestionarUsuarios() {
        String[] opciones = {"Crear", "Actualizar", "Eliminar", "Volver"};
        int opcion = JOptionPane.showOptionDialog(null, "Gestión de Usuarios", "SICA - Administración",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (opcion == 0) crearUsuario();
        else if (opcion == 1) actualizarUsuario();
        else if (opcion == 2) eliminarUsuario();
    }

    private void gestionarPersonas() {
        String[] opciones = {"Crear", "Actualizar", "Eliminar", "Volver"};
        int opcion = JOptionPane.showOptionDialog(null, "Gestión de Personas", "SICA - Administración",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (opcion == 0) crearPersona();
        else if (opcion == 1) actualizarPersona();
        else if (opcion == 2) eliminarPersona();
    }

    private void gestionarEmpresas() {
        String[] opciones = {"Crear", "Actualizar", "Eliminar", "Volver"};
        int opcion = JOptionPane.showOptionDialog(null, "Gestión de Empresas", "SICA - Administración",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (opcion == 0) crearEmpresa();
        else if (opcion == 1) actualizarEmpresa();
        else if (opcion == 2) eliminarEmpresa();
    }

    // ---------- Usuarios ----------

    private void crearUsuario() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        String nombre = JOptionPane.showInputDialog(null, "Nombre completo:");
        if (nombre == null || nombre.isBlank()) return;

        String email = JOptionPane.showInputDialog(null, "Correo:");
        if (email == null || email.isBlank()) return;

        String password = JOptionPane.showInputDialog(null, "Contraseña:");
        if (password == null || password.isBlank()) return;

        String rolTexto = JOptionPane.showInputDialog(null,
            "ID del rol (1=Superusuario, 2=Supervisor de Seguridad, 3=Guarda de Seguridad, 4=Funcionario de Empresa):");
        if (rolTexto == null || rolTexto.isBlank()) return;

        try {
            int rolId = Integer.parseInt(rolTexto.trim());
            gestorAdministracion.crearUsuario(actor, nombre.trim(), email.trim(), password.trim(), rolId);
            JOptionPane.showMessageDialog(null, "Usuario creado.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID de rol debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorDatoDuplicado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarUsuario() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        String email = JOptionPane.showInputDialog(null, "Correo del usuario a actualizar:");
        if (email == null || email.isBlank()) return;

        String nombre = JOptionPane.showInputDialog(null, "Nuevo nombre:");
        if (nombre == null || nombre.isBlank()) return;

        String password = JOptionPane.showInputDialog(null, "Nueva contraseña:");
        if (password == null || password.isBlank()) return;

        String rolTexto = JOptionPane.showInputDialog(null, "Nuevo ID de rol (1-4):");
        if (rolTexto == null || rolTexto.isBlank()) return;

        try {
            int rolId = Integer.parseInt(rolTexto.trim());
            gestorAdministracion.actualizarUsuario(actor, email.trim(), nombre.trim(), password.trim(), rolId);
            JOptionPane.showMessageDialog(null, "Usuario actualizado.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID de rol debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarUsuario() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        String email = JOptionPane.showInputDialog(null, "Correo del usuario a eliminar:");
        if (email == null || email.isBlank()) return;

        int confirmacion = JOptionPane.showConfirmDialog(null,
            "Esto desactiva al usuario (ya no podrá loguearse), sin borrar su historial de auditoría.\n¿Continuar?",
            "Eliminar usuario", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            gestorAdministracion.eliminarUsuario(actor, email.trim());
            JOptionPane.showMessageDialog(null, "Usuario eliminado (desactivado).");
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Personas ----------

    private void crearPersona() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        String documento = JOptionPane.showInputDialog(null, "Documento de identidad:");
        if (documento == null || documento.isBlank()) return;

        String nombre = JOptionPane.showInputDialog(null, "Nombre completo:");
        if (nombre == null || nombre.isBlank()) return;

        String tipo = JOptionPane.showInputDialog(null, "Tipo de persona ('Trabajador' o 'Invitado'):");
        if (tipo == null || tipo.isBlank()) return;

        String empresaTexto = JOptionPane.showInputDialog(null,
            "ID de la empresa (dejar vacío si no aplica; 1=TechCorp Acme, 2=Innovatech):");

        try {
            Integer empresaId = (empresaTexto == null || empresaTexto.isBlank())
                ? null : Integer.parseInt(empresaTexto.trim());
            gestorAdministracion.crearPersona(actor, documento.trim(), nombre.trim(), tipo.trim(), empresaId);
            JOptionPane.showMessageDialog(null, "Persona creada.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID de empresa debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorDatoDuplicado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarPersona() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        String documento = JOptionPane.showInputDialog(null, "Documento de la persona a actualizar:");
        if (documento == null || documento.isBlank()) return;

        String nombre = JOptionPane.showInputDialog(null, "Nuevo nombre:");
        if (nombre == null || nombre.isBlank()) return;

        String tipo = JOptionPane.showInputDialog(null, "Nuevo tipo ('Trabajador' o 'Invitado'):");
        if (tipo == null || tipo.isBlank()) return;

        String empresaTexto = JOptionPane.showInputDialog(null,
            "Nuevo ID de empresa (dejar vacío si no aplica):");

        try {
            Integer empresaId = (empresaTexto == null || empresaTexto.isBlank())
                ? null : Integer.parseInt(empresaTexto.trim());
            gestorAdministracion.actualizarPersona(actor, documento.trim(), nombre.trim(), tipo.trim(), empresaId);
            JOptionPane.showMessageDialog(null, "Persona actualizada.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID de empresa debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPersona() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        String documento = JOptionPane.showInputDialog(null, "Documento de la persona a eliminar:");
        if (documento == null || documento.isBlank()) return;

        int confirmacion = JOptionPane.showConfirmDialog(null,
            "Esto borra a la persona permanentemente. Solo funciona si no tiene visitas registradas.\n¿Continuar?",
            "Eliminar persona", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            gestorAdministracion.eliminarPersona(actor, documento.trim());
            JOptionPane.showMessageDialog(null, "Persona eliminada.");
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorOperacionNoPermitida e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "No permitido", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Empresas ----------

    private void crearEmpresa() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        String nombre = JOptionPane.showInputDialog(null, "Nombre de la empresa:");
        if (nombre == null || nombre.isBlank()) return;

        String contacto = JOptionPane.showInputDialog(null, "Contacto principal (correo o nombre):");

        try {
            int nuevoId = gestorAdministracion.crearEmpresa(actor, nombre.trim(), (contacto == null) ? null : contacto.trim());
            JOptionPane.showMessageDialog(null, "Empresa creada con ID " + nuevoId + ".");
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEmpresa() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        String idTexto = JOptionPane.showInputDialog(null, "ID de la empresa a actualizar:");
        if (idTexto == null || idTexto.isBlank()) return;

        String nombre = JOptionPane.showInputDialog(null, "Nuevo nombre:");
        if (nombre == null || nombre.isBlank()) return;

        String contacto = JOptionPane.showInputDialog(null, "Nuevo contacto principal:");

        try {
            int empresaId = Integer.parseInt(idTexto.trim());
            gestorAdministracion.actualizarEmpresa(actor, empresaId, nombre.trim(), (contacto == null) ? null : contacto.trim());
            JOptionPane.showMessageDialog(null, "Empresa actualizada.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEmpresa() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        String idTexto = JOptionPane.showInputDialog(null, "ID de la empresa a eliminar:");
        if (idTexto == null || idTexto.isBlank()) return;

        int confirmacion = JOptionPane.showConfirmDialog(null,
            "Esto borra la empresa permanentemente. Solo funciona si no tiene personas asociadas.\n¿Continuar?",
            "Eliminar empresa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            int empresaId = Integer.parseInt(idTexto.trim());
            gestorAdministracion.eliminarEmpresa(actor, empresaId);
            JOptionPane.showMessageDialog(null, "Empresa eliminada.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorOperacionNoPermitida e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "No permitido", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- El resto es idéntico a lo que ya tiene PantallaFuncionario ----------

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

    private void aprobarVisitaExcepcional() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();
        try {
            List<Visita> pendientes = gestorAprobaciones.listarPendientes(actor);
            if (pendientes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay visitas pendientes de aprobación.");
                return;
            }

            // Antes esto era un "for" armando un StringBuilder a mano. Con Stream: se transforma
            // (map) cada Visita en su línea de texto, y se unen (collect + joining) con un
            // encabezado al principio, sin bucle ni variable mutable.
            String lista = pendientes.stream()
                .map(v -> "ID " + v.getId() + " - " + v.getPersona().getNombre() +
                          " (" + v.getPersona().getDocumento() + ")" +
                          " - " + v.getPersona().getTipo() +
                          " - solicitado: " + v.getFechaIngreso())
                .collect(Collectors.joining("\n", "Visitas pendientes de aprobación:\n\n", ""));

            JOptionPane.showMessageDialog(null, lista);

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

    private void limpiarDatosDePrueba() {
        Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();

        int confirmacion = JOptionPane.showConfirmDialog(null,
            "Esto borra todas las visitas, incidentes y la bitácora de auditoría actuales,\n" +
            "y desbloquea a todas las personas. No se puede deshacer.\n\n¿Continuar?",
            "Limpiar datos de prueba", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            gestorMantenimiento.limpiarDatosDePrueba(actor);
            JOptionPane.showMessageDialog(null, "Datos de prueba limpiados. El sistema quedó como recién instalado.");
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private final GestorDashboard gestorDashboard = FabricaServicios.crearGestorDashboard();

private void mostrarDashboard() {
    Usuario actor = SesionUsuario.getInstancia().getUsuarioActual();
    try {
        ResumenAcceso resumen = gestorDashboard.obtenerResumen(actor);
        String texto = "Personas dentro ahora mismo: " + resumen.getPersonasDentro() + "\n" +
                       "Visitas pendientes de aprobación: " + resumen.getVisitasPendientes() + "\n" +
                       "Incidentes reportados hoy: " + resumen.getIncidentesHoy() + "\n" +
                       "Personas con acceso bloqueado: " + resumen.getPersonasBloqueadas() + "\n" +
                       "Empresas registradas: " + resumen.getEmpresasRegistradas();
        JOptionPane.showMessageDialog(null, texto, "Panel de Indicadores - SICA", JOptionPane.INFORMATION_MESSAGE);
    } catch (ErrorSinPermiso e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
    }
}
}