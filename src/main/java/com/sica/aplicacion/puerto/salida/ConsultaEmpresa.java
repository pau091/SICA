package com.sica.aplicacion.puerto.salida;

import com.sica.dominio.modelo.Empresa;

public interface ConsultaEmpresa {
    Empresa buscarPorId(int id);

    // Devuelve el id generado para la nueva empresa.
    int crear(String nombre, String contactoPrincipal);

    void actualizar(int empresaId, String nombre, String contactoPrincipal);

    // Devuelve false si no se pudo borrar (por ejemplo, la empresa todavía tiene personas
    // asociadas) — no lanza excepción para dejar esa decisión al core.
    boolean eliminar(int empresaId);
}