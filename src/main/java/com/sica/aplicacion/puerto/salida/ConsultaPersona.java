package com.sica.aplicacion.puerto.salida;

import com.sica.dominio.modelo.Persona;

public interface ConsultaPersona {
    Persona buscarPorDocumento(String documento);

    // Restringe / restaura el acceso de una persona (módulo de Gestión de Incidentes).
    void bloquear(int personaId);
    void desbloquear(int personaId);

    // Devuelve el id generado para la nueva persona. empresaId puede ser null.
    int crear(String documento, String nombre, String tipoPersona, Integer empresaId);
}