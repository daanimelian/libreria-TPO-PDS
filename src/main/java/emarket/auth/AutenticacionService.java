package emarket.auth;

import emarket.notificacion.CanalNotificacion;
import java.util.ArrayList;
import java.util.List;

public class AutenticacionService {

    private List<Usuario> usuarios = new ArrayList<>();
    private Usuario usuarioActual;
    private static final String CLAVE_ADMIN = "admin123";

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

    public void registrarCliente(String username, String pass, String direccion) {
        validarUsername(username);
        validarPassword(pass);
        verificarDisponibilidadUsername(username);
        usuarios.add(new Cliente(username, pass,direccion));
    }

    // Versión extendida para registrar un cliente con todos sus datos de contacto
    public Cliente registrarClienteCompleto(String username, String pass, String direccion,
                                             String email, String telefono, String tokenDispositivo,
                                             List<CanalNotificacion> canalesPreferidos) {
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
        verificarDisponibilidadUsername(username);
        validarClaveAdministrador(claveAdmin);
        usuarios.add(new Administrador(username,pass));
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

    private void verificarDisponibilidadUsername(String username) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) {
                throw new IllegalArgumentException(
                    "El usuario ya existe."
                );
            }
        }
    }

    private void validarUsername(String username) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El usuario no puede estar vacío."
            );
        }

        if (username.trim().length() < 4) {
            throw new IllegalArgumentException(
                "El usuario debe tener al menos 4 caracteres."
            );
        }
    }

    private void validarPassword(String pass) {

        if (pass == null || pass.isBlank()) {
            throw new IllegalArgumentException(
                "La contraseña no puede estar vacía."
            );
        }

        if (pass.length() < 8) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos 8 caracteres."
            );
        }

        if (!pass.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                "La contraseña debe contener al menos un número."
            );
        }
    }

    private void validarClaveAdministrador(String clave) {
        if (!CLAVE_ADMIN.equals(clave)) {
            throw new IllegalArgumentException(
                "Clave de administrador incorrecta."
            );
        }
    }
}
