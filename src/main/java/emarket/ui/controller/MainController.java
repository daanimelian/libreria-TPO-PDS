package emarket.ui.controller;

import emarket.facade.LibreriaFacade;
import emarket.ui.LibreriaSwingApp;
import emarket.ui.view.AdminCatalogoView;
import emarket.ui.view.AdminPedidosView;
import emarket.ui.view.CarritoView;
import emarket.ui.view.CatalogoView;
import emarket.ui.view.MainFrame;
import emarket.ui.view.MisPedidosView;

/**
 * Ensambla {@link MainFrame} con las pantallas correspondientes al rol del
 * usuario autenticado (Cliente o Administrador) y conecta la navegación con
 * cada controlador de pantalla.
 */
public class MainController {

    private static final String PANEL_CATALOGO = "CATALOGO";
    private static final String PANEL_CARRITO = "CARRITO";
    private static final String PANEL_MIS_PEDIDOS = "MIS_PEDIDOS";
    private static final String PANEL_PEDIDOS_ADMIN  = "PEDIDOS_ADMIN";
    private static final String PANEL_CATALOGO_ADMIN = "CATALOGO_ADMIN";

    private final LibreriaFacade facade;
    private final MainFrame mainFrame = new MainFrame();

    private CatalogoController catalogoController;
    private CarritoController carritoController;
    private PedidosClienteController pedidosClienteController;
    private AdminPedidosController adminPedidosController;
    private AdminCatalogoController adminCatalogoController;
    private NotificacionesController notificacionesController;

    public MainController(LibreriaFacade facade) {
        this.facade = facade;
    }

    public void iniciar() {
        boolean esCliente = facade.esCliente();

        CatalogoView catalogoView = new CatalogoView();
        catalogoController = new CatalogoController(facade, catalogoView);
        mainFrame.agregarPanel(PANEL_CATALOGO, catalogoView);

        if (esCliente) {
            CarritoView carritoView = new CarritoView();
            carritoController = new CarritoController(facade, carritoView, mainFrame);
            mainFrame.agregarPanel(PANEL_CARRITO, carritoView);

            MisPedidosView misPedidosView = new MisPedidosView();
            pedidosClienteController = new PedidosClienteController(facade, misPedidosView, mainFrame);
            mainFrame.agregarPanel(PANEL_MIS_PEDIDOS, misPedidosView);

            notificacionesController = new NotificacionesController(facade, mainFrame);
            mainFrame.configurarParaCliente();
        } else {
            AdminPedidosView adminPedidosView = new AdminPedidosView();
            adminPedidosController = new AdminPedidosController(facade, adminPedidosView, mainFrame);
            mainFrame.agregarPanel(PANEL_PEDIDOS_ADMIN, adminPedidosView);

            AdminCatalogoView adminCatalogoView = new AdminCatalogoView();
            adminCatalogoController = new AdminCatalogoController(facade, adminCatalogoView);
            mainFrame.agregarPanel(PANEL_CATALOGO_ADMIN, adminCatalogoView);

            mainFrame.configurarParaAdmin();
        }

        String rol = esCliente ? "CLIENTE" : "ADMINISTRADOR";
        mainFrame.setUsuarioActual(facade.getUsernameActual() + " [" + rol + "]");

        mainFrame.setCatalogoListener(e -> {
            catalogoController.refrescar();
            mainFrame.mostrarPanel(PANEL_CATALOGO);
        });

        if (esCliente) {
            mainFrame.setCarritoListener(e -> {
                carritoController.refrescar();
                mainFrame.mostrarPanel(PANEL_CARRITO);
            });
            mainFrame.setMisPedidosListener(e -> {
                pedidosClienteController.refrescar();
                mainFrame.mostrarPanel(PANEL_MIS_PEDIDOS);
            });
            mainFrame.setNotificacionesListener(e -> notificacionesController.mostrar());
        } else {
            mainFrame.setPedidosAdminListener(e -> {
                adminPedidosController.refrescar();
                mainFrame.mostrarPanel(PANEL_PEDIDOS_ADMIN);
            });
            mainFrame.setCatalogoAdminListener(e -> mainFrame.mostrarPanel(PANEL_CATALOGO_ADMIN));
        }

        mainFrame.setCerrarSesionListener(e -> cerrarSesion());

        catalogoController.refrescar();
        mainFrame.mostrarPanel(PANEL_CATALOGO);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void cerrarSesion() {
        facade.cerrarSesion();
        mainFrame.dispose();
        LibreriaSwingApp.mostrarVentanaLogin(facade);
    }
}
