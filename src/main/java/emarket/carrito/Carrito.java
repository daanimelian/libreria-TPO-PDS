package emarket.carrito;

import emarket.catalogo.Producto;
import java.util.ArrayList;
import java.util.List;

public class Carrito {

    private final List<ItemCarrito> items = new ArrayList<>();

    public void agregarProducto(Producto p, int cantidad) {
        for (ItemCarrito item : items) {
            if (item.getProducto().getId() == p.getId()) {
                item.modificarCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        items.add(new ItemCarrito(p, cantidad));
    }

    public void eliminarProducto(Producto p) {
        boolean removed = items.removeIf(item -> item.getProducto().getId() == p.getId());
        if (!removed)
            throw new IllegalStateException("El producto '" + p.getNombre() + "' no está en el carrito.");
    }

    public void modificarCantidad(Producto p, int cantidad) {
        for (ItemCarrito item : items) {
            if (item.getProducto().getId() == p.getId()) {
                item.modificarCantidad(cantidad);
                return;
            }
        }
        throw new IllegalStateException("El producto '" + p.getNombre() + "' no está en el carrito.");
    }

    public double calcularTotal() {
        return items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }

    public void vaciarCarrito() {
        items.clear();
    }

    public List<ItemCarrito> getItems() {
        return new ArrayList<>(items);
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }
}
