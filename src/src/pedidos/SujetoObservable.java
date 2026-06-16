package pedidos;

/**
 * PATRON OBSERVER - Interfaz Sujeto Observable.
 *
 * INTEGRACION: la implementa la clase Pedido (a cargo de otro integrante).
 * Se incluye aca como parte del contrato del patron; si el equipo ya la tiene
 * definida en el modulo de pedidos, usar esa y descartar esta copia.
 */
public interface SujetoObservable {
    void agregarObservador(ObservadorNotificacion o);
    void eliminarObservador(ObservadorNotificacion o);
    void notificarCambios();
}
