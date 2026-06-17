package emarket.estado;

import emarket.pedido.Pedido;

/**
 * Estado inicial del pedido: el pedido fue creado pero aún no se procesó el pago.
 *
 * <p>Al procesar, transiciona a {@link EstadoPagado}.
 */
public class EstadoPendiente implements EstadoPedido {

    /**
     * Transiciona el pedido al estado {@link EstadoPagado}.
     *
     * @param pedido contexto a actualizar
     */
    @Override
    public void procesar(Pedido pedido) {
        pedido.setEstado(new EstadoPagado());
    }

    @Override
    public String getNombre() { return "PENDIENTE"; }
}
