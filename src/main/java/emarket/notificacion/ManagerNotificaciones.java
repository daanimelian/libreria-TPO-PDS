package emarket.notificacion;

import emarket.auth.Cliente;
import emarket.repositorio.IRepositorioNotificaciones;

/**
 * Observador concreto del patrón Observer: recibe cambios de estado de pedidos
 * y los comunica al cliente por todos sus canales de notificación preferidos.
 *
 * <p>Al recibir un {@link EventoNotificacion}:
 * <ol>
 *   <li>Persiste la notificación en el repositorio.</li>
 *   <li>Para cada canal preferido del cliente, crea la estrategia correspondiente
 *       usando {@link EstrategiaNotificacionFactory} y envía el mensaje.</li>
 * </ol>
 *
 * <p>Combina el patrón Observer (es el observador concreto) con el patrón Strategy
 * (selecciona la estrategia de envío en tiempo de ejecución según el canal).
 */
public class ManagerNotificaciones implements ObservadorNotificacion {

    private final IRepositorioNotificaciones repositorio;

    /**
     * @param repositorio repositorio donde se persistirán las notificaciones generadas
     */
    public ManagerNotificaciones(IRepositorioNotificaciones repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Procesa el evento de cambio de estado: persiste y envía la notificación.
     *
     * @param evento DTO con id del pedido, nuevo estado y cliente afectado
     */
    @Override
    public void actualizar(EventoNotificacion evento) {
        Cliente cliente = evento.getCliente();
        String mensaje = buildMensaje(evento);
        repositorio.guardar(cliente.getUsername(), mensaje);
        enviarACanales(cliente, mensaje);
    }

    /** Construye el texto de la notificación a partir del evento. */
    private String buildMensaje(EventoNotificacion evento) {
        return "Tu pedido #" + evento.getIdPedido()
                + " cambió a estado: " + evento.getEstadoNombre();
    }

    /** Itera los canales preferidos del cliente y envía por cada uno. */
    private void enviarACanales(Cliente cliente, String mensaje) {
        for (CanalNotificacion canal : cliente.getCanalesPreferidos()) {
            EstrategiaNotificacion estrategia = EstrategiaNotificacionFactory.crearNotificacion(canal);
            estrategia.enviarMensaje(mensaje, cliente.getDestinatarioPara(canal));
        }
    }
}
