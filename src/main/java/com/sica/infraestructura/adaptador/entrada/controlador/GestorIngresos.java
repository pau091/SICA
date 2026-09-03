package com.sica.infraestructura.adaptador.entrada.controlador;

import com.sica.dominio.excepcion.ErrorAccesoBloqueado;
import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.aplicacion.servicio.ControladorEntradasImpl;
import com.sica.aplicacion.puerto.entrada.ControladorEntradas;
import com.sica.infraestructura.config.SesionUsuario;

public class GestorIngresos {
    private ControladorEntradas controlador = new ControladorEntradasImpl();

    public void registrarEntrada(String documento) throws ErrorNoEncontrado, ErrorSinPermiso, ErrorAccesoBloqueado {
        controlador.procesarIngreso(SesionUsuario.getInstancia().getUsuarioActual(), documento);
    }

    // Invitado no anunciado o carnet olvidado: deja la visita como "pendiente" en vez de
    // dejar entrar a la persona de una vez.
    public void registrarEntradaEspecial(String documento) throws ErrorNoEncontrado, ErrorSinPermiso, ErrorAccesoBloqueado {
        controlador.procesarIngresoEspecial(SesionUsuario.getInstancia().getUsuarioActual(), documento);
    }

    public void registrarSalida(String documento) throws ErrorNoEncontrado, ErrorSinPermiso {
        controlador.procesarSalida(SesionUsuario.getInstancia().getUsuarioActual(), documento);
    }
}