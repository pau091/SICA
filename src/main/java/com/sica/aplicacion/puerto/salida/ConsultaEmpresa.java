package com.sica.aplicacion.puerto.salida;

import com.sica.dominio.modelo.Empresa;

public interface ConsultaEmpresa {
    Empresa buscarPorId(int id);

    // Devuelve el id generado para la nueva empresa.
    int crear(String nombre, String contactoPrincipal);
}