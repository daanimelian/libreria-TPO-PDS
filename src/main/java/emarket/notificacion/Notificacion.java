package emarket.notificacion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una notificación generada para un cliente.
 *
 * <p>Almacena el mensaje, la fecha/hora de creación y si fue vista o no.
 * Proporciona un constructor privado de reconstitución para reconstruir
 * notificaciones desde la base de datos con su timestamp y estado originales.
 */
public class Notificacion {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final String mensaje;
    private final LocalDateTime timestamp;
    private boolean visto;

    /**
     * Construye una notificación nueva, marcándola como no vista y con timestamp actual.
     *
     * @param mensaje texto de la notificación
     */
    public Notificacion(String mensaje) {
        this.mensaje   = mensaje;
        this.timestamp = LocalDateTime.now();
        this.visto     = false;
    }

    /** Constructor privado de reconstitución: preserva timestamp y estado originales. */
    private Notificacion(String mensaje, LocalDateTime timestamp, boolean visto) {
        this.mensaje   = mensaje;
        this.timestamp = timestamp;
        this.visto     = visto;
    }

    /**
     * Factory method para reconstruir una notificación cargada desde la base de datos.
     *
     * @param mensaje   texto almacenado
     * @param timestamp fecha/hora original de la notificación
     * @param visto     si el cliente ya la vio
     * @return notificación reconstituida
     */
    public static Notificacion reconstruirDesdeDB(String mensaje, LocalDateTime timestamp, boolean visto) {
        return new Notificacion(mensaje, timestamp, visto);
    }

    /** @return texto de la notificación */
    public String getMensaje()          { return mensaje; }

    /** @return fecha y hora en que fue generada */
    public LocalDateTime getTimestamp() { return timestamp; }

    /** @return {@code true} si el cliente ya la visualizó */
    public boolean isVisto()            { return visto; }

    /** Marca la notificación como vista. */
    public void marcarVisto()           { visto = true; }

    /** Formatea la notificación como {@code [dd/MM HH:mm] mensaje}. */
    @Override
    public String toString() {
        return "[" + timestamp.format(FMT) + "] " + mensaje;
    }
}
