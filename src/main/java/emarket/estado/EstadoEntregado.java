package emarket.estado;

import emarket.pedido.Pedido;

/**
 * Estado terminal del pedido: el producto fue entregado al cliente.
 *
 * <p>No admite más transiciones; llamar a {@link #procesar(Pedido)} lanza
 * {@link IllegalStateException}.
 */
public class EstadoEntregado implements EstadoPedido {

    /**
     * Lanza {@link IllegalStateException}: el estado ENTREGADO es terminal.
     *
     * @param pedido contexto (no utilizado)
     * @throws IllegalStateException siempre, indicando que no hay más transiciones
     */
    @Override
    public void procesar(Pedido pedido) {
        throw new IllegalStateException(
                "El pedido #" + pedido.getId() + " ya fue entregado, no hay más transiciones.");
    }

    @Override
    public String getNombre() { return "ENTREGADO"; }
}
