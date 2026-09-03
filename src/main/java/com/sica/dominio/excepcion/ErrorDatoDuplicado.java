package com.sica.dominio.excepcion;

// Se lanza al intentar crear un usuario o una persona con un email/documento que ya existe.
public class ErrorDatoDuplicado extends Exception {
    public ErrorDatoDuplicado(String mensaje) { super(mensaje); }
}