package emarket.estado;

// Factory: reconstruye el objeto de estado a partir del nombre persistido en la DB
public class EstadoPedidoFactory {

    public static EstadoPedido crear(String nombre) {
        return switch (nombre) {
            case "PENDIENTE"  -> new EstadoPendiente();
            case "PAGADO"     -> new EstadoPagado();
            case "ENVIADO"    -> new EstadoEnviado();
            case "ENTREGADO"  -> new EstadoEntregado();
            default -> throw new IllegalArgumentException("Estado de pedido desconocido: " + nombre);
        };
    }
}
