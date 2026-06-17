package emarket.notificacion;

/**
 * Estrategia concreta de notificación por correo electrónico (patrón Strategy).
 *
 * <p>Simula el envío de un email al destinatario. En un entorno real, aquí se
 * integraría un cliente SMTP (ej. JavaMail, SendGrid).
 */
public class EstrategiaNotificacionEmail implements EstrategiaNotificacion {

    /** Host SMTP utilizado para el envío de correos (mock para el entorno de demo). */
    private final String smtpHost = "smtp.emarket.com";

    /**
     * Envía una notificación por email al destinatario indicado.
     *
     * @param mensaje      texto de la notificación
     * @param destinatario dirección de correo electrónico del cliente
     */
    @Override
    public void enviarMensaje(String mensaje, String destinatario) {
        System.out.println("📧 EMAIL a " + destinatario + ": " + mensaje);
    }
}
