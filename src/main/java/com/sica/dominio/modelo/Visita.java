package com.sica.dominio.modelo;

import java.time.LocalDateTime;




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


}