package emarket.notificacion;

/**
 * Contrato del patrón Strategy para los canales de notificación (Strategy — interfaz).
 *
 * <p>Permite seleccionar en tiempo de ejecución cómo enviar un mensaje al cliente,
 * sin que el contexto ({@link ManagerNotificaciones}) conozca el canal concreto.
 * Implementaciones disponibles:
 * <ul>
 *   <li>{@link EstrategiaNotificacionEmail}</li>
 *   <li>{@link EstrategiaNotificacionSMS}</li>
 *   <li>{@link EstrategiaNotificacionPush}</li>
 * </ul>
 */
public interface EstrategiaNotificacion {

    /**
     * Envía un mensaje de notificación al destinatario indicado.
     *
     * @param mensaje      texto de la notificación a enviar
     * @param destinatario identificador del receptor según el canal
     *                     (email, número de teléfono o token de dispositivo)
     */
    void enviarMensaje(String mensaje, String destinatario);
}
