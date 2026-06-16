package emarket.ui.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * Ventana principal: vista pasiva que actúa como contenedor de navegación
 * (CardLayout) entre las distintas pantallas de la aplicación. No conoce
 * lógica de negocio: solo agrega paneles y expone listeners de navegación.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelContenido = new JPanel(cardLayout);
    private final JLabel lblUsuario = new JLabel();

    private final JButton btnCatalogo = new JButton("Catálogo");
    private final JButton btnCarrito = new JButton("Carrito");
    private final JButton btnMisPedidos = new JButton("Mis pedidos");
    private final JButton btnPedidosAdmin = new JButton("Pedidos (admin)");
    private final JButton btnNotificaciones = new JButton("Notificaciones");
    private final JButton btnCerrarSesion = new JButton("Cerrar sesión");

    public MainFrame() {
        super("EMarket");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JToolBar barraNavegacion = new JToolBar();
        barraNavegacion.setFloatable(false);
        barraNavegacion.add(btnCatalogo);
        barraNavegacion.add(btnCarrito);
        barraNavegacion.add(btnMisPedidos);
        barraNavegacion.add(btnPedidosAdmin);
        barraNavegacion.add(btnNotificaciones);
        barraNavegacion.addSeparator();
        barraNavegacion.add(javax.swing.Box.createHorizontalGlue());
        barraNavegacion.add(lblUsuario);
        barraNavegacion.addSeparator();
        barraNavegacion.add(btnCerrarSesion);

        panelContenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(barraNavegacion, BorderLayout.NORTH);
        add(panelContenido, BorderLayout.CENTER);

        setMinimumSize(new java.awt.Dimension(800, 550));
    }

    public void agregarPanel(String nombre, JPanel panel) {
        panelContenido.add(panel, nombre);
    }

    public void mostrarPanel(String nombre) {
        cardLayout.show(panelContenido, nombre);
    }

    public void setUsuarioActual(String texto) {
        lblUsuario.setText(texto);
    }

    public void configurarParaCliente() {
        btnCarrito.setVisible(true);
        btnMisPedidos.setVisible(true);
        btnNotificaciones.setVisible(true);
        btnPedidosAdmin.setVisible(false);
    }

    public void configurarParaAdmin() {
        btnCarrito.setVisible(false);
        btnMisPedidos.setVisible(false);
        btnNotificaciones.setVisible(false);
        btnPedidosAdmin.setVisible(true);
    }

    public void setCatalogoListener(ActionListener listener) {
        btnCatalogo.addActionListener(listener);
    }

    public void setCarritoListener(ActionListener listener) {
        btnCarrito.addActionListener(listener);
    }

    public void setMisPedidosListener(ActionListener listener) {
        btnMisPedidos.addActionListener(listener);
    }

    public void setPedidosAdminListener(ActionListener listener) {
        btnPedidosAdmin.addActionListener(listener);
    }

    public void setNotificacionesListener(ActionListener listener) {
        btnNotificaciones.addActionListener(listener);
    }

    public void setCerrarSesionListener(ActionListener listener) {
        btnCerrarSesion.addActionListener(listener);
    }
}
