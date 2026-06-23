package emarket.ui.controller;

import emarket.facade.LibreriaFacade;
import emarket.ui.view.LoginView;
import emarket.ui.view.RegistroAdminView;
import emarket.ui.view.RegistroClienteView;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Conecta {@link LoginView} (y los formularios de registro) con
 * {@link LibreriaFacade}. Al autenticar con éxito, cede el control a
 * {@link MainController}, que construye la ventana principal según el rol.
 */
public class AuthController {

    private final LibreriaFacade facade;
    private final LoginView view;
    private final JFrame ventana;

    public AuthController(LibreriaFacade facade, LoginView view, JFrame ventana) {
        this.facade = facade;
        this.view = view;
        this.ventana = ventana;
        view.setIniciarSesionListener(e -> iniciarSesion());
        view.setRegistrarClienteListener(e -> abrirRegistroCliente());
        view.setRegistrarAdminListener(e -> abrirRegistroAdmin());
    }

    private void iniciarSesion() {
        String username = view.getUsername();
        String password = view.getPassword();
        try {
            boolean exito = facade.iniciarSesion(username, password);
            if (!exito) {
                view.mostrarError("Usuario o contraseña incorrectos.");
                return;
            }
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
            return;
        }
        ventana.dispose();
        new MainController(facade).iniciar();
    }

    private void abrirRegistroCliente() {
        RegistroClienteView registroView = new RegistroClienteView();
        JDialog dialogo = new JDialog(ventana, "Registro de cliente", true);

        registroView.setCancelarListener(e -> dialogo.dispose());
        registroView.setRegistrarListener(e -> {
            if (!registroView.getPassword().equals(registroView.getConfirmarPassword())) {
                registroView.mostrarError("Las contraseñas no coinciden.");
                return;
            }
            try {
                facade.registrarClienteCompleto(
                        registroView.getUsername(),
                        registroView.getPassword(),
                        registroView.getDireccion(),
                        registroView.getEmail(),
                        registroView.getTelefono(),
                        registroView.getTokenDispositivo(),
                        registroView.getCanalesSeleccionados());
                dialogo.dispose();
                view.mostrarMensaje("Cliente registrado correctamente. Ya podés iniciar sesión.");
            } catch (RuntimeException ex) {
                registroView.mostrarError(ex.getMessage());
            }
        });

        dialogo.setContentPane(registroView);
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true);
    }

    private void abrirRegistroAdmin() {
        RegistroAdminView registroView = new RegistroAdminView();
        JDialog dialogo = new JDialog(ventana, "Registro de administrador", true);

        registroView.setCancelarListener(e -> dialogo.dispose());
        registroView.setRegistrarListener(e -> {
            if (!registroView.getPassword().equals(registroView.getConfirmarPassword())) {
                registroView.mostrarError("Las contraseñas no coinciden.");
                return;
            }
            try {
                facade.registrarAdministrador(
                        registroView.getUsername(),
                        registroView.getPassword(),
                        registroView.getClaveAdmin());
                dialogo.dispose();
                view.mostrarMensaje("Administrador registrado correctamente. Ya podés iniciar sesión.");
            } catch (RuntimeException ex) {
                registroView.mostrarError(ex.getMessage());
            }
        });

        dialogo.setContentPane(registroView);
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true);
    }
}
