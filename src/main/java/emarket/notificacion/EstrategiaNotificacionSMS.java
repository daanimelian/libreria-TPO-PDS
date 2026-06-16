package emarket.notificacion;

public class EstrategiaNotificacionSMS implements EstrategiaNotificacion {

    private String proveedorSMS = "TwilioMock";

    @Override
    public void enviarMensaje(String mensaje, String destinatario) {
        System.out.println("📱 SMS a " + destinatario + ": " + mensaje);
    }
}
