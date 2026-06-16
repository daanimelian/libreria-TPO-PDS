package emarket.ui.controller;

import emarket.facade.LibreriaFacade;
import emarket.ui.view.NotificacionesView;
import java.awt.Frame;
import javax.swing.JDialog;

/**
 * Obtiene las notificaciones pendientes del cliente actual y las muestra en
 * un diálogo modal con {@link NotificacionesView}.
 */
public class NotificacionesController {

    private final LibreriaFacade facade;
    private final Frame ventanaPadre;

    public NotificacionesController(LibreriaFacade facade, Frame ventanaPadre) {
        this.facade = facade;
        this.ventanaPadre = ventanaPadre;
    }

    public void mostrar() {
        NotificacionesView view = new NotificacionesView();
        try {
            view.mostrarNotificaciones(facade.tomarNotificaciones());
        } catch (RuntimeException ex) {
            view.mostrarNotificaciones(java.util.List.of("Error al obtener notificaciones: " + ex.getMessage()));
        }

        JDialog dialogo = new JDialog(ventanaPadre, "Notificaciones", true);
        view.setCerrarListener(e -> dialogo.dispose());
        dialogo.setContentPane(view);
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventanaPadre);
        dialogo.setVisible(true);
    }
}
