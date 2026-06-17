package emarket.estado;

import emarket.pedido.Pedido;

/**
 * Contrato del patrón State para los estados del ciclo de vida de un pedido.
 *
 * <p>Cada estado concreto ({@link EstadoPendiente}, {@link EstadoPagado},
 * {@link EstadoEnviado}, {@link EstadoEntregado}) implementa {@link #procesar(Pedido)}
 * para hacer la transición al siguiente estado, evitando condicionales en
 * el contexto ({@link Pedido}).
 *
 * <p>La cadena de transiciones es:
 * <pre>PENDIENTE → PAGADO → ENVIADO → ENTREGADO (terminal)</pre>
 */
public interface EstadoPedido {

    /**
     * Avanza el pedido al estado siguiente en la cadena de transiciones.
     * Llama internamente a {@link Pedido#setEstado(EstadoPedido)} con el nuevo estado.
     *
     * @param pedido contexto cuyo estado se va a cambiar
     * @throws IllegalStateException si el estado es terminal ({@link EstadoEntregado})
     */
    void procesar(Pedido pedido);

    /**
     * @return nombre canónico del estado (ej: {@code "PENDIENTE"}, {@code "PAGADO"})
     */
    String getNombre();
}
