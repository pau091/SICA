package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.excepcion.ErrorSinPermiso;
import com.sica.dominio.modelo.ResumenAcceso;
import com.sica.dominio.modelo.Usuario;

// Caso de uso de solo lectura: reutiliza el mismo permiso que ya exige "Ver Reportes"
// (generar_reporte), porque un dashboard no es más que otra forma de ver ese mismo reporte.
public interface GestorDashboard {
    ResumenAcceso obtenerResumen(Usuario actor) throws ErrorSinPermiso;
}