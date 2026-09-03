package com.sica.aplicacion.puerto.salida;

public interface ConsultaBitacora {
    void guardar(Integer usuarioId, String accion, String tabla, Integer registroId, String detalles );

}