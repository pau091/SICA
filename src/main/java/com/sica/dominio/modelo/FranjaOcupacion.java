package com.sica.dominio.modelo;

public class FranjaOcupacion {
    private final int horaInicio; // 0-23
    private final long cantidadPersonas;

    public FranjaOcupacion(int horaInicio, long cantidadPersonas) {
        this.horaInicio = horaInicio;
        this.cantidadPersonas = cantidadPersonas;
    }

    public int getHoraInicio() { return horaInicio; }
    public long getCantidadPersonas() { return cantidadPersonas; }
}