package com.sica.dominio.modelo;


import java.time.LocalDateTime;

//registrar de forma transparente y cronológica todas las acciones 
// de los usuarios (como inicios de sesión o registros de entrada

public class Bitacora {
    private int id;
    private Integer usuarioId;
    private String accionRealizada;
    private String tablaAfectada;
    private Integer registroIdAfectado;
    private String detalles;
    private LocalDateTime fecha;

    public Bitacora(int id, Integer usuarioId, String accionRealizada, String tablaAfectada, Integer registroIdAfectado, String detalles, LocalDateTime fecha){
        this.id = id;
        this.usuarioId = usuarioId;
        this.accionRealizada = accionRealizada;
        this.tablaAfectada = tablaAfectada;
        this.registroIdAfectado = registroIdAfectado;
        this.detalles = detalles;
        this.fecha = fecha;

    }
    public int getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public String getAccionRealizada() { return accionRealizada; }
    public String getTablaAfectada() { return tablaAfectada; }
    public Integer getRegistroIdAfectado() { return registroIdAfectado; }
    public String getDetalles() { return detalles; }
    public LocalDateTime getFecha() { return fecha; }



}