package emarket.ui.controller;

import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import emarket.facade.LibreriaFacade;
import emarket.ui.view.CatalogoView;

/**
 * Único punto que conecta {@link CatalogoView} con el backend a través de
 * {@link LibreriaFacade}. La vista nunca invoca al facade directamente.
 */
public class CatalogoController {

    private final LibreriaFacade facade;
    private final CatalogoView view;

    public CatalogoController(LibreriaFacade facade, CatalogoView view) {
        this.facade = facade;
        this.view = view;
        view.setAgregarCarritoListener(e -> agregarAlCarrito());
    }

    public void refrescar() {
        try {
            ComponenteCatalogo raiz = facade.listarCatalogo();
            view.mostrarCatalogo(raiz);
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    private void agregarAlCarrito() {
        Producto producto = view.getProductoSeleccionado();
        if (producto == null) {
            view.mostrarError("Seleccioná un producto del catálogo.");
            return;
        }
        int cantidad = view.getCantidadIngresada();
        try {
            facade.agregarProductoAlCarrito(producto.getId(), cantidad);
            view.mostrarMensaje("Se agregó \"" + producto.getNombre() + "\" (x" + cantidad + ") al carrito.");
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
        }
    }
}
