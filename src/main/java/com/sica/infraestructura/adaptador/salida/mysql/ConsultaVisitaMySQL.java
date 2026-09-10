package com.sica.infraestructura.adaptador.salida.mysql;

import com.sica.dominio.modelo.Persona;
import com.sica.dominio.modelo.Visita;
import com.sica.aplicacion.puerto.salida.ConsultaVisita;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


public class ConsultaVisitaMySQL implements ConsultaVisita {

    // Estos números son los mismos ids con los que data.sql siembra la tabla "visita_estados".
    // Si algún día cambian esos ids en data.sql, hay que actualizarlos aquí también.
    private static final int ESTADO_DENTRO = 1;
    private static final int ESTADO_FUERA = 2;
    private static final int ESTADO_PENDIENTE = 3;
    private static final int ESTADO_APROBADO = 4;
    private static final int ESTADO_RECHAZADO = 5;
    private static final int ESTADO_CERRADA_POR_SISTEMA = 6;

    // Todas las consultas de lectura usan este mismo SELECT como base: traen la visita,
    // el nombre del estado (uniendo con visita_estados) y los datos básicos de la persona
    // (uniendo con personas), para que quien reciba el objeto Visita ya tenga todo lo necesario
    // sin tener que hacer otra consulta aparte.
    private static final String SELECT_BASE =
        "SELECT v.id, v.persona_id, v.fecha_entrada, v.fecha_salida, ve.nombre_estado, " +
        "       p.documento_identidad, p.nombre AS persona_nombre, p.tipo_persona " +
        "FROM visitas v " +
        "JOIN visita_estados ve ON v.estado_visita_id = ve.id " +
        "JOIN personas p ON v.persona_id = p.id ";

    @Override
    public void registrarIngreso(int personaId) {
        ejecutarInsertConEstado(personaId, ESTADO_DENTRO);
    }

    @Override
    public void crearVisitaPendiente(int personaId) {
        ejecutarInsertConEstado(personaId, ESTADO_PENDIENTE);
    }

    private void ejecutarInsertConEstado(int personaId, int estadoId) {
        String sql = "INSERT INTO visitas (persona_id, fecha_entrada, estado_visita_id) VALUES (?, NOW(), ?)";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personaId);
            stmt.setInt(2, estadoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preRegistrarVisita(int personaId, int funcionarioId) {
        // A diferencia de un ingreso normal, aquí ya sabemos quién la aprobó desde el principio
        // (el mismo funcionario que la está pre-registrando), así que guardamos ese dato también.
        String sql = "INSERT INTO visitas (persona_id, fecha_entrada, estado_visita_id, visita_aprobada_por) " +
                     "VALUES (?, NOW(), ?, ?)";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personaId);
            stmt.setInt(2, ESTADO_APROBADO);
            stmt.setInt(3, funcionarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registrarSalida(int visitaId) {
        String sql = "UPDATE visitas SET fecha_salida = NOW(), estado_visita_id = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ESTADO_FUERA);
            stmt.setInt(2, visitaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resolverAprobacion(int visitaId, boolean aprobada, int funcionarioId) {
        String sql = "UPDATE visitas SET estado_visita_id = ?, visita_aprobada_por = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, aprobada ? ESTADO_APROBADO : ESTADO_RECHAZADO);
            stmt.setInt(2, funcionarioId);
            stmt.setInt(3, visitaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void marcarCheckIn(int visitaId) {
        // La fecha de entrada se actualiza a "ahora", porque el momento real en que la persona
        // entra por la puerta es este, no el momento en que se creó/aprobó la visita.
        String sql = "UPDATE visitas SET estado_visita_id = ?, fecha_entrada = NOW() WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ESTADO_DENTRO);
            stmt.setInt(2, visitaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cerrarPorSistema(int visitaId) {
        String sql = "UPDATE visitas SET estado_visita_id = ?, fecha_salida = NOW() WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ESTADO_CERRADA_POR_SISTEMA);
            stmt.setInt(2, visitaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Visita buscarVisitaActiva(int personaId) {
        String sql = SELECT_BASE + "WHERE v.persona_id = ? AND v.estado_visita_id = ? ORDER BY v.id DESC LIMIT 1";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personaId);
            stmt.setInt(2, ESTADO_DENTRO);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Visita buscarVisitaDeHoyPorPersona(int personaId) {
        String sql = SELECT_BASE + "WHERE v.persona_id = ? AND DATE(v.fecha_entrada) = CURDATE() " +
                     "ORDER BY v.id DESC LIMIT 1";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Visita> listarPendientesDeAprobacion() {
        List<Visita> pendientes = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE v.estado_visita_id = ? ORDER BY v.fecha_entrada ASC";
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ESTADO_PENDIENTE);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pendientes.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendientes;
    }

    // Arma un objeto Visita completo (con su Persona, no solo el id) a partir de una fila
    // devuelta por cualquiera de los SELECT de arriba, todos basados en SELECT_BASE.
    private Visita mapear(ResultSet rs) throws SQLException {
        Persona persona = new Persona(
            rs.getInt("persona_id"),
            rs.getString("documento_identidad"),
            rs.getString("persona_nombre"),
            rs.getString("tipo_persona"),
            null
        );

        Timestamp entradaTs = rs.getTimestamp("fecha_entrada");
        LocalDateTime fechaIngreso = (entradaTs != null) ? entradaTs.toLocalDateTime() : null;

        Timestamp salidaTs = rs.getTimestamp("fecha_salida");
        LocalDateTime fechaSalida = (salidaTs != null) ? salidaTs.toLocalDateTime() : null;

        return new Visita(rs.getInt("id"), persona, fechaIngreso, fechaSalida, rs.getString("nombre_estado"));
    }

    @Override
public List<Visita> listarVisitasDelDia(LocalDate fecha) {
    List<Visita> visitas = new ArrayList<>();
    // Se solapa con el día si entró antes de que el día termine, y (sigue dentro o salió
    // después de que el día empezara). Cubre visitas que cruzan la medianoche en cualquier sentido.
    String sql = SELECT_BASE + "WHERE v.fecha_entrada < ? AND (v.fecha_salida IS NULL OR v.fecha_salida > ?)";

    LocalDateTime inicioDia = fecha.atStartOfDay();
    LocalDateTime finDia = inicioDia.plusDays(1);

    try (Connection conn = ConexionBD.getInstancia();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setTimestamp(1, Timestamp.valueOf(finDia));
        stmt.setTimestamp(2, Timestamp.valueOf(inicioDia));
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            visitas.add(mapear(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return visitas;
}
}