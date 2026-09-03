package com.sica.infraestructura.adaptador.salida.mysql;

import com.sica.dominio.modelo.Persona;
import com.sica.aplicacion.puerto.salida.ConsultaPersona;

import java.sql.*;

public class ConsultaPersonaMySQL implements ConsultaPersona {

    // Deben coincidir con los ids de data.sql (tabla persona_estados_acceso).
    private static final int ESTADO_ACTIVO = 1;
    private static final int ESTADO_BLOQUEADO = 2;

    @Override
    public Persona buscarPorDocumento(String documento) {
        // LEFT JOIN (no JOIN) porque estado_acceso_id puede quedar en null en la tabla personas;
        // con un JOIN normal esas personas dejarían de aparecer aquí por completo.
        String sql = "SELECT p.id, p.documento_identidad, p.nombre, p.tipo_persona, pe.nombre_estado " +
                     "FROM personas p " +
                     "LEFT JOIN persona_estados_acceso pe ON p.estado_acceso_id = pe.id " +
                     "WHERE p.documento_identidad = ?";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documento);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Persona(
                    rs.getInt("id"),
                    rs.getString("documento_identidad"),
                    rs.getString("nombre"),
                    rs.getString("tipo_persona"),
                    null,
                    rs.getString("nombre_estado")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void bloquear(int personaId) {
        actualizarEstado(personaId, ESTADO_BLOQUEADO);
    }

    @Override
    public void desbloquear(int personaId) {
        actualizarEstado(personaId, ESTADO_ACTIVO);
    }

    @Override
    public int crear(String documento, String nombre, String tipoPersona, Integer empresaId) {
        String sql = "INSERT INTO personas (nombre, documento_identidad, empresa_id, tipo_persona, estado_acceso_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nombre);
            stmt.setString(2, documento);
            if (empresaId != null) {
                stmt.setInt(3, empresaId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, tipoPersona);
            stmt.setInt(5, ESTADO_ACTIVO);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void actualizarEstado(int personaId, int estadoAccesoId) {
        String sql = "UPDATE personas SET estado_acceso_id = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, estadoAccesoId);
            stmt.setInt(2, personaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}