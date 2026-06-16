package emarket.notificacion;

public class EstrategiaNotificacionPush implements EstrategiaNotificacion {

    @Override
    public void enviarMensaje(String mensaje, String destinatario) {
        System.out.println("🔔 PUSH a " + destinatario + ": " + mensaje);
    }
}
