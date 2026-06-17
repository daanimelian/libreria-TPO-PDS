package emarket.notificacion;

/**
 * Estrategia concreta de notificación push (patrón Strategy).
 *
 * <p>Simula el envío de una notificación push al dispositivo del cliente mediante
 * su token registrado. En un entorno real, aquí se integraría Firebase FCM o APNs.
 */
public class EstrategiaNotificacionPush implements EstrategiaNotificacion {

    /**
     * Envía una notificación push al token de dispositivo indicado.
     *
     * @param mensaje      texto de la notificación
     * @param destinatario token del dispositivo del cliente
     */
    @Override
    public void enviarMensaje(String mensaje, String destinatario) {
        System.out.println("🔔 PUSH a " + destinatario + ": " + mensaje);
    }
}
