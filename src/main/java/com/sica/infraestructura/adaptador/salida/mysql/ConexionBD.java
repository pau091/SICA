package com.sica.infraestructura.adaptador.salida.mysql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/sica?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "Pau1091976524@"; // 

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