package emarket.estado;

import emarket.pedido.Pedido;

public class EstadoEntregado implements EstadoPedido {

    @Override
    public void procesar(Pedido pedido) {
        System.out.println("El pedido ya fue entregado, no hay más transiciones.");
    }

    @Override
    public String getNombre() { return "ENTREGADO"; }
}
