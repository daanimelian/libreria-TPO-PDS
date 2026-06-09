package notificaciones;

/**
 * PATRON STRATEGY - Interfaz comun.
 *
 * Define el contrato que cumplen todas las formas concretas de enviar
 * una notificacion (email, SMS, push). Permite intercambiar el algoritmo
 * de envio en tiempo de ejecucion sin que el contexto (ManagerNotificaciones)
 * conozca la implementacion concreta.
 *
 * @param mensaje      texto ya armado a enviar
 * @param destinatario dato de contacto del CLIENTE para este canal
 *                     (email, numero de telefono o token de dispositivo).
 *                     OJO: el destinatario es un dato del cliente, NO de la
 *                     estrategia. Por eso viaja como parametro y no como atributo.
 */
public interface EstrategiaNotificacion {
    void enviarMensaje(String mensaje, String destinatario);
}
