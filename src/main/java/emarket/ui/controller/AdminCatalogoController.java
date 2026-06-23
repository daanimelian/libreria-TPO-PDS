package emarket.ui.controller;

import emarket.facade.LibreriaFacade;
import emarket.ui.view.AdminCatalogoView;

public class AdminCatalogoController {

    private final LibreriaFacade facade;
    private final AdminCatalogoView view;

    public AdminCatalogoController(LibreriaFacade facade, AdminCatalogoView view) {
        this.facade = facade;
        this.view   = view;

        view.setAgregarProductoListener(e  -> agregarProducto());
        view.setAgregarCategoriaListener(e -> agregarCategoria());
        view.setModificarStockListener(e   -> modificarStock());
    }

    private void agregarProducto() {
        try {
            int    id     = Integer.parseInt(view.getIdProducto());
            String nombre = view.getNombreProducto();
            double precio = Double.parseDouble(view.getPrecio());
            int    stock  = Integer.parseInt(view.getStock());
            String cat    = view.getCategoria();

            if (nombre.isBlank() || cat.isBlank())
                throw new IllegalArgumentException("Nombre y categoría son obligatorios.");

            facade.agregarProducto(cat, id, nombre, precio, stock);
            view.mostrarMensaje("Producto '" + nombre + "' agregado en '" + cat + "'.");
            view.limpiarCamposProducto();
        } catch (NumberFormatException ex) {
            view.mostrarError("ID, precio y stock deben ser números válidos.");
        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    private void agregarCategoria() {
        try {
            String nombre = view.getNombreCategoria();
            String padre  = view.getCategoriaPadre();

            if (nombre.isBlank() || padre.isBlank())
                throw new IllegalArgumentException("Nombre y categoría padre son obligatorios.");

            facade.agregarCategoria(nombre, padre);
            view.mostrarMensaje("Categoría '" + nombre + "' agregada bajo '" + padre + "'.");
            view.limpiarCamposCategoria();
        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    private void modificarStock() {
        try {
            int id    = Integer.parseInt(view.getIdStock());
            int stock = Integer.parseInt(view.getNuevoStock());

            facade.modificarStock(id, stock);
            view.mostrarMensaje("Stock del producto #" + id + " actualizado a " + stock + ".");
            view.limpiarCamposStock();
        } catch (NumberFormatException ex) {
            view.mostrarError("ID y stock deben ser números válidos.");
        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }
}
