package com.sica.aplicacion.puerto.entrada;

import com.sica.dominio.modelo.Empresa;

public interface ConsultaEmpresa {
    Empresa buscarPorId(int id);

}