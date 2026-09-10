package com.sica.infraestructura.config;

import com.sica.aplicacion.servicio.*;
import com.sica.aplicacion.puerto.entrada.*;
//se retornan las instancias (Una instancia es la creación real en memoria de un objeto basado en las instrucciones de una clase.)
//instancia --> NEW 

public class FabricaServicios {
    public static ValidadorAcceso crearValidadorAcceso(){return new ValidadorAccesoImpl(); }
    public static ControladorEntradas crearControladorEntradas() {return new ControladorEntradasImpl();}
    public static RegistradorAuditoria crearRegistradorAuditoria() {return new RegistradorAuditoriaImpl();}
    public static GestorAprobaciones crearGestorAprobaciones() {return new GestorAprobacionesImpl();}
    public static GestorIncidentes crearGestorIncidentes() {return new GestorIncidentesImpl();}
    public static GestorMantenimiento crearGestorMantenimiento() {return new GestorMantenimientoImpl();}
    public static GestorAdministracion crearGestorAdministracion() {return new GestorAdministracionImpl();}
    public static GestorDashboard crearGestorDashboard() {return new GestorDashboardImpl(); }
    public static GestorReporteOcupacion crearGestorReporteOcupacion() {return new GestorReporteOcupacionImpl(); }
}