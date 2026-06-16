package emarket.notificacion;

import emarket.repositorio.IRepositorioNotificaciones;

public class EstrategiaNotificacionFactory {

    public static EstrategiaNotificacion crearNotificacion(CanalNotificacion tipo) {
        return switch (tipo) {
            case EMAIL -> new EstrategiaNotificacionEmail();
            case SMS   -> new EstrategiaNotificacionSMS();
            case PUSH  -> new EstrategiaNotificacionPush();
        };
    }

    public static ManagerNotificaciones crearManager(IRepositorioNotificaciones repositorio) {
        return new ManagerNotificaciones(repositorio);
    }
}
