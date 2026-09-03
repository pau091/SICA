package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;

// Caso de uso para dejar limpios los datos de prueba (pensado para antes de una demo),
// sin tener que volver a correr schema.sql/data.sql a mano.
public interface GestorMantenimiento {
    void limpiarDatosDePrueba(Usuario actor) throws ErrorSinPermiso;
}