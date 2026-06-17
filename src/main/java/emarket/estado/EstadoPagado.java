package emarket.estado;

import emarket.pedido.Pedido;

/**
 * Estado que indica que el pago fue procesado correctamente.
 *
 * <p>Al procesar, transiciona a {@link EstadoEnviado}.
 */
public class EstadoPagado implements EstadoPedido {

    /**
     * Transiciona el pedido al estado {@link EstadoEnviado}.
     *
     * @param pedido contexto a actualizar
     */
    @Override
    public void procesar(Pedido pedido) {
        pedido.setEstado(new EstadoEnviado());
    }

    @Override
    public String getNombre() { return "PAGADO"; }
}
