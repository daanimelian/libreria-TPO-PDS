package emarket.notificacion;

import emarket.auth.Cliente;

/**
 * DTO que el sujeto ({@link emarket.pedido.Pedido}) pasa a los observadores al notificar
 * un cambio de estado.
 *
 * <p>Evita que {@link ObservadorNotificacion} dependa directamente de
 * {@code emarket.pedido.Pedido}, reduciendo el acoplamiento entre paquetes.
 */
public class EventoNotificacion {

    private final int idPedido;
    private final String estadoNombre;
    private final Cliente cliente;

    /**
     * @param idPedido     identificador del pedido que cambió de estado
     * @param estadoNombre nombre canónico del nuevo estado (ej: {@code "PAGADO"})
     * @param cliente      cliente propietario del pedido
     */
    public EventoNotificacion(int idPedido, String estadoNombre, Cliente cliente) {
        this.idPedido     = idPedido;
        this.estadoNombre = estadoNombre;
        this.cliente      = cliente;
    }

    /** @return identificador del pedido afectado */
    public int getIdPedido()        { return idPedido; }

    /** @return nombre del nuevo estado del pedido */
    public String getEstadoNombre() { return estadoNombre; }

    /** @return cliente propietario del pedido */
    public Cliente getCliente()     { return cliente; }
}
