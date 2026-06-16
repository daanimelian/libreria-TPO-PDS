package emarket.notificacion;

public class EstrategiaNotificacionEmail implements EstrategiaNotificacion {

    private String smtpHost = "smtp.emarket.com";

    @Override
    public void enviarMensaje(String mensaje, String destinatario) {
        System.out.println("📧 EMAIL a " + destinatario + ": " + mensaje);
    }
}
