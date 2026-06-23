package emarket.ui.controller;

import emarket.facade.LibreriaFacade;
import emarket.pedido.Pedido;
import emarket.ui.view.MisPedidosView;
import emarket.ui.view.PedidoDetalleView;
import java.awt.Frame;
import javax.swing.JDialog;

/**
 * Conecta {@link MisPedidosView} con el backend para listar los pedidos del
 * cliente autenticado y mostrar su detalle.
 */
public class PedidosClienteController {

    private final LibreriaFacade facade;
    private final MisPedidosView view;
    private final Frame ventanaPadre;

    public PedidosClienteController(LibreriaFacade facade, MisPedidosView view, Frame ventanaPadre) {
        this.facade = facade;
        this.view = view;
        this.ventanaPadre = ventanaPadre;
        view.setVerDetalleListener(e -> verDetalle());
    }

    public void refrescar() {
        try {
            view.mostrarPedidos(facade.listarPedidosCliente());
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
        mostrarDialogoDetalle(pedido, ventanaPadre);
    }

    /** Reutilizado por {@link AdminPedidosController} para mostrar el mismo diálogo de detalle. */
    static void mostrarDialogoDetalle(Pedido pedido, Frame ventanaPadre) {
        JDialog dialogo = new JDialog(ventanaPadre, "Detalle del pedido #" + pedido.getId(), true);
        PedidoDetalleView detalleView = new PedidoDetalleView();
        detalleView.mostrarDetalle(pedido);
        detalleView.setCerrarListener(e -> dialogo.dispose());
        dialogo.setContentPane(detalleView);
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventanaPadre);
        dialogo.setVisible(true);
    }
}
