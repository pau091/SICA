package com.sica.infraestructura.adaptador.entrada.controlador;

import com.sica.dominio.excepcion.ErrorAutenticacion;
import com.sica.dominio.modelo.Usuario;
import com.sica.aplicacion.servicio.ValidadorAccesoImpl;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.infraestructura.config.SesionUsuario;
public class GestorSesion {
    private ValidadorAcceso validador = new ValidadorAccesoImpl ();

    public boolean iniciarSesion(String email, String password) throws ErrorAutenticacion {
        // "autenticar" nunca devuelve null: si el login está mal, lanza ErrorAutenticacion (revisa
        // ValidadorAccesoImpl). Por eso el "if" de aquí abajo nunca podía ser falso, y por eso antes
        // esta función decía "return true" en los dos caminos (el de adentro del if y el de afuera):
        // daba igual lo que pasara, siempre iba a devolver true. Como ya no hace falta el "if",
        // lo quitamos y dejamos solo lo que realmente pasa: si no hubo excepción, el login sí sirvió.
        Usuario usuario = validador.autenticar(email, password);
        SesionUsuario.getInstancia().iniciarSesion(usuario);
        return true;
    }
    public void cerrarSesion(){
        SesionUsuario.getInstancia().cerrarSesion();
    }
}