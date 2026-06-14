package emarket.estado;

import emarket.pedido.Pedido;

public class EstadoPagado implements EstadoPedido {

    @Override
    public void procesar(Pedido pedido) {
        pedido.setEstado(new EstadoEnviado());
    }

    @Override
    public String getNombre() { return "PAGADO"; }
}
