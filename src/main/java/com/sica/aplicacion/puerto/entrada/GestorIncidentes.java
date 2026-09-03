package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;

// Caso de uso del Supervisor de Seguridad: reportar incidentes y poder restringir (o restaurar)
// el acceso de una persona de inmediato, que era uno de los problemas que describía el enunciado.
public interface GestorIncidentes {
    void reportarIncidente(Usuario actor, String documentoPersona, String descripcion)
        throws ErrorSinPermiso, ErrorNoEncontrado;

    void bloquearPersona(Usuario actor, String documentoPersona) throws ErrorSinPermiso, ErrorNoEncontrado;

    void desbloquearPersona(Usuario actor, String documentoPersona) throws ErrorSinPermiso, ErrorNoEncontrado;
}