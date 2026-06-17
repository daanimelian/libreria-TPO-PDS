package emarket.notificacion;

/**
 * Contrato del patrón Observer para los sujetos que emiten notificaciones (Subject).
 *
 * <p>Define las operaciones de gestión de la lista de observadores y el método de
 * notificación. Implementado por {@link emarket.pedido.Pedido}.
 */
public interface SujetoObservable {

    /**
     * Registra un observador para recibir notificaciones de cambio de estado.
     *
     * @param o observador a registrar
     */
    void agregarObservador(ObservadorNotificacion o);

    /**
     * Elimina un observador previamente registrado.
     *
     * @param o observador a eliminar
     */
    void eliminarObservador(ObservadorNotificacion o);

    /**
     * Notifica a todos los observadores registrados sobre un cambio de estado.
     * Debe ser invocado internamente por el sujeto cada vez que su estado cambia.
     */
    void notificarCambios();
}
