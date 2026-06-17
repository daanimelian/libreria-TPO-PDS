package emarket.notificacion;

/**
 * Enumeración de los canales de notificación disponibles en el sistema.
 *
 * <p>Cada valor corresponde a una implementación concreta de {@link EstrategiaNotificacion}
 * creada por {@link EstrategiaNotificacionFactory}. Un cliente puede tener uno o más
 * canales preferidos configurados.
 */
public enum CanalNotificacion {
    /** Notificación por correo electrónico (requiere email registrado del cliente). */
    EMAIL,
    /** Notificación por mensaje de texto SMS (requiere teléfono registrado del cliente). */
    SMS,
    /** Notificación push al dispositivo móvil (requiere token de dispositivo del cliente). */
    PUSH
}
