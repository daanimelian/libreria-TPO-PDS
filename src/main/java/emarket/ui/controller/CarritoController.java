package emarket.ui.controller;

import emarket.carrito.ItemCarrito;
import emarket.facade.LibreriaFacade;
import emarket.ui.view.CarritoView;
import emarket.ui.view.PagoView;
import java.awt.Frame;
import javax.swing.JDialog;

/**
 * Conecta {@link CarritoView} con el backend y orquesta el flujo de checkout
 * abriendo el diálogo de pago ({@link PagoView} + {@link PagoController}).
 */
public class CarritoController {

    private final LibreriaFacade facade;
    private final CarritoView view;
    private final Frame ventanaPadre;

    public CarritoController(LibreriaFacade facade, CarritoView view, Frame ventanaPadre) {
        this.facade = facade;
        this.view = view;
        this.ventanaPadre = ventanaPadre;
        view.setModificarCantidadListener(e -> modificarCantidad());
        view.setEliminarListener(e -> eliminarProducto());
        view.setVaciarListener(e -> vaciarCarrito());
        view.setConfirmarCompraListener(e -> iniciarCheckout());
    }

    public void refrescar() {
        try {
            view.mostrarItems(facade.getItemsCarrito(), facade.getTotalCarrito());
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    private void modificarCantidad() {
        ItemCarrito item = view.getItemSeleccionado();
        if (item == null) {
            view.mostrarError("Seleccioná un ítem del carrito.");
            return;
        }
        int nuevaCantidad = view.pedirNuevaCantidad(item.getCantidad());
        if (nuevaCantidad <= 0) return;
        try {
            facade.modificarCantidadEnCarrito(item.getProducto().getId(), nuevaCantidad);
            refrescar();
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    private void eliminarProducto() {
        ItemCarrito item = view.getItemSeleccionado();
        if (item == null) {
            view.mostrarError("Seleccioná un ítem del carrito.");
            return;
        }
        try {
            facade.eliminarProductoDelCarrito(item.getProducto().getId());
            refrescar();
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    private void vaciarCarrito() {
        try {
            facade.vaciarCarrito();
            refrescar();
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    private void iniciarCheckout() {
        try {
            if (facade.getItemsCarrito().isEmpty()) {
                view.mostrarError("El carrito está vacío.");
                return;
            }
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
            return;
        }

        JDialog dialogo = new JDialog(ventanaPadre, "Confirmar compra", true);
        PagoView pagoView = new PagoView();
        pagoView.setTotal(facade.getTotalCarrito());
        new PagoController(facade, pagoView, dialogo, () -> {
            refrescar();
            view.mostrarMensaje("¡Compra confirmada con éxito!");
        });
        dialogo.setContentPane(pagoView);
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventanaPadre);
        dialogo.setVisible(true);
    }
}
