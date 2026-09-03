package com.sica.dominio.modelo;

public class Permiso {
    private int id;
    private String nombrePermiso;

    public Permiso(int id, String nombrePermiso){
        this.id = id;
        this.nombrePermiso = nombrePermiso;

    }
    public int getId() {
        return id;
    }
    public String getNombrePermiso(){return nombrePermiso;}

}
//permisos q se requieren