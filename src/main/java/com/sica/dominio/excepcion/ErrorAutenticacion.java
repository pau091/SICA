package com.sica.dominio.excepcion;

//se coloca extends para agarrar la info de la otra clase
//ademas del string mensaje se debe poner un super llamando a mensaje de nuevo
//errores de mensajes 

public class ErrorAutenticacion extends Exception {
    public ErrorAutenticacion(String mensaje) {super(mensaje); }

}