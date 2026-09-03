package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;
import com.sica.dominio.modelo.Visita;

import java.util.List;

// Este es el "caso de uso" del Funcionario de Empresa: ver qué visitas están esperando
// su aprobación, aprobarlas o rechazarlas, y pre-registrar visitas con anticipación.
public interface GestorAprobaciones {

    List<Visita> listarPendientes(Usuario actor) throws ErrorSinPermiso;

    void aprobar(Usuario actor, int visitaId) throws ErrorSinPermiso;

    void rechazar(Usuario actor, int visitaId) throws ErrorSinPermiso;

    // Flujo del "invitado pre-registrado": el Funcionario ya sabe que alguien va a llegar
    // y lo autoriza de una vez, antes de que se presente en la portería.
    void preRegistrarVisita(Usuario actor, String documentoPersona) throws ErrorSinPermiso, ErrorNoEncontrado;
}