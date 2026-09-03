package com.sica.aplicacion.puerto.entrada;

public interface RegistradorAuditoria {
    void registrarEvento(Integer usuarioId, String accion, String tabla, Integer registroId, String detalles);
}