package com.sica.aplicacion.puerto.salida;

// Deja la base de datos como si acabaras de correr schema.sql + data.sql, pero sin tener que
// salir de la aplicación ni volver a pegar los scripts en MySQL Workbench.
public interface MantenimientoDatos {
    void limpiarDatosDePrueba();
}