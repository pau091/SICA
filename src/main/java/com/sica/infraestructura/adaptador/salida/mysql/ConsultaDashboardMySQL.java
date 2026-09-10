package com.sica.infraestructura.adaptador.salida.mysql;

import com.sica.aplicacion.puerto.salida.ConsultaDashboard;
import com.sica.dominio.modelo.ResumenAcceso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConsultaDashboardMySQL implements ConsultaDashboard {

    // Mismos ids que ya usan ConsultaVisitaMySQL / ConsultaPersonaMySQL para sus estados.
    private static final int ESTADO_VISITA_DENTRO = 1;
    private static final int ESTADO_VISITA_PENDIENTE = 3;
    private static final int ESTADO_ACCESO_BLOQUEADO = 2;

    // Una sola consulta con subconsultas escalares: más barato que 5 round-trips separados
    // a la base de datos para armar un solo panel.
    private static final String SQL =
        "SELECT " +
        "  (SELECT COUNT(*) FROM visitas WHERE estado_visita_id = ?) AS dentro, " +
        "  (SELECT COUNT(*) FROM visitas WHERE estado_visita_id = ?) AS pendientes, " +
        "  (SELECT COUNT(*) FROM incidentes WHERE DATE(fecha) = CURDATE()) AS incidentes_hoy, " +
        "  (SELECT COUNT(*) FROM personas WHERE estado_acceso_id = ?) AS bloqueadas, " +
        "  (SELECT COUNT(*) FROM empresas) AS empresas";

    @Override
    public ResumenAcceso obtenerResumen() {
        try (Connection conn = ConexionBD.getInstancia();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, ESTADO_VISITA_DENTRO);
            stmt.setInt(2, ESTADO_VISITA_PENDIENTE);
            stmt.setInt(3, ESTADO_ACCESO_BLOQUEADO);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ResumenAcceso(
                    rs.getInt("dentro"),
                    rs.getInt("pendientes"),
                    rs.getInt("incidentes_hoy"),
                    rs.getInt("bloqueadas"),
                    rs.getInt("empresas")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResumenAcceso(0, 0, 0, 0, 0);
    }
}