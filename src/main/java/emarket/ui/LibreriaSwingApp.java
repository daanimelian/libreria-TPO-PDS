package emarket.ui;

import emarket.facade.LibreriaFacade;
import emarket.repositorio.factory.InMemoryRepositorioFactory;
import emarket.repositorio.factory.JdbcRepositorioFactory;
import emarket.repositorio.factory.RepositorioFactory;
import emarket.ui.controller.AuthController;
import emarket.ui.view.LoginView;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la interfaz de escritorio Swing (MVC), alternativa a la
 * UI de consola de {@code emarket.Main}. Crea la única instancia de
 * {@link LibreriaFacade} para la sesión y muestra la pantalla de login.
 */
public class LibreriaSwingApp {

    public static void main(String[] args) {
        boolean usarJdbc = java.util.Arrays.asList(args).contains("--jdbc");
        lanzar(usarJdbc);
    }

    public static void lanzar(boolean usarJdbc) {
        RepositorioFactory factory = usarJdbc
                ? new JdbcRepositorioFactory()
                : new InMemoryRepositorioFactory();
        SwingUtilities.invokeLater(() -> iniciarAplicacion(factory));
    }

    private static void iniciarAplicacion(RepositorioFactory factory) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        LibreriaFacade facade = new LibreriaFacade(factory);
        facade.precargarDatos();

        mostrarVentanaLogin(facade);
    }

    /** Muestra la ventana de login; reutilizada también al cerrar sesión desde la app principal. */
    public static void mostrarVentanaLogin(LibreriaFacade facade) {
        JFrame ventana = new JFrame("EMarket - Iniciar sesión");
        LoginView loginView = new LoginView();
        new AuthController(facade, loginView, ventana);

        ventana.setContentPane(loginView);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setMinimumSize(new Dimension(420, 320));
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
