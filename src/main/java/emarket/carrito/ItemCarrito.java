package emarket.carrito;

import emarket.catalogo.Producto;

/**
 * Representa un ítem dentro del carrito de compras.
 *
 * <p>Asocia un {@link Producto} con la cantidad elegida por el cliente y
 * calcula el subtotal correspondiente.
 */
public class ItemCarrito {

    private final Producto producto;
    private int cantidad;

    /**
     * @param producto producto referenciado por este ítem
     * @param cantidad cantidad inicial (debe ser mayor a 0)
     */
    public ItemCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    /** @return el producto asociado a este ítem */
    public Producto getProducto() { return producto; }

    /** @return cantidad actual de unidades de este ítem */
    public int getCantidad() { return cantidad; }

    /**
     * Calcula el subtotal del ítem (precio unitario × cantidad).
     *
     * @return subtotal sin impuestos
     */
    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    /**
     * Reemplaza la cantidad de este ítem.
     *
     * @param cantidad nueva cantidad (debe ser mayor a 0)
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero
     */
    public void modificarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        this.cantidad = cantidad;
    }
}
