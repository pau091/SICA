package com.sica.dominio.modelo;

import java.time.LocalDateTime;

public class Incidente {
    private int id;
    private String descripcion;
    private LocalDateTime fecha;
    private Usuario usuario;

    public Incidente (int id, String descripcion, LocalDateTime fecha, Usuario usuario) {
        this.id = id;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.usuario = usuario;
    }

    public int getId(){return id;}
    public String getDescripcion () {return descripcion;}
    public LocalDateTime getFecha() {return fecha;}
    public Usuario getUsuario() {return usuario; }


}