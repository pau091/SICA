package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.excepcion.ErrorDatoDuplicado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;

// Caso de uso exclusivo del Superusuario: crear usuarios, personas y empresas.
// Es lo que faltaba para que la bitácora pudiera auditar "creación de Usuarios, Personas
// o Empresas", que era el único de los 5 puntos mínimos de auditoría del enunciado sin cubrir.
public interface GestorAdministracion {

    void crearUsuario(Usuario actor, String nombre, String email, String password, int rolId)
        throws ErrorSinPermiso, ErrorDatoDuplicado;

    void crearPersona(Usuario actor, String documento, String nombre, String tipoPersona, Integer empresaId)
        throws ErrorSinPermiso, ErrorDatoDuplicado;

    void crearEmpresa(Usuario actor, String nombre, String contactoPrincipal) throws ErrorSinPermiso;
}