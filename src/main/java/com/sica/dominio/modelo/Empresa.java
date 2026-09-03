package com.sica.dominio.modelo;

public class Empresa {

    private int id;
    private String nombre;
    private String numIdentificacion;

    public Empresa(int id, String nombre, String numIdentificacion){
        this.id = id;
        this.nombre = nombre;
        this.numIdentificacion = numIdentificacion;
    }
    public int getId() {return id;}
    public String getNombre() {return nombre;}
    public String getnumIdentificacion() {return numIdentificacion; }

}