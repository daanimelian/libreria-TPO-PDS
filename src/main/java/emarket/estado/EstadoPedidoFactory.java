package emarket.estado;

/**
 * Factory que reconstruye un objeto {@link EstadoPedido} a partir de su nombre persistido.
 *
 * <p>Necesario para la capa JDBC: al cargar un pedido desde la base de datos, el estado
 * se almacena como {@code String} (ej: {@code "PAGADO"}) y esta clase lo convierte al
 * objeto de dominio correspondiente.
 */
public class EstadoPedidoFactory {

    /**
     * Crea la instancia de {@link EstadoPedido} que corresponde al nombre dado.
     *
     * @param nombre nombre canónico del estado ({@code "PENDIENTE"}, {@code "PAGADO"},
     *               {@code "ENVIADO"} o {@code "ENTREGADO"})
     * @return instancia del estado correspondiente
     * @throws IllegalArgumentException si el nombre no corresponde a ningún estado conocido
     */
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
