package com.sica.infraestructura.adaptador.salida.mysql;

import com.sica.aplicacion.puerto.salida.MantenimientoDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MantenimientoDatosMySQL implements MantenimientoDatos {

    // Debe coincidir con el id de "Activo" en persona_estados_acceso (ver data.sql).
    private static final int ESTADO_ACCESO_ACTIVO = 1;

    @Override
    public void limpiarDatosDePrueba() {
        Connection conn = ConexionBD.getInstancia();
        try {
            // Todo o nada: si algo falla a la mitad, no queremos dejar la base a medio borrar.
            conn.setAutoCommit(false);

            // "incidentes" tiene una FK hacia "visitas", así que hay que borrarla primero:
            // MySQL no deja borrar una fila de "visitas" mientras algo en "incidentes" la referencie.
            ejecutar(conn, "DELETE FROM incidentes");
            ejecutar(conn, "DELETE FROM visitas");
            ejecutar(conn, "DELETE FROM bitacora_auditoria");

            try (PreparedStatement stmt = conn.prepareStatement("UPDATE personas SET estado_acceso_id = ?")) {
                stmt.setInt(1, ESTADO_ACCESO_ACTIVO);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackError) {
                rollbackError.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void ejecutar(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}