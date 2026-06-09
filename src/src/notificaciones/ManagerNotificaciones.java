package notificaciones;

/**
 * OBSERVER concreto + CONTEXTO del Strategy.
 *
 * - Como OBSERVADOR: se registra en el Pedido y reacciona en actualizar()
 *   cuando el pedido cambia de estado.
 * - Como CONTEXTO de Strategy: por cada canal preferido del cliente, le pide
 *   a la factory la estrategia correspondiente y delega el envio en ella.
 *
 * DECISION DE DISENO: hay UN solo observador (este manager) que despues abre
 * en abanico hacia los canales, en lugar de un observador por canal. Asi se
 * respeta la preferencia de canales POR CLIENTE (Cliente.getCanalesPreferidos()).
 */
public class ManagerNotificaciones implements ObservadorNotificacion {

    private final NotificacionFactory notificacionFactory;

    public ManagerNotificaciones() {
        this(new NotificacionFactory());
    }

    // Constructor para inyectar el factory (util en tests).
    public ManagerNotificaciones(NotificacionFactory notificacionFactory) {
        this.notificacionFactory = notificacionFactory;
    }

    @Override
    public void actualizar(Pedido pedido) {
        Cliente cliente = pedido.getCliente();
        String mensaje = construirMensaje(pedido);
        enviarACanales(mensaje, cliente);
    }

    /** Despacha el mismo mensaje por cada canal preferido del cliente. */
    private void enviarACanales(String mensaje, Cliente cliente) {
        for (CanalNotificacion canal : cliente.getCanalesPreferidos()) {
            // 1) Simple Factory: crea la estrategia segun el canal.
            EstrategiaNotificacion estrategia = notificacionFactory.crearNotificacion(canal);

            // 2) Cliente resuelve el dato de contacto correcto para ese canal.
            String destinatario = cliente.getDestinatarioPara(canal);

            // 3) Strategy: delega el envio. Si falta el dato de contacto, se omite.
            if (destinatario != null && !destinatario.isEmpty()) {
                estrategia.enviarMensaje(mensaje, destinatario);
            } else {
                System.out.println("[AVISO] Cliente sin dato de contacto para el canal " + canal + ", se omite.");
            }
        }
    }

    /** Arma el texto a partir del estado actual del pedido. */
    private String construirMensaje(Pedido pedido) {
        return "Hola! Tu pedido #" + pedido.getId()
                + " cambio de estado a: " + pedido.getEstado().getNombre() + ".";
    }
}