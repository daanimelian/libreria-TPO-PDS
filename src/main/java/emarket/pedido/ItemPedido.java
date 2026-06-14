package emarket.pedido;

// Snapshot inmutable del producto al momento de la compra
public class ItemPedido {

    private String nombreProducto;
    private double precioUnitario;
    private int cantidad;

    public ItemPedido(String nombreProducto, double precioUnitario, int cantidad) {
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public String getNombreProducto() { return nombreProducto; }
    public double getPrecioUnitario() { return precioUnitario; }
    public int getCantidad() { return cantidad; }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    @Override
    public String toString() {
        return String.format("    - %s x%d @ $%.2f = $%.2f",
                nombreProducto, cantidad, precioUnitario, getSubtotal());
    }
}
