package notificaciones;

/**
 * PATRON SIMPLE FACTORY.
 *
 * Encapsula la creacion de la estrategia concreta segun el canal, devolviendo
 * siempre el tipo abstracto EstrategiaNotificacion. El cliente del factory
 * (ManagerNotificaciones) no necesita conocer las clases concretas.
 *
 * CORRECCION DE DISENO: ahora es metodo de INSTANCIA (antes era estatico pero
 * el Manager guardaba una referencia, lo cual era incoherente). Ser de instancia
 * facilita inyectarlo y mockearlo en tests.
 *
 * Tambien centraliza aca la lectura de la config de infraestructura desde el
 * Singleton ConfiguracionSistema, manteniendo a las estrategias desacopladas
 * del Singleton (se las inyecta por constructor).
 */
public class NotificacionFactory {

    public EstrategiaNotificacion crearNotificacion(CanalNotificacion tipo) {
        // INTEGRACION: usa el Singleton de configuracion ya existente en el proyecto.
        // Las claves esperadas ("notif.smtp.host", etc.) deben estar cargadas en
        // ConfiguracionSistema. Ajustar los nombres de clave si el proyecto usa otros.
        ConfiguracionSistema config = ConfiguracionSistema.getInstance();

        switch (tipo) {
            case EMAIL:
                return new NotificacionEmail(config.getParametro("notif.smtp.host"));
            case SMS:
                return new NotificacionSMS(config.getParametro("notif.sms.proveedor"));
            case PUSH:
                return new NotificacionPush(config.getParametro("notif.push.servicio"));
            default:
                throw new IllegalArgumentException("Canal de notificacion no soportado: " + tipo);
        }
    }
}