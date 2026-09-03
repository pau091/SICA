package com.sica.infraestructura.adaptador.salida.mysql;

import com.sica.aplicacion.puerto.salida.ConsultaIncidente;

import java.sql.*;

public class ConsultaIncidenteMySQL implements ConsultaIncidente {
    @Override
    public void registrarIncidente(Integer visitaId, String descripcion, int reportadoPorId) {
        String sql = "INSERT INTO incidentes (visita_id, descripcion, fecha, reportado_por_id) VALUES (?, ?, NOW(), ?)";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (visitaId != null) {
                stmt.setInt(1, visitaId);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, descripcion);
            stmt.setInt(3, reportadoPorId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}