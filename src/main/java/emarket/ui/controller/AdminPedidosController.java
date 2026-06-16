package emarket.ui.controller;

import emarket.facade.LibreriaFacade;
import emarket.pedido.Pedido;
import emarket.ui.view.AdminPedidosView;
import java.awt.Frame;

/**
 * Conecta {@link AdminPedidosView} con el backend: lista todos los pedidos y
 * permite avanzar su estado (State pattern manejado íntegramente por el
 * backend a través de {@link LibreriaFacade#actualizarEstadoPedido(int)}).
 */
public class AdminPedidosController {

    private final LibreriaFacade facade;
    private final AdminPedidosView view;
    private final Frame ventanaPadre;

    public AdminPedidosController(LibreriaFacade facade, AdminPedidosView view, Frame ventanaPadre) {
        this.facade = facade;
        this.view = view;
        this.ventanaPadre = ventanaPadre;
        view.setVerDetalleListener(e -> verDetalle());
        view.setAvanzarEstadoListener(e -> avanzarEstado());
    }

    public void refrescar() {
        try {
            view.mostrarPedidos(facade.listarTodosLosPedidos());
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    private void verDetalle() {
        Pedido pedido = view.getPedidoSeleccionado();
        if (pedido == null) {
            view.mostrarError("Seleccioná un pedido.");
            return;
        }
        PedidosClienteController.mostrarDialogoDetalle(pedido, ventanaPadre);
    }

    private void avanzarEstado() {
        Pedido pedido = view.getPedidoSeleccionado();
        if (pedido == null) {
            view.mostrarError("Seleccioná un pedido.");
            return;
        }
        try {
            facade.actualizarEstadoPedido(pedido.getId());
            refrescar();
            view.mostrarMensaje("Estado del pedido #" + pedido.getId() + " actualizado.");
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
        }
    }
}
