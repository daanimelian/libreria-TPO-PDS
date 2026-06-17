package emarket.auth;

import emarket.carrito.Carrito;
import emarket.notificacion.CanalNotificacion;
import java.util.ArrayList;
import java.util.List;

/**
 * Usuario con rol de cliente: puede agregar productos al carrito, realizar compras
 * y recibir notificaciones sobre el estado de sus pedidos.
 *
 * <p>Cada cliente posee un {@link Carrito} propio y una lista de canales de
 * notificación preferidos ({@code EMAIL}, {@code SMS}, {@code PUSH}).
 */
public class Cliente extends Usuario {

    private final Carrito carrito;
    private List<CanalNotificacion> canalesPreferidos;
    private String direccion;
    private String email;
    private String telefono;
    private String tokenDispositivo;

    /**
     * Construye un cliente con su carrito vacío y sin canales configurados.
     *
     * @param username  nombre de usuario único
     * @param pass      contraseña en texto plano
     * @param direccion domicilio de entrega
     */
    public Cliente(String username, String pass, String direccion) {
        super(username, pass);
        this.direccion         = direccion;
        this.carrito           = new Carrito();
        this.canalesPreferidos = new ArrayList<>();
    }

    /** @return carrito de compras propio de este cliente */
    public Carrito getCarrito() { return carrito; }

    /** @return lista de canales por los cuales el cliente desea recibir notificaciones */
    public List<CanalNotificacion> getCanalesPreferidos() { return canalesPreferidos; }

    /**
     * Actualiza la lista de canales de notificación preferidos.
     *
     * @param canales nueva lista de canales (puede contener {@code EMAIL}, {@code SMS}, {@code PUSH})
     */
    public void modificarPreferenciasNotificacion(List<CanalNotificacion> canales) {
        this.canalesPreferidos = canales;
    }

    /**
     * Devuelve el identificador de contacto del cliente para el canal indicado.
     *
     * @param canal canal de notificación consultado
     * @return email, número de teléfono o token de dispositivo según el canal
     */
    public String getDestinatarioPara(CanalNotificacion canal) {
        return switch (canal) {
            case EMAIL -> email;
            case SMS   -> telefono;
            case PUSH  -> tokenDispositivo;
        };
    }

    /** @return domicilio de entrega del cliente */
    public String getDireccion() { return direccion; }

    /** @param direccion nuevo domicilio de entrega */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /** @return dirección de correo electrónico del cliente */
    public String getEmail() { return email; }

    /** @param email nueva dirección de correo electrónico */
    public void setEmail(String email) { this.email = email; }

    /** @return número de teléfono de 10 dígitos del cliente */
    public String getTelefono() { return telefono; }

    /** @param telefono nuevo número de teléfono */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /** @return token del dispositivo para notificaciones push */
    public String getTokenDispositivo() { return tokenDispositivo; }

    /** @param tokenDispositivo nuevo token de dispositivo */
    public void setTokenDispositivo(String tokenDispositivo) { this.tokenDispositivo = tokenDispositivo; }
}
