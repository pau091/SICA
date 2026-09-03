package com.sica.aplicacion.servicio;

import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.Usuario;
import com.sica.aplicacion.puerto.entrada.GestorMantenimiento;
import com.sica.aplicacion.puerto.entrada.RegistradorAuditoria;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.aplicacion.puerto.salida.MantenimientoDatos;
import com.sica.infraestructura.adaptador.salida.mysql.MantenimientoDatosMySQL;

public class GestorMantenimientoImpl implements GestorMantenimiento {

    private static final String PERMISO_RESETEAR_DATOS = "resetear_datos_prueba";

    private final MantenimientoDatos mantenimientoDAO;
    private final ValidadorAcceso validador;
    private final RegistradorAuditoria auditoria;

    public GestorMantenimientoImpl() {
        this.mantenimientoDAO = new MantenimientoDatosMySQL();
        this.validador = new ValidadorAccesoImpl();
        this.auditoria = new RegistradorAuditoriaImpl();
    }

    public GestorMantenimientoImpl(MantenimientoDatos mantenimientoDAO, ValidadorAcceso validador,
                                    RegistradorAuditoria auditoria) {
        this.mantenimientoDAO = mantenimientoDAO;
        this.validador = validador;
        this.auditoria = auditoria;
    }

    @Override
    public void limpiarDatosDePrueba(Usuario actor) throws ErrorSinPermiso {
        validador.validarPermiso(actor, PERMISO_RESETEAR_DATOS);
        mantenimientoDAO.limpiarDatosDePrueba();
        // Se registra DESPUÉS de limpiar (si se hiciera antes, este mismo evento se borraría solo).
        auditoria.registrarEvento(actor.getId(), "RESET_DATOS_PRUEBA", null, null,
            "Se limpiaron visitas, incidentes y la bitácora anterior; se desbloquearon todas las personas.");
    }
}