package emarket.carrito;

import emarket.catalogo.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrito de compras del cliente.
 *
 * <p>Mantiene una lista de {@link ItemCarrito} y ofrece operaciones para agregar,
 * eliminar, modificar cantidades y calcular el subtotal (sin impuestos).
 * Las validaciones de stock de dominio residen en {@link CarritoService}.
 */
public class Carrito {

    private final List<ItemCarrito> items = new ArrayList<>();

    /**
     * Agrega el producto al carrito con la cantidad indicada.
     * Si el producto ya está en el carrito, acumula la cantidad.
     *
     * @param p       producto a agregar
     * @param cantidad cantidad a añadir (debe ser mayor a 0)
     */
    public void agregarProducto(Producto p, int cantidad) {
        for (ItemCarrito item : items) {
            if (item.getProducto().getId() == p.getId()) {
                item.modificarCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        items.add(new ItemCarrito(p, cantidad));
    }

    /**
     * Elimina completamente un producto del carrito.
     *
     * @param p producto a eliminar
     * @throws IllegalStateException si el producto no está en el carrito
     */
    public void eliminarProducto(Producto p) {
        boolean removed = items.removeIf(item -> item.getProducto().getId() == p.getId());
        if (!removed)
            throw new IllegalStateException("El producto '" + p.getNombre() + "' no está en el carrito.");
    }

    /**
     * Reemplaza la cantidad de un producto ya presente en el carrito.
     *
     * @param p       producto cuya cantidad se desea modificar
     * @param cantidad nueva cantidad (debe ser mayor a 0)
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero
     * @throws IllegalStateException    si el producto no está en el carrito
     */
    public void modificarCantidad(Producto p, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a 0. Para eliminar un producto, usá eliminarProducto().");
        }
        for (ItemCarrito item : items) {
            if (item.getProducto().getId() == p.getId()) {
                item.modificarCantidad(cantidad);
                return;
            }
        }
        throw new IllegalStateException("El producto '" + p.getNombre() + "' no está en el carrito.");
    }

    /**
     * Calcula el subtotal del carrito sumando los subtotales de cada ítem (sin impuestos).
     *
     * @return suma de {@code precio × cantidad} para todos los ítems
     */
    public double calcularTotal() {
        return items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }

    /** Elimina todos los ítems del carrito. */
    public void vaciarCarrito() {
        items.clear();
    }

    /**
     * Devuelve una copia defensiva de la lista de ítems.
     *
     * @return nueva lista con los ítems actuales; modificarla no afecta el carrito
     */
    public List<ItemCarrito> getItems() {
        return new ArrayList<>(items);
    }

    /** @return {@code true} si el carrito no tiene ningún ítem */
    public boolean estaVacio() {
        return items.isEmpty();
    }
}
