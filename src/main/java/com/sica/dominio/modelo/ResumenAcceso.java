package com.sica.dominio.modelo;

// DTO de solo lectura para el panel de indicadores: no se persiste, solo agrupa los conteos
// que ya se pueden calcular con las tablas existentes.
public class ResumenAcceso {
    private final int personasDentro;
    private final int visitasPendientes;
    private final int incidentesHoy;
    private final int personasBloqueadas;
    private final int empresasRegistradas;

    public ResumenAcceso(int personasDentro, int visitasPendientes, int incidentesHoy,
                          int personasBloqueadas, int empresasRegistradas) {
        this.personasDentro = personasDentro;
        this.visitasPendientes = visitasPendientes;
        this.incidentesHoy = incidentesHoy;
        this.personasBloqueadas = personasBloqueadas;
        this.empresasRegistradas = empresasRegistradas;
    }

    public int getPersonasDentro() { return personasDentro; }
    public int getVisitasPendientes() { return visitasPendientes; }
    public int getIncidentesHoy() { return incidentesHoy; }
    public int getPersonasBloqueadas() { return personasBloqueadas; }
    public int getEmpresasRegistradas() { return empresasRegistradas; }
}
