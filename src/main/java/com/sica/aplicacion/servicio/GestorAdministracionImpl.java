package com.sica.aplicacion.servicio;

import com.sica.dominio.excepcion.ErrorDatoDuplicado;
import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorOperacionNoPermitida;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Persona;
import com.sica.dominio.modelo.Usuario;
import com.sica.aplicacion.puerto.entrada.GestorAdministracion;
import com.sica.aplicacion.puerto.entrada.RegistradorAuditoria;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.aplicacion.puerto.salida.ConsultaEmpresa;
import com.sica.aplicacion.puerto.salida.ConsultaPersona;
import com.sica.aplicacion.puerto.salida.ConsultaUsuario;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaEmpresaMySQL;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaPersonaMySQL;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaUsuarioMySQL;

public class GestorAdministracionImpl implements GestorAdministracion {

    // Un solo permiso por entidad gobierna todo su ciclo de vida (crear/actualizar/eliminar).
    // No se creó un permiso distinto por cada operación para no inflar la tabla de permisos
    // con variantes casi idénticas; quien puede crear una entidad, puede administrarla.
    private static final String PERMISO_USUARIO = "crear_usuario";
    private static final String PERMISO_PERSONA = "crear_persona";
    private static final String PERMISO_EMPRESA = "crear_empresa";

    private final ConsultaUsuario usuarioDAO;
    private final ConsultaPersona personaDAO;
    private final ConsultaEmpresa empresaDAO;
    private final ValidadorAcceso validador;
    private final RegistradorAuditoria auditoria;

    public GestorAdministracionImpl() {
        this.usuarioDAO = new ConsultaUsuarioMySQL();
        this.personaDAO = new ConsultaPersonaMySQL();
        this.empresaDAO = new ConsultaEmpresaMySQL();
        this.validador = new ValidadorAccesoImpl();
        this.auditoria = new RegistradorAuditoriaImpl();
    }

    public GestorAdministracionImpl(ConsultaUsuario usuarioDAO, ConsultaPersona personaDAO, ConsultaEmpresa empresaDAO,
                                     ValidadorAcceso validador, RegistradorAuditoria auditoria) {
        this.usuarioDAO = usuarioDAO;
        this.personaDAO = personaDAO;
        this.empresaDAO = empresaDAO;
        this.validador = validador;
        this.auditoria = auditoria;
    }

    // ---------- Usuarios ----------

    @Override
    public void crearUsuario(Usuario actor, String nombre, String email, String password, int rolId)
            throws ErrorSinPermiso, ErrorDatoDuplicado {
        validador.validarPermiso(actor, PERMISO_USUARIO);
        if (usuarioDAO.buscarPorEmail(email) != null) {
            throw new ErrorDatoDuplicado("Ya existe un usuario registrado con ese correo.");
        }
        int nuevoId = usuarioDAO.crear(nombre, email, password, rolId);
        auditoria.registrarEvento(actor.getId(), "USUARIO_CREADO", "usuarios", nuevoId,
            "Usuario creado: " + nombre + " (" + email + ").");
    }

    @Override
    public void actualizarUsuario(Usuario actor, String email, String nuevoNombre, String nuevaPassword, int nuevoRolId)
            throws ErrorSinPermiso, ErrorNoEncontrado {
        validador.validarPermiso(actor, PERMISO_USUARIO);
        Usuario existente = buscarUsuarioOLanzar(email);
        usuarioDAO.actualizar(existente.getId(), nuevoNombre, nuevaPassword, nuevoRolId);
        auditoria.registrarEvento(actor.getId(), "USUARIO_ACTUALIZADO", "usuarios", existente.getId(),
            "Usuario actualizado: " + email + ".");
    }

    @Override
    public void eliminarUsuario(Usuario actor, String email) throws ErrorSinPermiso, ErrorNoEncontrado {
        validador.validarPermiso(actor, PERMISO_USUARIO);
        Usuario existente = buscarUsuarioOLanzar(email);
        // No se borra la fila: puede tener historial en la bitácora, en visitas aprobadas o
        // en incidentes reportados, y borrarla de verdad rompería esas referencias. Desactivar
        // (esta_activo = FALSE) ya alcanza para que no pueda volver a loguearse.
        usuarioDAO.desactivar(existente.getId());
        auditoria.registrarEvento(actor.getId(), "USUARIO_ELIMINADO", "usuarios", existente.getId(),
            "Usuario desactivado: " + email + ".");
    }

