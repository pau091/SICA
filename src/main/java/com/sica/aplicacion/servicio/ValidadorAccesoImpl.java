package com.sica.aplicacion.servicio;

// Importación de excepciones
import com.sica.dominio.excepcion.ErrorAutenticacion;
import com.sica.dominio.excepcion.ErrorSinPermiso;

// Importación del modelo
import com.sica.dominio.modelo.Usuario;

// Importaciones corregidas de interfaces e implementaciones
import com.sica.aplicacion.puerto.salida.ConsultaUsuario;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaUsuarioMySQL;
import com.sica.aplicacion.puerto.entrada.RegistradorAuditoria;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;

/**
 * Implementación de la lógica de negocio para la autenticación 
 * y verificación de permisos de usuarios en el sistema.
 */
public class ValidadorAccesoImpl implements ValidadorAcceso {

    // Antes esta línea decía "new ConsultaVisitaMySQL()" (la clase de visitas, no de usuarios),
    // y esa clase ni siquiera estaba importada aquí, así que esto no compilaba.
    // La clase correcta (ConsultaUsuarioMySQL) ya se importaba arriba, pero no se usaba.
    // Ahora sí se crea la clase que realmente busca usuarios en la base de datos.
    private ConsultaUsuario usuarioDAO = new ConsultaUsuarioMySQL();
    
    // Instancia del servicio de auditoría
    private RegistradorAuditoria auditoria = new RegistradorAuditoriaImpl();

    @Override
    public Usuario autenticar(String email, String password) throws ErrorAutenticacion {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario == null || !usuario.getPassword().equals(password)) {
            auditoria.registrarEvento(null, "LOGIN_FALLIDO", "usuarios", null, "Correo: " + email);
            throw new ErrorAutenticacion("Credenciales incorrectas o usuario inactivo.");
        }

        auditoria.registrarEvento(usuario.getId(), "LOGIN_EXITOSO", "usuarios", usuario.getId(), "Inicio de sesión exitoso");
        return usuario;
    }

    @Override
    public void validarPermiso(Usuario usuario, String permisoRequerido) throws ErrorSinPermiso {
        if (usuario == null || !usuario.getRol().tienePermiso(permisoRequerido)) {
            throw new ErrorSinPermiso("Acceso denegado. Se requiere el permiso: " + permisoRequerido);
        }
    }
}