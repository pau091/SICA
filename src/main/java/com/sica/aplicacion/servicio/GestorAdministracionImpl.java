package com.sica.aplicacion.servicio;

import com.sica.dominio.excepcion.ErrorDatoDuplicado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
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

    private static final String PERMISO_CREAR_USUARIO = "crear_usuario";
    private static final String PERMISO_CREAR_PERSONA = "crear_persona";
    private static final String PERMISO_CREAR_EMPRESA = "crear_empresa";

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

    @Override
    public void crearUsuario(Usuario actor, String nombre, String email, String password, int rolId)
            throws ErrorSinPermiso, ErrorDatoDuplicado {
        validador.validarPermiso(actor, PERMISO_CREAR_USUARIO);
        if (usuarioDAO.buscarPorEmail(email) != null) {
            throw new ErrorDatoDuplicado("Ya existe un usuario registrado con ese correo.");
        }
        int nuevoId = usuarioDAO.crear(nombre, email, password, rolId);
        auditoria.registrarEvento(actor.getId(), "USUARIO_CREADO", "usuarios", nuevoId,
            "Usuario creado: " + nombre + " (" + email + ").");
    }

    @Override
    public void crearPersona(Usuario actor, String documento, String nombre, String tipoPersona, Integer empresaId)
            throws ErrorSinPermiso, ErrorDatoDuplicado {
        validador.validarPermiso(actor, PERMISO_CREAR_PERSONA);
        if (personaDAO.buscarPorDocumento(documento) != null) {
            throw new ErrorDatoDuplicado("Ya existe una persona registrada con ese documento.");
        }
        int nuevoId = personaDAO.crear(documento, nombre, tipoPersona, empresaId);
        auditoria.registrarEvento(actor.getId(), "PERSONA_CREADA", "personas", nuevoId,
            "Persona creada: " + nombre + " (" + documento + ").");
    }

    @Override
    public void crearEmpresa(Usuario actor, String nombre, String contactoPrincipal) throws ErrorSinPermiso {
        validador.validarPermiso(actor, PERMISO_CREAR_EMPRESA);
        int nuevoId = empresaDAO.crear(nombre, contactoPrincipal);
        auditoria.registrarEvento(actor.getId(), "EMPRESA_CREADA", "empresas", nuevoId,
            "Empresa creada: " + nombre + ".");
    }
}