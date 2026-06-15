package emarket.auth;

import emarket.notificacion.CanalNotificacion;
import java.util.ArrayList;
import java.util.List;

public class AutenticacionService {

    private List<Usuario> usuarios = new ArrayList<>();
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

    public void registrarCliente(String username, String pass, String direccion) {
        usuarios.add(new Cliente(username, pass, direccion));
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

    public void registrarAdministrador(String username, String pass) {
        usuarios.add(new Administrador(username, pass));
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
