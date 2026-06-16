package emarket.estado;

import emarket.pedido.Pedido;

// State: contrato para todos los estados de un pedido
public interface EstadoPedido {
    void procesar(Pedido pedido);
    String getNombre();
}
