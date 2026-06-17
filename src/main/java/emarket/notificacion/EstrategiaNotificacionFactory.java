package emarket.notificacion;

import emarket.repositorio.IRepositorioNotificaciones;

/**
 * Factory de estrategias de notificación y del manager de notificaciones.
 *
 * <p>Centraliza la creación de implementaciones de {@link EstrategiaNotificacion}
 * a partir de un {@link CanalNotificacion}, evitando que {@link ManagerNotificaciones}
 * dependa de las clases concretas de cada canal.
 */
public class EstrategiaNotificacionFactory {

    /**
     * Crea la estrategia de notificación correspondiente al canal indicado.
     *
     * @param tipo canal de notificación ({@code EMAIL}, {@code SMS} o {@code PUSH})
     * @return implementación concreta de {@link EstrategiaNotificacion} para ese canal
     */
    public static EstrategiaNotificacion crearNotificacion(CanalNotificacion tipo) {
        return switch (tipo) {
            case EMAIL -> new EstrategiaNotificacionEmail();
            case SMS   -> new EstrategiaNotificacionSMS();
            case PUSH  -> new EstrategiaNotificacionPush();
        };
    }

    /**
     * Crea un {@link ManagerNotificaciones} ya configurado con su repositorio.
     *
     * @param repositorio repositorio de persistencia de notificaciones
     * @return nuevo manager listo para registrarse como observador
     */
    public static ManagerNotificaciones crearManager(IRepositorioNotificaciones repositorio) {
        return new ManagerNotificaciones(repositorio);
    }
}
