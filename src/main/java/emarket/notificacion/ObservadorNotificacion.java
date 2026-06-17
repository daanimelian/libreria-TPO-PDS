package emarket.notificacion;

/**
 * Contrato del patrón Observer para los observadores de cambios de estado de pedidos.
 *
 * <p>Implementado por {@link ManagerNotificaciones}. El método {@link #actualizar}
 * recibe un {@link EventoNotificacion} en lugar de referencia directa al pedido,
 * lo que desacopla al observador de la clase {@link emarket.pedido.Pedido}.
 */
public interface ObservadorNotificacion {

    /**
     * Reacciona a un cambio de estado en el pedido observado.
     *
     * @param evento DTO con el id del pedido, el nuevo nombre de estado y el cliente afectado
     */
    void actualizar(EventoNotificacion evento);
}
