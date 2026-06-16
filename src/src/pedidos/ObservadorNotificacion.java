package pedidos;

/**
 * PATRON OBSERVER - Interfaz Observador.
 *
 * La implementa quien quiera reaccionar a los cambios de estado de un Pedido.
 * En este modulo, la implementa ManagerNotificaciones.
 *
 * Se usa modelo "push": el sujeto pasa el Pedido completo, asi el observador
 * puede leer estado, cliente y total para armar el mensaje.
 *
 * INTEGRACION: 'Pedido' lo provee otro integrante del equipo. Este modulo solo
 * necesita poder leer de Pedido: getId(), getEstado(), getCliente().
 */
public interface ObservadorNotificacion {
    void actualizar(Pedido pedido);
}
