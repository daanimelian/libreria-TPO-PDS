package emarket.estado;

import emarket.pedido.Pedido;

public class EstadoEnviado implements EstadoPedido {

    @Override
    public void procesar(Pedido pedido) {
        pedido.setEstado(new EstadoEntregado());
    }

    @Override
    public String getNombre() { return "ENVIADO"; }
}
