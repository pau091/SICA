package com.sica.aplicacion.servicio;

import com.sica.aplicacion.puerto.entrada.GestorReporteOcupacion;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.aplicacion.puerto.salida.ConsultaVisita;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.FranjaOcupacion;
import com.sica.dominio.modelo.Usuario;
import com.sica.dominio.modelo.Visita;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaVisitaMySQL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestorReporteOcupacionImpl implements GestorReporteOcupacion {

    private static final String PERMISO_VER_REPORTE = "generar_reporte";
    private static final int HORAS_DIA = 24;

    private final ConsultaVisita visitaDAO;
    private final ValidadorAcceso validador;

    public GestorReporteOcupacionImpl() {
        this.visitaDAO = new ConsultaVisitaMySQL();
        this.validador = new ValidadorAccesoImpl();
    }

    public GestorReporteOcupacionImpl(ConsultaVisita visitaDAO, ValidadorAcceso validador) {
        this.visitaDAO = visitaDAO;
        this.validador = validador;
    }

    @Override
    public List<FranjaOcupacion> generarReporte(Usuario actor, LocalDate fecha) throws ErrorSinPermiso {
        validador.validarPermiso(actor, PERMISO_VER_REPORTE);

        // Una sola consulta a la BD; el análisis hora por hora se hace en memoria con Streams,
        // en vez de golpear la base de datos 24 veces.
        List<Visita> visitasDelDia = visitaDAO.listarVisitasDelDia(fecha);

        List<FranjaOcupacion> reporte = new ArrayList<>(HORAS_DIA);
        for (int hora = 0; hora < HORAS_DIA; hora++) {
            LocalDateTime inicioFranja = fecha.atTime(hora, 0);
            LocalDateTime finFranja = inicioFranja.plusHours(1);

            long cantidad = visitasDelDia.stream()
                .filter(v -> seSuperponeConFranja(v, inicioFranja, finFranja))
                .count();

            reporte.add(new FranjaOcupacion(hora, cantidad));
        }
        return reporte;
    }

    // Una visita se superpone con [inicioFranja, finFranja) si entró antes de que la franja
    // termine, y (todavía no salió, o salió después de que la franja empezara). Cubre: visita
    // dentro de una sola hora, visita que abarca varias horas, visita que empezó antes del día
    // analizado, y visita aún sin fecha_salida.
    private boolean seSuperponeConFranja(Visita v, LocalDateTime inicioFranja, LocalDateTime finFranja) {
        LocalDateTime entrada = v.getFechaIngreso();
        if (entrada == null) return false;
        LocalDateTime salida = v.getFechaSalida();
        boolean entroAntesDeQueTermineLaFranja = entrada.isBefore(finFranja);
        boolean sigueDentroOSalioDespuesDeQueEmpezara = (salida == null) || salida.isAfter(inicioFranja);
        return entroAntesDeQueTermineLaFranja && sigueDentroOSalioDespuesDeQueEmpezara;
    }
}