package com.sica;

import com.sica.dominio.modelo.Usuario;
import com.sica.infraestructura.config.SesionUsuario;
import com.sica.infraestructura.adaptador.entrada.swing.PantallaAdmin;
import com.sica.infraestructura.adaptador.entrada.swing.PantallaFuncionario;
import com.sica.infraestructura.adaptador.entrada.swing.PantallaGuarda;
import com.sica.infraestructura.adaptador.entrada.swing.PantallaLogin;
import com.sica.infraestructura.adaptador.entrada.swing.PantallaSupervisor;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SICA ===");

        // Bucle para permitir regresar a la pantalla de Login sin finalizar el programa
        while (true) {
            PantallaLogin login = new PantallaLogin();
            boolean exito = login.mostrar();

            if (exito) {
                Usuario usuario = SesionUsuario.getInstancia().getUsuarioActual();

                if (usuario == null || usuario.getRol() == null) {
                    System.err.println("Error: El usuario no tiene un rol asignado en la sesión.");
                    continue;
                }

                String nombreRol = usuario.getRol().getNombreRol();
                System.out.println("Rol detectado: " + nombreRol);

                // 1. Roles de Seguridad
                if ("Guardia".equalsIgnoreCase(nombreRol) || 
                    "Guarda".equalsIgnoreCase(nombreRol) || 
                    "Guarda de Seguridad".equalsIgnoreCase(nombreRol)) {

                    System.out.println("Cargando interfaz de Guarda de Seguridad...");
                    new PantallaGuarda().mostrarMenu();

                // 2. Roles de Funcionario (Se agrega 'Funcionario de Empresa')
                } else if ("Funcionario".equalsIgnoreCase(nombreRol) ||
                           "Funcionario de Empresa".equalsIgnoreCase(nombreRol)) {

                    System.out.println("Cargando interfaz de Funcionario...");
                    new PantallaFuncionario().mostrarMenu();

                // 2b. Supervisor de Seguridad: antes este rol no tenía ninguna pantalla asignada
                // y caía siempre en el "else" de abajo, aunque sí existía en la base de datos.
                } else if ("Supervisor".equalsIgnoreCase(nombreRol) ||
                           "Supervisor de Seguridad".equalsIgnoreCase(nombreRol)) {

                    System.out.println("Cargando interfaz de Supervisor de Seguridad...");
                    new PantallaSupervisor().mostrarMenu();

                // 3. Roles de Administración / Superusuario
                // Antes esto cargaba PantallaFuncionario (la misma que ve el Funcionario).
                // Ahora tiene su propia pantalla, con las acciones que solo el Superusuario
                // puede hacer (crear Usuarios, Personas y Empresas).
                } else if ("Admin".equalsIgnoreCase(nombreRol) ||
                           "Administrador".equalsIgnoreCase(nombreRol) ||
                           "Superusuario".equalsIgnoreCase(nombreRol)) {

                    System.out.println("Cargando interfaz de Administrador...");
                    new PantallaAdmin().mostrarMenu();

                } else {
                    System.out.println("El rol '" + nombreRol + "' no tiene un menú asignado.");
                }

                System.out.println("\n--- Sesión finalizada. Regresando a la pantalla de login... ---\n");

            } else {
                System.out.println("No se pudo iniciar sesión o se canceló el acceso.");
                break; // Sale definitivamente del programa si le das 'Cancelar' o cierras el Login
            }
        }
    }
}

//RBAC -- control de acceso en roles