package com.sica.infraestructura.adaptador.salida.mysql;

import com.sica.dominio.modelo.Empresa;
import com.sica.aplicacion.puerto.salida.ConsultaEmpresa;

import java.sql.*;

public class ConsultaEmpresaMySQL implements ConsultaEmpresa {
    @Override
    public Empresa buscarPorId(int id) {
        // Antes esta consulta pedía una columna "nit", pero la tabla "empresas" (ver schema.sql)
        // nunca tuvo esa columna, solo tiene: id, nombre, contacto_principal.
        // Como Java no revisa los nombres de columnas de la base de datos (eso solo lo nota MySQL
        // cuando el programa ya está corriendo), esto compilaba bien pero fallaba al usarlo de verdad.
        // Aquí solo pedimos las columnas que sí existen.
        String sql = "SELECT id, nombre, contacto_principal FROM empresas WHERE id = ?";
        try {
            Connection conn = ConexionBD.getInstancia();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Ojo: "contacto_principal" es un correo/nombre de contacto, no un NIT.
                // La base de datos no guarda un NIT en ningún lado todavía, así que no podemos
                // inventarlo aquí. Si más adelante lo necesitan de verdad, hay que agregar
                // esa columna en schema.sql primero.
                return new Empresa(rs.getInt("id"), rs.getString("nombre"), rs.getString("contacto_principal"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int crear(String nombre, String contactoPrincipal) {
        String sql = "INSERT INTO empresas (nombre, contacto_principal) VALUES (?, ?)";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nombre);
            stmt.setString(2, contactoPrincipal);
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

    @Override
    public void actualizar(int empresaId, String nombre, String contactoPrincipal) {
        String sql = "UPDATE empresas SET nombre = ?, contacto_principal = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, contactoPrincipal);
            stmt.setInt(3, empresaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean eliminar(int empresaId) {
        String sql = "DELETE FROM empresas WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empresaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Lo más probable es que falle por la FK de "personas" (esta empresa todavía
            // tiene personas asociadas). No se distingue el motivo, se reporta como "no se pudo".
            e.printStackTrace();
            return false;
        }
    }
}