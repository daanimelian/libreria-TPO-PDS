package emarket.auth;

import emarket.notificacion.CanalNotificacion;
import java.util.ArrayList;
import java.util.List;

public class AutenticacionService {

    private static final String CLAVE_ADMIN = "admin123";

    private final List<Usuario> usuarios = new ArrayList<>();
    private Usuario usuarioActual;

    public boolean iniciarSesion(String username, String pass) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && u.validarCredenciales(pass)) {
                usuarioActual = u;
                return true;
            }
        }
        return false;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

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
        usuarios.add(cliente);
        return cliente;
    }

    public void registrarAdministrador(String username, String pass, String claveAdmin) {
        validarUsername(username);
        validarPassword(pass);
        validarClaveAdministrador(claveAdmin);
        usuarios.add(new Administrador(username, pass));
    }

    private void validarUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        if (username.trim().length() < 4)
            throw new IllegalArgumentException("El nombre de usuario debe tener al menos 4 caracteres.");
        for (Usuario u : usuarios) {
            if (u.getUsername().equalsIgnoreCase(username))
                throw new IllegalArgumentException("El nombre de usuario '" + username + "' ya está en uso.");
        }
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

    public Cliente getClienteActual() {
        if (usuarioActual instanceof Cliente c) return c;
        return null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean estaAutenticado() {
        return usuarioActual != null;
    }
}