    private Usuario buscarUsuarioOLanzar(String email) throws ErrorNoEncontrado {
        Usuario u = usuarioDAO.buscarPorEmail(email);
        if (u == null) {
            throw new ErrorNoEncontrado("No existe un usuario activo con ese correo.");
        }
        return u;
    }

    // ---------- Personas ----------

    @Override
    public void crearPersona(Usuario actor, String documento, String nombre, String tipoPersona, Integer empresaId)
            throws ErrorSinPermiso, ErrorDatoDuplicado {
        validador.validarPermiso(actor, PERMISO_PERSONA);
        if (personaDAO.buscarPorDocumento(documento) != null) {
            throw new ErrorDatoDuplicado("Ya existe una persona registrada con ese documento.");
        }
        int nuevoId = personaDAO.crear(documento, nombre, tipoPersona, empresaId);
        auditoria.registrarEvento(actor.getId(), "PERSONA_CREADA", "personas", nuevoId,
            "Persona creada: " + nombre + " (" + documento + ").");
    }

    @Override
    public void actualizarPersona(Usuario actor, String documento, String nuevoNombre, String nuevoTipo, Integer nuevaEmpresaId)
            throws ErrorSinPermiso, ErrorNoEncontrado {
        validador.validarPermiso(actor, PERMISO_PERSONA);
        Persona existente = buscarPersonaOLanzar(documento);
        personaDAO.actualizar(existente.getId(), nuevoNombre, nuevoTipo, nuevaEmpresaId);
        auditoria.registrarEvento(actor.getId(), "PERSONA_ACTUALIZADA", "personas", existente.getId(),
            "Persona actualizada: " + documento + ".");
    }

    @Override
    public void eliminarPersona(Usuario actor, String documento)
            throws ErrorSinPermiso, ErrorNoEncontrado, ErrorOperacionNoPermitida {
        validador.validarPermiso(actor, PERMISO_PERSONA);
        Persona existente = buscarPersonaOLanzar(documento);
        boolean eliminada = personaDAO.eliminar(existente.getId());
        if (!eliminada) {
            throw new ErrorOperacionNoPermitida(
                "No se pudo eliminar: esta persona tiene visitas registradas asociadas.");
        }
        auditoria.registrarEvento(actor.getId(), "PERSONA_ELIMINADA", "personas", existente.getId(),
            "Persona eliminada: " + documento + ".");
    }

    private Persona buscarPersonaOLanzar(String documento) throws ErrorNoEncontrado {
        Persona p = personaDAO.buscarPorDocumento(documento);
        if (p == null) {
            throw new ErrorNoEncontrado("No existe ninguna persona registrada con ese documento.");
        }
        return p;
    }

    // ---------- Empresas ----------

    @Override
    public int crearEmpresa(Usuario actor, String nombre, String contactoPrincipal) throws ErrorSinPermiso {
        validador.validarPermiso(actor, PERMISO_EMPRESA);
        int nuevoId = empresaDAO.crear(nombre, contactoPrincipal);
        auditoria.registrarEvento(actor.getId(), "EMPRESA_CREADA", "empresas", nuevoId,
            "Empresa creada: " + nombre + ".");
        return nuevoId;
    }

    @Override
    public void actualizarEmpresa(Usuario actor, int empresaId, String nuevoNombre, String nuevoContacto)
            throws ErrorSinPermiso, ErrorNoEncontrado {
        validador.validarPermiso(actor, PERMISO_EMPRESA);
        if (empresaDAO.buscarPorId(empresaId) == null) {
            throw new ErrorNoEncontrado("No existe ninguna empresa con ese ID.");
        }
        empresaDAO.actualizar(empresaId, nuevoNombre, nuevoContacto);
        auditoria.registrarEvento(actor.getId(), "EMPRESA_ACTUALIZADA", "empresas", empresaId,
            "Empresa actualizada: ID " + empresaId + ".");
    }

    @Override
    public void eliminarEmpresa(Usuario actor, int empresaId)
            throws ErrorSinPermiso, ErrorNoEncontrado, ErrorOperacionNoPermitida {
        validador.validarPermiso(actor, PERMISO_EMPRESA);
        if (empresaDAO.buscarPorId(empresaId) == null) {
            throw new ErrorNoEncontrado("No existe ninguna empresa con ese ID.");
        }
        boolean eliminada = empresaDAO.eliminar(empresaId);
        if (!eliminada) {
            throw new ErrorOperacionNoPermitida(
                "No se pudo eliminar: esta empresa tiene personas asociadas.");
        }
        auditoria.registrarEvento(actor.getId(), "EMPRESA_ELIMINADA", "empresas", empresaId,
            "Empresa eliminada: ID " + empresaId + ".");
    }
}