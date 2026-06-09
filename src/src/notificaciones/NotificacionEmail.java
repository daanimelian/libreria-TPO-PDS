package notificaciones;

/**
 * PATRON STRATEGY - Estrategia concreta (Email).
 *
 * El unico atributo es config de INFRAESTRUCTURA del emisor (el host SMTP),
 * que es propio de la estrategia. El dato del destinatario (la casilla de mail)
 * llega por parametro porque pertenece al cliente.
 *
 * El host se inyecta por constructor para que la clase sea testeable
 * sin depender del Singleton de configuracion (la factory es quien lo provee).
 */
public class NotificacionEmail implements EstrategiaNotificacion {

    private final String smtpHost;

    public NotificacionEmail(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    @Override
    public void enviarMensaje(String mensaje, String destinatario) {
        // Envio simulado (el enunciado permite simular las notificaciones).
        System.out.println("[EMAIL] (SMTP: " + smtpHost + ")");
        System.out.println("  Para: " + destinatario);
        System.out.println("  Mensaje: " + mensaje);
    }
}
