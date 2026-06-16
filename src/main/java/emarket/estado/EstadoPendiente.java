package emarket.estado;

import emarket.pedido.Pedido;

public class EstadoPendiente implements EstadoPedido {

    @Override
    public void procesar(Pedido pedido) {
        pedido.setEstado(new EstadoPagado());
    }

    @Override
    public String getNombre() { return "PENDIENTE"; }
}
