package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.excepcion.ErrorAccesoBloqueado;
import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;

public interface ControladorEntradas {
    // Ingreso normal: si la persona no tiene nada especial pendiente hoy, entra directo.
    void procesarIngreso(Usuario actor, String documento)
        throws ErrorNoEncontrado, ErrorSinPermiso, ErrorAccesoBloqueado;

    // Ingreso especial: invitado no anunciado o trabajador con el carnet olvidado.
    // Crea la visita como "pendiente" en vez de dejarla entrar de una vez.
    void procesarIngresoEspecial(Usuario actor, String documento)
        throws ErrorNoEncontrado, ErrorSinPermiso, ErrorAccesoBloqueado;

    void procesarSalida(Usuario actor, String documento) throws ErrorNoEncontrado, ErrorSinPermiso;
}