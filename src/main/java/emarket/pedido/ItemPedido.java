package emarket.pedido;

/**
 * Snapshot inmutable de un producto al momento de confirmar la compra.
 *
 * <p>No referencia a {@link emarket.catalogo.Producto} directamente, sino que
 * copia los datos relevantes (nombre y precio unitario) en el instante de la compra.
 * Esto garantiza que cambios futuros en el producto (precio, eliminación) no alteren
 * el historial de pedidos.
 */
public class ItemPedido {

    private String nombreProducto;
    private double precioUnitario;
    private int cantidad;

    /**
     * @param nombreProducto nombre del producto en el momento de la compra
     * @param precioUnitario precio unitario en el momento de la compra
     * @param cantidad       cantidad de unidades adquiridas
     */
    public ItemPedido(String nombreProducto, double precioUnitario, int cantidad) {
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad       = cantidad;
    }

    /** @return nombre del producto al momento de la compra */
    public String getNombreProducto() { return nombreProducto; }

    /** @return precio unitario al momento de la compra */
    public double getPrecioUnitario() { return precioUnitario; }

    /** @return cantidad de unidades compradas */
    public int getCantidad() { return cantidad; }

    /**
     * Calcula el subtotal de este ítem.
     *
     * @return {@code precioUnitario × cantidad}
     */
    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    @Override
    public String toString() {
        return String.format("    - %s x%d @ $%.2f = $%.2f",
                nombreProducto, cantidad, precioUnitario, getSubtotal());
    }
}
