package com.sica.aplicacion.puerto.salida;

import com.sica.dominio.modelo.Usuario;

//consulta al usuario q quiere ingresar a las empresas
public interface ConsultaUsuario {
   Usuario buscarPorEmail(String email);

   // Devuelve el id generado para el nuevo usuario.
   int crear(String nombre, String email, String password, int rolId);
}