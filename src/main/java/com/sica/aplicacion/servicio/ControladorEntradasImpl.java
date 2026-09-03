package com.sica.aplicacion.servicio;

import com.sica.dominio.excepcion.ErrorAccesoBloqueado;
import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Persona;
import com.sica.dominio.modelo.Usuario;
import com.sica.dominio.modelo.Visita;
import com.sica.aplicacion.puerto.salida.ConsultaPersona;
import com.sica.aplicacion.puerto.salida.ConsultaVisita;
import com.sica.aplicacion.puerto.entrada.ControladorEntradas;
import com.sica.aplicacion.puerto.entrada.RegistradorAuditoria;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaPersonaMySQL;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaVisitaMySQL;

public class ControladorEntradasImpl implements ControladorEntradas {

    private static final String PERMISO_REGISTRAR_VISITA = "registrar_visita";

    // Estos textos deben ser IDÉNTICOS a los que trae la tabla visita_estados (ver data.sql).
    private static final String ESTADO_PENDIENTE = "Pendiente de Aprobacion";
    private static final String ESTADO_APROBADO = "Aprobado";
    private static final String ESTADO_RECHAZADO = "Rechazado";

    // Este debe ser IDÉNTICO al de la tabla persona_estados_acceso (ver data.sql).
    private static final String ESTADO_ACCESO_BLOQUEADO = "Con Prohibicion de Ingreso";

    private final ConsultaPersona personaDAO;
    private final ConsultaVisita visitaDAO;
    private final ValidadorAcceso validador;
    private final RegistradorAuditoria auditoria;

    public ControladorEntradasImpl() {
        this.personaDAO = new ConsultaPersonaMySQL();
        this.visitaDAO = new ConsultaVisitaMySQL();
        this.validador = new ValidadorAccesoImpl();
        this.auditoria = new RegistradorAuditoriaImpl();
    }

    public ControladorEntradasImpl(ConsultaPersona personaDAO, ConsultaVisita visitaDAO,
                                    ValidadorAcceso validador, RegistradorAuditoria auditoria) {
        this.personaDAO = personaDAO;
        this.visitaDAO = visitaDAO;
        this.validador = validador;
        this.auditoria = auditoria;
    }

    @Override
    public void procesarIngreso(Usuario actor, String documento)
            throws ErrorNoEncontrado, ErrorSinPermiso, ErrorAccesoBloqueado {
        validador.validarPermiso(actor, PERMISO_REGISTRAR_VISITA);
        Persona p = buscarPersonaOLanzar(documento);
        rechazarSiEstaBloqueada(p);

        cerrarSiQuedoAbiertaDeAntes(p);

        Visita visitaDeHoy = visitaDAO.buscarVisitaDeHoyPorPersona(p.getId());

        if (visitaDeHoy != null && ESTADO_APROBADO.equals(visitaDeHoy.getEstado())) {
            // Esta visita ya estaba autorizada (pre-registro del Funcionario, o una que ya
            // había sido aprobada antes). Aquí sí pasa a ser un ingreso real.
            visitaDAO.marcarCheckIn(visitaDeHoy.getId());
            auditoria.registrarEvento(actor.getId(), "CHECK_IN", "visitas", visitaDeHoy.getId(),
                "Ingreso de " + p.getNombre() + " (visita ya autorizada).");
            return;
        }
        if (visitaDeHoy != null && ESTADO_PENDIENTE.equals(visitaDeHoy.getEstado())) {
            throw new ErrorNoEncontrado("Esta visita sigue pendiente de aprobación del funcionario.");
        }
        if (visitaDeHoy != null && ESTADO_RECHAZADO.equals(visitaDeHoy.getEstado())) {
            throw new ErrorNoEncontrado("El ingreso de esta persona fue rechazado hoy por un funcionario.");
        }

        // No había nada especial pendiente hoy: es un ingreso normal y directo.
        visitaDAO.registrarIngreso(p.getId());
        auditoria.registrarEvento(actor.getId(), "CHECK_IN", "visitas", null,
            "Ingreso directo de " + p.getNombre() + ".");
    }

    @Override
    public void procesarIngresoEspecial(Usuario actor, String documento)
            throws ErrorNoEncontrado, ErrorSinPermiso, ErrorAccesoBloqueado {
        validador.validarPermiso(actor, PERMISO_REGISTRAR_VISITA);
        Persona p = buscarPersonaOLanzar(documento);
        rechazarSiEstaBloqueada(p);

        cerrarSiQuedoAbiertaDeAntes(p);

        Visita visitaDeHoy = visitaDAO.buscarVisitaDeHoyPorPersona(p.getId());
        if (visitaDeHoy != null) {
            throw new ErrorNoEncontrado(
                "Ya existe un registro de hoy para esta persona (estado: " + visitaDeHoy.getEstado() + ").");
        }

        visitaDAO.crearVisitaPendiente(p.getId());
        auditoria.registrarEvento(actor.getId(), "INGRESO_PENDIENTE", "visitas", null,
            "Ingreso especial (invitado no anunciado o carnet olvidado) para " + p.getNombre() + ".");
    }

    @Override
    public void procesarSalida(Usuario actor, String documento) throws ErrorNoEncontrado, ErrorSinPermiso {
        validador.validarPermiso(actor, PERMISO_REGISTRAR_VISITA);
        Persona p = buscarPersonaOLanzar(documento);

        Visita v = visitaDAO.buscarVisitaActiva(p.getId());
        if (v == null) {
            throw new ErrorNoEncontrado("La persona no tiene un ingreso activo registrado.");
        }
        visitaDAO.registrarSalida(v.getId());
        auditoria.registrarEvento(actor.getId(), "CHECK_OUT", "visitas", v.getId(),
            "Salida de " + p.getNombre() + ".");
    }

    private Persona buscarPersonaOLanzar(String documento) throws ErrorNoEncontrado {
        Persona p = personaDAO.buscarPorDocumento(documento);
        if (p == null) {
            throw new ErrorNoEncontrado("Persona no registrada en el sistema.");
        }
        return p;
    }

    // El Supervisor de Seguridad puede restringir el acceso de alguien (ver GestorIncidentesImpl).
    // Esta es la otra mitad de esa función: que el ingreso de verdad la respete.
    // No se aplica en procesarSalida a propósito: a alguien bloqueado no se le impide salir.
    private void rechazarSiEstaBloqueada(Persona p) throws ErrorAccesoBloqueado {
        if (ESTADO_ACCESO_BLOQUEADO.equals(p.getEstadoAcceso())) {
            throw new ErrorAccesoBloqueado(
                p.getNombre() + " tiene el acceso restringido y no puede ingresar.");
        }
    }

    // Flujo de "salida olvidada" del enunciado: si esta persona ya tenía una visita en estado
    // "Dentro" (o sea, nunca marcó salida la última vez), el sistema la cierra solo, dejando
    // constancia en la bitácora, y deja seguir con el ingreso de ahora sin bloquear a nadie.
    private void cerrarSiQuedoAbiertaDeAntes(Persona p) {
        Visita abierta = visitaDAO.buscarVisitaActiva(p.getId());
        if (abierta != null) {
            visitaDAO.cerrarPorSistema(abierta.getId());
            auditoria.registrarEvento(null, "CIERRE_AUTOMATICO_SALIDA_OLVIDADA", "visitas", abierta.getId(),
                "Se cerró automáticamente una visita de " + p.getNombre() + " que había quedado abierta.");
        }
    }
}