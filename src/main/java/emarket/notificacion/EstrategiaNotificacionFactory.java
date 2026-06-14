package emarket.notificacion;

public class EstrategiaNotificacionFactory {

    public EstrategiaNotificacion crearNotificacion(CanalNotificacion tipo) {
        return switch (tipo) {
            case EMAIL -> new EstrategiaNotificacionEmail();
            case SMS  -> new EstrategiaNotificacionSMS();
            case PUSH -> new EstrategiaNotificacionPush();
        };
    }
}
