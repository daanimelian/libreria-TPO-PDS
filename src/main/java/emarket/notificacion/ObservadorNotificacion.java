package emarket.notificacion;

import emarket.pedido.Pedido;

// Observer: contrato que deben implementar todos los observadores de pedidos
public interface ObservadorNotificacion {
    void actualizar(Pedido pedido);
}
