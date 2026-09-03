package com.sica.aplicacion.servicio;

import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Persona;
import com.sica.dominio.modelo.Usuario;
import com.sica.dominio.modelo.Visita;
import com.sica.aplicacion.puerto.entrada.GestorAprobaciones;
import com.sica.aplicacion.puerto.entrada.RegistradorAuditoria;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.aplicacion.puerto.salida.ConsultaPersona;
import com.sica.aplicacion.puerto.salida.ConsultaVisita;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaPersonaMySQL;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaVisitaMySQL;

import java.util.List;

public class GestorAprobacionesImpl implements GestorAprobaciones {

    // El enunciado no tiene un permiso separado para "pre-registrar", asÃ­ que usamos el mismo
    // permiso que para aprobar/rechazar: pre-registrar es, en el fondo, aprobar por adelantado.
    private static final String PERMISO_APROBAR_VISITA = "aprobar_visita";

    private final ConsultaVisita visitaDAO;
    private final ConsultaPersona personaDAO;
    private final ValidadorAcceso validador;
    private final RegistradorAuditoria auditoria;

    public GestorAprobacionesImpl() {
        this.visitaDAO = new ConsultaVisitaMySQL();
        this.personaDAO = new ConsultaPersonaMySQL();
        this.validador = new ValidadorAccesoImpl();
        this.auditoria = new RegistradorAuditoriaImpl();
    }

    public GestorAprobacionesImpl(ConsultaVisita visitaDAO, ConsultaPersona personaDAO,
                                   ValidadorAcceso validador, RegistradorAuditoria auditoria) {
        this.visitaDAO = visitaDAO;
        this.personaDAO = personaDAO;
        this.validador = validador;
        this.auditoria = auditoria;
    }

    @Override
    public List<Visita> listarPendientes(Usuario actor) throws ErrorSinPermiso {
        validador.validarPermiso(actor, PERMISO_APROBAR_VISITA);
        return visitaDAO.listarPendientesDeAprobacion();
    }

    @Override
    public void aprobar(Usuario actor, int visitaId) throws ErrorSinPermiso {
        validador.validarPermiso(actor, PERMISO_APROBAR_VISITA);
        visitaDAO.resolverAprobacion(visitaId, true, actor.getId());
        auditoria.registrarEvento(actor.getId(), "VISITA_APROBADA", "visitas", visitaId,
            "Visita aprobada por " + actor.getNombre() + ".");
    }

    @Override
    public void rechazar(Usuario actor, int visitaId) throws ErrorSinPermiso {
        validador.validarPermiso(actor, PERMISO_APROBAR_VISITA);
        visitaDAO.resolverAprobacion(visitaId, false, actor.getId());
        auditoria.registrarEvento(actor.getId(), "VISITA_RECHAZADA", "visitas", visitaId,
            "Visita rechazada por " + actor.getNombre() + ".");
    }

    @Override
    public void preRegistrarVisita(Usuario actor, String documentoPersona) throws ErrorSinPermiso, ErrorNoEncontrado {
        validador.validarPermiso(actor, PERMISO_APROBAR_VISITA);
        Persona persona = personaDAO.buscarPorDocumento(documentoPersona);
        if (persona == null) {
            throw new ErrorNoEncontrado("No existe ninguna persona registrada con ese documento.");
        }
        visitaDAO.preRegistrarVisita(persona.getId(), actor.getId());
        auditoria.registrarEvento(actor.getId(), "VISITA_PREREGISTRADA", "visitas", null,
            "Pre-registro de visita para " + persona.getNombre() + ", hecho por " + actor.getNombre() + ".");
    }
}