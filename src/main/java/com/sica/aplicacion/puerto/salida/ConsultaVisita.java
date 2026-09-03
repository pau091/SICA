package com.sica.aplicacion.puerto.salida;

import com.sica.dominio.modelo.Visita;

import java.util.List;

public interface ConsultaVisita {
    // Ingreso directo (sin nada pendiente): crea la visita ya en estado "Dentro".
    void registrarIngreso(int personaId);

    // Cierra la visita activa de la persona (pasa a "Fuera").
    void registrarSalida(int visitaId);

    // La visita que sigue abierta ("Dentro") de esta persona, si tiene una. Sirve para el
    // check-out normal y para detectar el flujo de "salida olvidada".
    Visita buscarVisitaActiva(int personaId);

    // La última visita creada HOY para esta persona, sin importar en qué estado esté
    // (Pendiente, Aprobado, Rechazado, Dentro...). Sirve para saber si ya hay algo en curso hoy.
    Visita buscarVisitaDeHoyPorPersona(int personaId);

    // Crea una visita en estado "Pendiente de Aprobacion" (invitado no anunciado o carnet olvidado).
    void crearVisitaPendiente(int personaId);

    // Crea una visita ya en estado "Aprobado" (pre-registro hecho por un Funcionario con anticipación).
    void preRegistrarVisita(int personaId, int funcionarioId);

    // El Funcionario resuelve una visita pendiente: aprobada = true -> "Aprobado", false -> "Rechazado".
    void resolverAprobacion(int visitaId, boolean aprobada, int funcionarioId);

    // Convierte una visita "Aprobado" en un ingreso real: pasa a "Dentro" y marca la hora de entrada.
    void marcarCheckIn(int visitaId);

    // Cierra automáticamente una visita que quedó abierta (flujo de "salida olvidada").
    void cerrarPorSistema(int visitaId);

    // Todas las visitas que están esperando que un Funcionario las apruebe o rechace.
    List<Visita> listarPendientesDeAprobacion();
}