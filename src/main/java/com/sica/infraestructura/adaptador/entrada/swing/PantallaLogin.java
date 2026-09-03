package com.sica.infraestructura.adaptador.entrada.swing;

import com.sica.infraestructura.adaptador.entrada.controlador.GestorSesion;
import com.sica.dominio.excepcion.ErrorAutenticacion;

import javax.swing.JOptionPane;

/**
 * Vista encargada de solicitar las credenciales de acceso al usuario.
 */
public class PantallaLogin {

    private GestorSesion gestorSesion = new GestorSesion();

    public boolean mostrar() {

        while (true) {

            // 1. Solicitar correo institucional
            String email = JOptionPane.showInputDialog(
                    null,
                    "Correo institucional:",
                    "SICA - Iniciar Sesión",
                    JOptionPane.QUESTION_MESSAGE
            );

            // Si el usuario presiona "Cancelar" o cierra la ventana
            if (email == null) {
                System.out.println("Inicio de sesión cancelado por el usuario.");
                return false;
            }

            // Validar que el correo no esté vacío
            if (email.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "El correo no puede estar vacío.",
                        "Atención",
                        JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            // 2. Solicitar contraseña
            String pass = JOptionPane.showInputDialog(
                    null,
                    "Contraseña:",
                    "SICA - Iniciar Sesión",
                    JOptionPane.QUESTION_MESSAGE
            );

            // Si presiona "Cancelar" en la contraseña
            if (pass == null) {
                System.out.println("Inicio de sesión cancelado por el usuario.");
                return false;
            }

            // Validar que la contraseña no esté vacía
            if (pass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "La contraseña no puede estar vacía.",
                        "Atención",
                        JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            // 3. Intentar autenticar contra la base de datos
            try {

                boolean exito = gestorSesion.iniciarSesion(
                        email.trim(),
                        pass.trim()
                );

                if (exito) {

                    JOptionPane.showMessageDialog(
                            null,
                            "¡Bienvenido al sistema SICA!",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    return true;
                }

            } catch (ErrorAutenticacion e) {

                // Notificar credenciales incorrectas y reintentar
                JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Error de Autenticación",
                        JOptionPane.ERROR_MESSAGE
                );

            } catch (Exception e) {

                // Capturar cualquier otro error inesperado
                // como fallos en la conexión con la BD
                System.err.println(
                        "ERROR INESPERADO DURANTE EL LOGIN:"
                );

                e.printStackTrace();

                JOptionPane.showMessageDialog(
                        null,
                        "Error al conectar con la base de datos. "
                        + "Revisa la consola.",
                        "Error Crítico",
                        JOptionPane.ERROR_MESSAGE
                );

                return false;
            }
        }
    }
}