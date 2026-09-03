package com.sica.dominio.excepcion;

// Se lanza cuando una persona con estado "Con Prohibicion de Ingreso" intenta entrar.
public class ErrorAccesoBloqueado extends Exception {
    public ErrorAccesoBloqueado(String mensaje) { super(mensaje); }
}