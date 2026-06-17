package emarket.auth;

import emarket.notificacion.CanalNotificacion;
import emarket.repositorio.IRepositorioUsuarios;
import java.util.List;

/**
 * Servicio de autenticación y registro de usuarios.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Validar credenciales y gestionar la sesión activa</li>
 *   <li>Registrar nuevos clientes y administradores con sus validaciones</li>
 * </ul>
 *
 * <p>Depende de {@link IRepositorioUsuarios} para la persistencia, respetando DIP.
 */
public class AutenticacionService {

    /** Clave secreta requerida para registrar cuentas de administrador. */
    private static final String CLAVE_ADMIN = "admin123";

    private final IRepositorioUsuarios repositorio;
    private Usuario usuarioActual;

    /**
     * @param repositorio implementación de persistencia de usuarios a utilizar
     */
    public AutenticacionService(IRepositorioUsuarios repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Intenta iniciar sesión con las credenciales provistas.
     *
     * @param username nombre de usuario
     * @param pass     contraseña en texto plano
     * @return {@code true} si las credenciales son válidas; {@code false} en caso contrario
     */
    public boolean iniciarSesion(String username, String pass) {
        return repositorio.buscarPorUsername(username)
                .filter(u -> u.validarCredenciales(pass))
                .map(u -> { usuarioActual = u; return true; })
                .orElse(false);
    }

    /** Cierra la sesión activa, dejando {@code usuarioActual} en {@code null}. */
    public void cerrarSesion() {
        usuarioActual = null;
    }

    /**
     * Registra un nuevo cliente aplicando validaciones de negocio.
     *
     * @param username          mínimo 4 caracteres, único en el sistema
     * @param pass              mínimo 8 caracteres, debe contener al menos un dígito
     * @param direccion         domicilio de entrega
     * @param email             correo electrónico para notificaciones
     * @param telefono          número de teléfono para notificaciones SMS
     * @param tokenDispositivo  token para notificaciones push
     * @param canalesPreferidos canales de notificación preferidos
     * @return el nuevo cliente registrado
     * @throws IllegalArgumentException si username o contraseña no cumplen las reglas
     */
    public Cliente registrarClienteCompleto(String username, String pass, String direccion,
                                             String email, String telefono, String tokenDispositivo,
                                             List<CanalNotificacion> canalesPreferidos) {
        validarUsername(username);
        validarPassword(pass);
        Cliente cliente = new Cliente(username, pass, direccion);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setTokenDispositivo(tokenDispositivo);
        cliente.modificarPreferenciasNotificacion(canalesPreferidos);
        repositorio.guardar(cliente);
        return cliente;
    }

    /**
     * Registra un nuevo administrador verificando la clave secreta de autorización.
     *
     * @param username   mínimo 4 caracteres, único en el sistema
     * @param pass       mínimo 8 caracteres, debe contener al menos un dígito
     * @param claveAdmin debe ser igual a la clave secreta configurada
     * @throws IllegalArgumentException si algún dato no cumple las validaciones
     */
    public void registrarAdministrador(String username, String pass, String claveAdmin) {
        validarUsername(username);
        validarPassword(pass);
        validarClaveAdministrador(claveAdmin);
        repositorio.guardar(new Administrador(username, pass));
    }

    private void validarUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        if (username.trim().length() < 4)
            throw new IllegalArgumentException("El nombre de usuario debe tener al menos 4 caracteres.");
        if (repositorio.existeUsername(username))
            throw new IllegalArgumentException("El nombre de usuario '" + username + "' ya está en uso.");
    }

    private void validarPassword(String pass) {
        if (pass == null || pass.isBlank())
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        if (pass.length() < 8)
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        if (!pass.matches(".*\\d.*"))
            throw new IllegalArgumentException("La contraseña debe contener al menos un número.");
    }

    private void validarClaveAdministrador(String clave) {
        if (!CLAVE_ADMIN.equals(clave))
            throw new IllegalArgumentException("Clave de administrador incorrecta.");
    }

    /**
     * @return el cliente actualmente autenticado, o {@code null} si no hay sesión
     *         o el usuario autenticado es un administrador
     */
    public Cliente getClienteActual() {
        if (usuarioActual instanceof Cliente c) return c;
        return null;
    }

    /** @return el usuario actualmente autenticado, o {@code null} si no hay sesión */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /** @return {@code true} si hay un usuario autenticado actualmente */
    public boolean estaAutenticado() {
        return usuarioActual != null;
    }
}
