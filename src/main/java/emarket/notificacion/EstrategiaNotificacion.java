package emarket.notificacion;

// Strategy: contrato para todos los canales de notificación
public interface EstrategiaNotificacion {
    void enviarMensaje(String mensaje, String destinatario);
}
