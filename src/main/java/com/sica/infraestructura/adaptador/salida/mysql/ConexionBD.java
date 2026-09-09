package com.sica.infraestructura.adaptador.salida.mysql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3307/campus?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USUARIO = "campus";
    private static final String PASSWORD = "campus123"; // 

    private static Connection conexion = null;

    private ConexionBD() {}
    public static Connection getInstancia() {
        try {
            if(conexion == null || conexion.isClosed()){
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch(ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conexion;

    } 

}