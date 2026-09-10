package com.sica.aplicacion.servicio;

import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.ResumenAcceso;
import com.sica.dominio.modelo.Usuario;
import com.sica.aplicacion.puerto.entrada.GestorDashboard;
import com.sica.aplicacion.puerto.entrada.ValidadorAcceso;
import com.sica.aplicacion.puerto.salida.ConsultaDashboard;
import com.sica.infraestructura.adaptador.salida.mysql.ConsultaDashboardMySQL;

public class GestorDashboardImpl implements GestorDashboard {

    private static final String PERMISO_VER_DASHBOARD = "generar_reporte";

    private final ConsultaDashboard dashboardDAO;
    private final ValidadorAcceso validador;

    public GestorDashboardImpl() {
        this.dashboardDAO = new ConsultaDashboardMySQL();
        this.validador = new ValidadorAccesoImpl();
    }

    public GestorDashboardImpl(ConsultaDashboard dashboardDAO, ValidadorAcceso validador) {
        this.dashboardDAO = dashboardDAO;
        this.validador = validador;
    }

    @Override
    public ResumenAcceso obtenerResumen(Usuario actor) throws ErrorSinPermiso {
        validador.validarPermiso(actor, PERMISO_VER_DASHBOARD);
        return dashboardDAO.obtenerResumen();
    }
}