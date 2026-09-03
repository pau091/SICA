package com.sica.aplicacion.puerto.salida;

public interface ConsultaIncidente {
    // visitaId puede ser null: la tabla "incidentes" lo permite, para cuando el incidente
    // no está ligado a ninguna visita activa en ese momento.
    void registrarIncidente(Integer visitaId, String descripcion, int reportadoPorId);
}