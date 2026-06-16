package emarket.estado;

import emarket.pedido.Pedido;

public class EstadoEntregado implements EstadoPedido {

    @Override
    public void procesar(Pedido pedido) {
        throw new IllegalStateException(
                "El pedido #" + pedido.getId() + " ya fue entregado, no hay más transiciones.");
    }

    @Override
    public String getNombre() { return "ENTREGADO"; }
}
