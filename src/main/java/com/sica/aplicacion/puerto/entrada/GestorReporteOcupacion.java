package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.FranjaOcupacion;
import com.sica.dominio.modelo.Usuario;

import java.time.LocalDate;
import java.util.List;

// Caso de uso de solo lectura: reutiliza el mismo permiso que ya exige "Ver Reportes"
// (generar_reporte), igual patrón que GestorDashboard.
public interface GestorReporteOcupacion {
    List<FranjaOcupacion> generarReporte(Usuario actor, LocalDate fecha) throws ErrorSinPermiso;
}