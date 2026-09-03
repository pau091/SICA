package com.sica.dominio.excepcion;
// Se lanza cuando una operación es válida en principio (el que la pide tiene permiso, el
// registro existe) pero no se puede completar por una regla del negocio — por ejemplo,
// borrar una empresa que todavía tiene personas asociadas.
public class ErrorOperacionNoPermitida extends Exception {
    public ErrorOperacionNoPermitida(String mensaje) { super(mensaje); }
}