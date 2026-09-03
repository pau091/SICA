package com.sica.infraestructura.adaptador.salida.mysql;


import com.sica.aplicacion.puerto.salida.ConsultaBitacora;

import java.sql.*;

public class ConsultaBitacoraMySQL implements ConsultaBitacora {
    @Override
    public void guardar(Integer usuarioId, String accion, String tabla, Integer registroId, String detalles) {
        String sql = "INSERT INTO bitacora_auditoria (usuario_id, accion_realizada, tabla_afectada, registro_id_afectado, detalles) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = ConexionBD.getInstancia();
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (usuarioId != null) stmt.setInt(1, usuarioId); else stmt.setNull(1, Types.INTEGER);
            stmt.setString(2, accion);
            stmt.setString(3, tabla);
            if (registroId != null) stmt.setInt(4, registroId); else stmt.setNull(4, Types.INTEGER);
            stmt.setString(5, detalles);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}