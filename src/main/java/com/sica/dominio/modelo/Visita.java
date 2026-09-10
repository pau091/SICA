package com.sica.dominio.modelo;

import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



//info que debe ingresar la visita
public class Visita {
    private int id;
    private Persona persona;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida;
    private String estado; // le dice al sistema si la persona todavia esta o no en el edificio

    public Visita (int id, Persona persona, LocalDateTime fechaIngreso, LocalDateTime fechaSalida, String estado){
        this.id = id;
        this.persona = persona;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.estado = estado;

    }

    public int getId() {return id; }
    public Persona getPersona () {return persona; }
    public LocalDateTime getFechaIngreso() {return fechaIngreso; }
    public LocalDateTime getFechaSalida() {return fechaSalida; }
    public String getEstado() {return estado; }
/* 
    public List<Visita> obtenerVisitaPorDia(LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime inicioSiguiente = fecha.plusDays(1).atStartOfDay();

        String sql = """
                SELECT *
                FROM visitas,
                WHERE fecha_entrada,
                AND (fecha_salida IS NULL OR fecha_salida > ?)
                """;

        List<Visita> visitas = new ArrayList<>();
        try(Connection ConexionBD.obtenerVisitaPorDia(fecha));
        PreparedStatement = statement = conexion.prepareStatement((sql)) {
        
            statement.setTimestamp(1, Timestamp.valueOf(inicioSiguiente));
            statement.setTimestamp(2, Timestamp.valueOf(inicioDia));
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Visita.set.FechaEntrada(
                    rs.getTimestamp("fecha_entrada").toLocalDateTime());
                    Timestamp salida = rs.getTimestamp("fecha_salida");
                    if (salida != null) {
                        visitas.setFechaSalida(salida.toLocalDateTime());
                    
                    }

                    visitas.add(visita);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las visitas", e);

        }
        return visitas;


    } */

}