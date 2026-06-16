package notificaciones;

/**
 * PATRON STRATEGY - Estrategia concreta (Push).
 *
 * CORRECCION DE DISENO: antes esta clase tenia 'tokenDispositivo' como atributo.
 * Eso estaba mal: el token identifica al DISPOSITIVO DEL CLIENTE, no al emisor,
 * por lo que una unica instancia de la estrategia no podia servir a varios
 * clientes. Ahora el token llega por parametro (destinatario) y el atributo
 * propio de la estrategia es el servicio de push (config de infraestructura).
 */
public class NotificacionPush implements EstrategiaNotificacion {

    private final String servicioPush; // p.ej. "FCM", "APNs"

    public NotificacionPush(String servicioPush) {
        this.servicioPush = servicioPush;
    }

    @Override
    public void enviarMensaje(String mensaje, String destinatario) {
        // 'destinatario' es el token del dispositivo del cliente. Envio simulado.
        System.out.println("[PUSH] (Servicio: " + servicioPush + ")");
        System.out.println("  Token destino: " + destinatario);
        System.out.println("  Mensaje: " + mensaje);
    }
}
