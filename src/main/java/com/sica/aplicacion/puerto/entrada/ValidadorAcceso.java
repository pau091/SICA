package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.excepcion.ErrorAutenticacion;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;

public interface ValidadorAcceso {
    Usuario autenticar(String email, String password) throws ErrorAutenticacion;
    void validarPermiso(Usuario usuario, String permisoRequerido) throws ErrorSinPermiso;
}