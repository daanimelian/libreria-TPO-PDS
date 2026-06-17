package emarket.estado;

import emarket.pedido.Pedido;

/**
 * Estado que indica que el pedido fue despachado al cliente.
 *
 * <p>Al procesar, transiciona a {@link EstadoEntregado}.
 */
public class EstadoEnviado implements EstadoPedido {

    /**
     * Transiciona el pedido al estado {@link EstadoEntregado}.
     *
     * @param pedido contexto a actualizar
     */
    @Override
    public void procesar(Pedido pedido) {
        pedido.setEstado(new EstadoEntregado());
    }

    @Override
    public String getNombre() { return "ENVIADO"; }
}
