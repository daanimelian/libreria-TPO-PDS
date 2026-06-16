package emarket.carrito;

import emarket.catalogo.Producto;

public class ItemCarrito {

    private final Producto producto;
    private int cantidad;

    public ItemCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }

    public int getCantidad() { return cantidad; }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    public void modificarCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
