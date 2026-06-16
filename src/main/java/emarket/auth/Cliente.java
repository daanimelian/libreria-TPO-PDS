package emarket.auth;

import emarket.carrito.Carrito;
import emarket.notificacion.CanalNotificacion;
import emarket.notificacion.Notificacion;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cliente extends Usuario {

    private final Carrito carrito;
    private List<CanalNotificacion> canalesPreferidos;
    private final List<Notificacion> notificaciones = new ArrayList<>();
    private String direccion;
    private String email;
    private String telefono;
    private String tokenDispositivo;

    public Cliente(String username, String pass, String direccion) {
        super(username, pass);
        this.direccion = direccion;
        this.carrito = new Carrito();
        this.canalesPreferidos = new ArrayList<>();
    }

    public Carrito getCarrito() { return carrito; }

    public List<CanalNotificacion> getCanalesPreferidos() { return canalesPreferidos; }

    public void modificarPreferenciasNotificacion(List<CanalNotificacion> canales) {
        this.canalesPreferidos = canales;
    }

    // Retorna el destinatario según el canal: email, teléfono o token push
    public String getDestinatarioPara(CanalNotificacion canal) {
        return switch (canal) {
            case EMAIL -> email;
            case SMS   -> telefono;
            case PUSH  -> tokenDispositivo;
        };
    }

    public void agregarNotificacion(String mensaje) {
        notificaciones.add(new Notificacion(mensaje));
    }

    // Devuelve las notificaciones no vistas y las marca como vistas.
    public List<String> tomarNotificaciones() {
        List<Notificacion> noVistas = notificaciones.stream()
                .filter(n -> !n.isVisto())
                .toList();
        noVistas.forEach(Notificacion::marcarVisto);
        return noVistas.stream()
                .map(Notificacion::toString)
                .collect(Collectors.toList());
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTokenDispositivo() { return tokenDispositivo; }
    public void setTokenDispositivo(String tokenDispositivo) { this.tokenDispositivo = tokenDispositivo; }
}
