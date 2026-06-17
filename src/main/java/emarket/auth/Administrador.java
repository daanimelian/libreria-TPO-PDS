package emarket.auth;

/**
 * Usuario con rol de administrador: puede ver todos los pedidos y avanzar sus estados.
 *
 * <p>Se diferencia del {@link Cliente} en que no tiene carrito ni puede realizar
 * compras. Su legajo se genera automáticamente a partir del nombre de usuario.
 */
public class Administrador extends Usuario {

    private final String legajo;

    /**
     * Construye un administrador y genera su legajo como {@code "ADM-<USERNAME>"}.
     *
     * @param username nombre de usuario único
     * @param pass     contraseña en texto plano
     */
    public Administrador(String username, String pass) {
        super(username, pass);
        this.legajo = "ADM-" + username.toUpperCase();
    }

    /** @return legajo identificador del administrador (ej: {@code "ADM-ADMIN"}) */
    public String getLegajo() { return legajo; }
}
