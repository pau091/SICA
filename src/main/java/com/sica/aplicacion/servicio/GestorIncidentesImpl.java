package com.sica.aplicacion.servicio;

import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Persona;
import com.sica.dominio.modelo.Usuario;
import com.sica.dominio.modelo.Visita;
import com.sica.aplicacion.puerto.entrada.GestorIncidentes;
import com.sica.aplicacion.puerto.entrada.RegistradorAuditoria;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.aplicacion.puerto.salida.ConsultaIncidente;
import com.sica.aplicacion.puerto.salida.ConsultaPersona;
import com.sica.aplicacion.puerto.salida.ConsultaVisita;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaIncidenteMySQL;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaPersonaMySQL;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaVisitaMySQL;

public class GestorIncidentesImpl implements GestorIncidentes {

    private static final String PERMISO_REPORTAR_INCIDENTE = "reportar_incidente";
    private static final String PERMISO_BLOQUEAR_PERSONA = "bloquear_persona";

    private final ConsultaPersona personaDAO;
    private final ConsultaVisita visitaDAO;
    private final ConsultaIncidente incidenteDAO;
    private final ValidadorAcceso validador;
    private final RegistradorAuditoria auditoria;

    public GestorIncidentesImpl() {
        this.personaDAO = new ConsultaPersonaMySQL();
        this.visitaDAO = new ConsultaVisitaMySQL();
        this.incidenteDAO = new ConsultaIncidenteMySQL();
        this.validador = new ValidadorAccesoImpl();
        this.auditoria = new RegistradorAuditoriaImpl();
    }

    public GestorIncidentesImpl(ConsultaPersona personaDAO, ConsultaVisita visitaDAO, ConsultaIncidente incidenteDAO,
                                 ValidadorAcceso validador, RegistradorAuditoria auditoria) {
        this.personaDAO = personaDAO;
        this.visitaDAO = visitaDAO;
        this.incidenteDAO = incidenteDAO;
        this.validador = validador;
        this.auditoria = auditoria;
    }

    @Override
    public void reportarIncidente(Usuario actor, String documentoPersona, String descripcion)
            throws ErrorSinPermiso, ErrorNoEncontrado {
        validador.validarPermiso(actor, PERMISO_REPORTAR_INCIDENTE);
        Persona persona = buscarPersonaOLanzar(documentoPersona);

        // Si la persona está adentro en este momento, el incidente queda ligado a esa visita.
        // Si no, igual se guarda: la tabla "incidentes" permite que visita_id quede vacío.
        Visita visitaActiva = visitaDAO.buscarVisitaActiva(persona.getId());
        Integer visitaId = (visitaActiva != null) ? visitaActiva.getId() : null;

        incidenteDAO.registrarIncidente(visitaId, descripcion, actor.getId());
        auditoria.registrarEvento(actor.getId(), "INCIDENTE_REGISTRADO", "incidentes", visitaId,
            "Incidente reportado sobre " + persona.getNombre() + ": " + descripcion);
    }

    @Override
    public void bloquearPersona(Usuario actor, String documentoPersona) throws ErrorSinPermiso, ErrorNoEncontrado {
        validador.validarPermiso(actor, PERMISO_BLOQUEAR_PERSONA);
        Persona persona = buscarPersonaOLanzar(documentoPersona);
        personaDAO.bloquear(persona.getId());
        auditoria.registrarEvento(actor.getId(), "PERSONA_BLOQUEADA", "personas", persona.getId(),
            "Se restringió el acceso de " + persona.getNombre() + ".");
    }

    @Override
    public void desbloquearPersona(Usuario actor, String documentoPersona) throws ErrorSinPermiso, ErrorNoEncontrado {
        validador.validarPermiso(actor, PERMISO_BLOQUEAR_PERSONA);
        Persona persona = buscarPersonaOLanzar(documentoPersona);
        personaDAO.desbloquear(persona.getId());
        auditoria.registrarEvento(actor.getId(), "PERSONA_DESBLOQUEADA", "personas", persona.getId(),
            "Se restauró el acceso de " + persona.getNombre() + ".");
    }

    private Persona buscarPersonaOLanzar(String documento) throws ErrorNoEncontrado {
        Persona p = personaDAO.buscarPorDocumento(documento);
        if (p == null) {
            throw new ErrorNoEncontrado("No existe ninguna persona registrada con ese documento.");
        }
        return p;
    }
}