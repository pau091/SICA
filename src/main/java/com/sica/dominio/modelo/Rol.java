package com.sica.dominio.modelo;

//roles que requieren id con el nombre del rol y permisos

import java.util.List;


public class Rol {
    private int id;
    private String nombreRol;
    private List <Permiso> permisos;


    public Rol(int id, String nombreRol, List<Permiso> permisos){
        this.id = id;
        this.nombreRol = nombreRol;
        this.permisos = permisos;

    }

    public int getId() {return id;

    }

    public String getNombreRol() {return nombreRol;

    }
    public List<Permiso> getPermiso() {return permisos; 

    }

    public boolean tienePermiso(String nombrePermiso){
        if(permisos == null) return  false;
        return permisos.stream(). anyMatch(p -> p.getNombrePermiso().equalsIgnoreCase(nombrePermiso));

    }
}