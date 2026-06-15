package emarket.notificacion;

// Observer: contrato que deben implementar todos los observadores de pedidos
public interface ObservadorNotificacion {
    void actualizar(EventoNotificacion evento);
}
