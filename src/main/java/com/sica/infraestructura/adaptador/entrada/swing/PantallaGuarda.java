package com.sica.infraestructura.adaptador.entrada.swing;

import com.sica.infraestructura.adaptador.entrada.controlador.GestorIngresos;
import com.sica.dominio.excepcion.ErrorAccesoBloqueado;
import com.sica.dominio.excepcion.ErrorNoEncontrado;
import com.sica.dominio.excepcion.ErrorSinPermiso;
import javax.swing.JOptionPane;

public class PantallaGuarda {
    private GestorIngresos gestorIngresos = new GestorIngresos();

    public void mostrarMenu() {
        String[] opciones = {
            "Registrar Entrada",
            "Entrada Especial (sin carnet / invitado no anunciado)",
            "Registrar Salida",
            "Cerrar Sesión"
        };
        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                null, "Guardias de Seguridad", "SICA - Control de Acceso",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]
            );

            if (opcion == 0) {
                registrarEntrada(false);
            } else if (opcion == 1) {
                registrarEntrada(true);
            } else if (opcion == 2) {
                registrarSalida();
            } else {
                break;
            }
        }
    }

    // "especial" = true cuando la persona no tiene nada que la autorice todavía (invitado sin
    // avisar, o trabajador sin su carnet): en vez de dejarla entrar, la visita queda pendiente
    // hasta que un Funcionario la apruebe.
    private void registrarEntrada(boolean especial) {
        String doc = JOptionPane.showInputDialog("Documento de la persona:");
        if (doc == null || doc.isBlank()) return;

        try {
            if (especial) {
                gestorIngresos.registrarEntradaEspecial(doc);
                JOptionPane.showMessageDialog(null,
                    "Ingreso registrado como pendiente. Debe ser aprobado por un Funcionario antes de poder entrar.");
            } else {
                gestorIngresos.registrarEntrada(doc);
                JOptionPane.showMessageDialog(null, "¡Entrada registrada exitosamente!");
            }
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorAccesoBloqueado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Ingreso restringido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarSalida() {
        String doc = JOptionPane.showInputDialog("Documento de la persona:");
        if (doc == null || doc.isBlank()) return;

        try {
            gestorIngresos.registrarSalida(doc);
            JOptionPane.showMessageDialog(null, "¡Salida registrada exitosamente!");
        } catch (ErrorNoEncontrado e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ErrorSinPermiso e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
    }
}