package emarket.notificacion;

/**
 * Estrategia concreta de notificación por SMS (patrón Strategy).
 *
 * <p>Simula el envío de un mensaje de texto al destinatario. En un entorno real,
 * aquí se integraría un proveedor como Twilio o AWS SNS.
 */
public class EstrategiaNotificacionSMS implements EstrategiaNotificacion {

    /** Proveedor de SMS utilizado (mock para el entorno de demo). */
    private final String proveedorSMS = "TwilioMock";

    /**
     * Envía una notificación por SMS al número de teléfono indicado.
     *
     * @param mensaje      texto de la notificación
     * @param destinatario número de teléfono del cliente (10 dígitos)
     */
    @Override
    public void enviarMensaje(String mensaje, String destinatario) {
        System.out.println("📱 SMS a " + destinatario + ": " + mensaje);
    }
}
