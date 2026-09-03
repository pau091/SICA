package com.sica.dominio.modelo;


public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private Rol rol;


    public Usuario(int id, String nombre, String email, String password, Rol rol){
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public int getId(){return id;} 
    public String getNombre() {return nombre; }
    public String getEmail() { return email; }
    public String getPassword() {return password; }
    public Rol getRol() { return rol; }


}