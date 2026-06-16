package notificaciones;

/**
 * PATRON STRATEGY - Estrategia concreta (SMS).
 *
 * 'proveedorSMS' es config de infraestructura del emisor (la pasarela de SMS).
 * El numero de telefono del cliente llega por parametro (destinatario).
 */
public class NotificacionSMS implements EstrategiaNotificacion {

    private final String proveedorSMS;

    public NotificacionSMS(String proveedorSMS) {
        this.proveedorSMS = proveedorSMS;
    }

    @Override
    public void enviarMensaje(String mensaje, String destinatario) {
        // Envio simulado.
        System.out.println("[SMS] (Proveedor: " + proveedorSMS + ")");
        System.out.println("  Para: " + destinatario);
        System.out.println("  Mensaje: " + mensaje);
    }
}
