package com.sica.aplicacion.puerto.salida;

import com.sica.dominio.modelo.Usuario;

//consulta al usuario q quiere ingresar a las empresas
public interface ConsultaUsuario {
   Usuario buscarPorEmail(String email);

   // Devuelve el id generado para el nuevo usuario.
   int crear(String nombre, String email, String password, int rolId);

   void actualizar(int usuarioId, String nombre, String password, int rolId);

   // "Eliminar" un usuario es desactivarlo (esta_activo = FALSE), no borrar la fila: un usuario
   // puede tener historial en la bitácora, en visitas aprobadas o en incidentes reportados, y
   // borrarlo de verdad rompería esas referencias — justo lo que un sistema de auditoría no
   // se puede permitir. Un usuario desactivado ya no puede loguearse (ver buscarPorEmail).
   void desactivar(int usuarioId);
}