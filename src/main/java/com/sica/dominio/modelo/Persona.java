package com.sica.dominio.modelo;

public class Persona {
    private int id;
    private String documento;
    private String nombre;
    private String tipoPersona; //rol de la persona dentro del sistema al momento de ingresar a las instalaciones.
    private Empresa empresa;
    private String estadoAcceso; // "Activo" o "Con Prohibicion de Ingreso" (ver persona_estados_acceso)

    // Constructor viejo: se mantiene para no romper el código que ya existía y no necesita
    // saber el estado de acceso (queda simplemente sin ese dato, en null).
    public Persona(int id, String documento, String nombre, String tipoPersona, Empresa empresa) {
        this(id, documento, nombre, tipoPersona, empresa, null);
    }

    public Persona(int id, String documento, String nombre, String tipoPersona, Empresa empresa, String estadoAcceso) {
        this.id = id;
        this.documento = documento;
        this.nombre = nombre;
        this.tipoPersona = tipoPersona;
        this.empresa = empresa;
        this.estadoAcceso = estadoAcceso;
    }

    public int getId() { return id; }
    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipoPersona; }
    public Empresa getEmpresa() { return empresa; }
    public String getEstadoAcceso() { return estadoAcceso; }
}