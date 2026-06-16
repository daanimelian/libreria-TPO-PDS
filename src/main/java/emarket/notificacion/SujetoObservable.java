package emarket.notificacion;

// Observer: contrato para los sujetos que emiten notificaciones
public interface SujetoObservable {
    void agregarObservador(ObservadorNotificacion o);
    void eliminarObservador(ObservadorNotificacion o);
    void notificarCambios();
}
