package emarket.notificacion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notificacion {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final String mensaje;
    private final LocalDateTime timestamp;
    private boolean visto;

    public Notificacion(String mensaje) {
        this.mensaje   = mensaje;
        this.timestamp = LocalDateTime.now();
        this.visto     = false;
    }

    public String getMensaje()          { return mensaje; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isVisto()            { return visto; }
    public void marcarVisto()           { visto = true; }

    @Override
    public String toString() {
        return "[" + timestamp.format(FMT) + "] " + mensaje;
    }
}
