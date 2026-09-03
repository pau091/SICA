package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.excepcion.ErrorDatoDuplicado;
import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorOperacionNoPermitida;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;

// Caso de uso exclusivo del Superusuario: administrar Usuarios, Personas y Empresas
// (crear, actualizar, eliminar). Cierra los 3 puntos de auditoría del enunciado sobre
// "creación, actualización o eliminación de cualquier entidad principal".
public interface GestorAdministracion {

    void crearUsuario(Usuario actor, String nombre, String email, String password, int rolId)
        throws ErrorSinPermiso, ErrorDatoDuplicado;

    void actualizarUsuario(Usuario actor, String email, String nuevoNombre, String nuevaPassword, int nuevoRolId)
        throws ErrorSinPermiso, ErrorNoEncontrado;

    void eliminarUsuario(Usuario actor, String email) throws ErrorSinPermiso, ErrorNoEncontrado;

    void crearPersona(Usuario actor, String documento, String nombre, String tipoPersona, Integer empresaId)
        throws ErrorSinPermiso, ErrorDatoDuplicado;

    void actualizarPersona(Usuario actor, String documento, String nuevoNombre, String nuevoTipo, Integer nuevaEmpresaId)
        throws ErrorSinPermiso, ErrorNoEncontrado;

    void eliminarPersona(Usuario actor, String documento)
        throws ErrorSinPermiso, ErrorNoEncontrado, ErrorOperacionNoPermitida;

    // Devuelve el id generado, para poder mostrarlo (la empresa no tiene otra clave natural
    // como un documento o un correo con el que buscarla después).
    int crearEmpresa(Usuario actor, String nombre, String contactoPrincipal) throws ErrorSinPermiso;

    void actualizarEmpresa(Usuario actor, int empresaId, String nuevoNombre, String nuevoContacto)
        throws ErrorSinPermiso, ErrorNoEncontrado;

    void eliminarEmpresa(Usuario actor, int empresaId)
        throws ErrorSinPermiso, ErrorNoEncontrado, ErrorOperacionNoPermitida;
}