package com.sica.dominio.excepcion;

//se llama a la clase excepcion con extends 
//se hace lo mismo de ErrorAutenticacion (Manejo de errores)
public class ErrorBaseDatos extends Exception {
    public ErrorBaseDatos(String mensaje) {super(mensaje); 

    }

}