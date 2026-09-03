package com.sica.aplicacion.servicio;

import com.sica.aplicacion.puerto.salida.ConsultaBitacora;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaBitacoraMySQL;
import com.sica.aplicacion.puerto.entrada.RegistradorAuditoria;

public class RegistradorAuditoriaImpl implements RegistradorAuditoria {
    private ConsultaBitacora bitacoraDAO = new ConsultaBitacoraMySQL();

    @Override
    public void registrarEvento(Integer usuarioId, String accion, String tabla, Integer registroId, String detalles) {
        bitacoraDAO.guardar(usuarioId, accion, tabla, registroId, detalles);
    }
}