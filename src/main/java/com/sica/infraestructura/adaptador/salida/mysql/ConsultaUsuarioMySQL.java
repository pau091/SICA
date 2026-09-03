package com.sica.infraestructura.adaptador.salida.mysql;

import com.sica.infraestructura.adaptador.salida.mysql.ConexionBD;
import com.sica.dominio.modelo.Permiso;
import com.sica.dominio.modelo.Rol;
import com.sica.dominio.modelo.Usuario;
import com.sica.aplicacion.puerto.salida.ConsultaUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Esta clase no existia en el proyecto. Se llamaba en otro archivo (ValidadorAccesoImpl)
// pero nunca se habia creado, por eso el login no podia funcionar.
// Su trabajo es simple: buscar un usuario por su correo, junto con su rol y los permisos de ese rol.
public class ConsultaUsuarioMySQL implements ConsultaUsuario {

    @Override
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT u.id, u.nombre, u.email, u.password, r.id AS rol_id, r.nombre_rol " +
                     "FROM usuarios u " +
                     "JOIN roles r ON u.rol_id = r.id " +
                     "WHERE u.email = ? AND u.esta_activo = TRUE";

        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int rolId = rs.getInt("rol_id");
                String nombreRol = rs.getString("nombre_rol");

                // Traemos tambien los permisos del rol, para que Rol.tienePermiso(...) funcione de verdad.
                List<Permiso> permisos = buscarPermisosDelRol(conn, rolId);
                Rol rol = new Rol(rolId, nombreRol, permisos);

                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rol
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int crear(String nombre, String email, String password, int rolId) {
        String sql = "INSERT INTO usuarios (nombre, email, password, rol_id, esta_activo) VALUES (?, ?, ?, ?, TRUE)";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setInt(4, rolId);
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

    // Metodo de apoyo: busca todos los permisos que tiene asignado un rol,
    // uniendo la tabla rol_permisos (union) con la tabla permisos.
    private List<Permiso> buscarPermisosDelRol(Connection conn, int rolId) throws SQLException {
        List<Permiso> permisos = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre_permiso " +
                     "FROM permisos p " +
                     "JOIN rol_permisos rp ON p.id = rp.permiso_id " +
                     "WHERE rp.rol_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rolId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                permisos.add(new Permiso(rs.getInt("id"), rs.getString("nombre_permiso")));
            }
        }
        return permisos;
    }
}