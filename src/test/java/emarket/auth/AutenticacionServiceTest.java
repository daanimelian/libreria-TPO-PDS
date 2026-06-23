package emarket.auth;

import emarket.notificacion.CanalNotificacion;
import emarket.repositorio.inmemory.InMemoryRepositorioUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AutenticacionServiceTest {

    private AutenticacionService service;

    @BeforeEach
    void setUp() {
        service = new AutenticacionService(new InMemoryRepositorioUsuarios());
    }

    @Test
    void registrarCliente_exitoso() {
        Cliente c = service.registrarClienteCompleto(
                "pepe", "pass1234", "Av. Corrientes 123",
                "pepe@email.com", "1155550000", "TOKEN_PEPE",
                List.of(CanalNotificacion.EMAIL));
        assertNotNull(c);
        assertEquals("pepe", c.getUsername());
    }

    @Test
    void registrarAdministrador_exitoso() {
        assertDoesNotThrow(() ->
                service.registrarAdministrador("admin1", "admin1234", "admin123"));
    }

    @Test
    void registrar_usernameMuyCorto_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarClienteCompleto("ab", "pass1234", "dir",
                        "a@b.com", "123", "TOKEN", List.of()));
    }

    @Test
    void registrar_usernameVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarClienteCompleto("", "pass1234", "dir",
                        "a@b.com", "123", "TOKEN", List.of()));
    }

    @Test
    void registrar_passwordSinNumero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarClienteCompleto("usuario1", "sinNumeros", "dir",
                        "a@b.com", "123", "TOKEN", List.of()));
    }

    @Test
    void registrar_passwordMuyCorta_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarClienteCompleto("usuario1", "abc1", "dir",
                        "a@b.com", "123", "TOKEN", List.of()));
    }

    @Test
    void registrar_usernameDuplicado_lanzaExcepcion() {
        service.registrarClienteCompleto("pepe", "pass1234", "dir",
                "pepe@email.com", "123", "TOKEN", List.of());
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarClienteCompleto("pepe", "pass5678", "otra dir",
                        "pepe2@email.com", "456", "TOKEN2", List.of()));
    }

    @Test
    void registrarAdmin_claveIncorrecta_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarAdministrador("admin2", "admin1234", "claveWrong"));
    }

    @Test
    void login_exitoso_retornaTrue() {
        service.registrarClienteCompleto("pepe", "pass1234", "dir",
                "pepe@email.com", "123", "TOKEN", List.of());
        assertTrue(service.iniciarSesion("pepe", "pass1234"));
        assertTrue(service.estaAutenticado());
    }

    @Test
    void login_passwordIncorrecta_retornaFalse() {
        service.registrarClienteCompleto("pepe", "pass1234", "dir",
                "pepe@email.com", "123", "TOKEN", List.of());
        assertFalse(service.iniciarSesion("pepe", "wrongpass1"));
        assertFalse(service.estaAutenticado());
    }

    @Test
    void login_usernameInexistente_retornaFalse() {
        assertFalse(service.iniciarSesion("nadie", "pass12345"));
        assertFalse(service.estaAutenticado());
    }

    @Test
    void cerrarSesion_limpiaSesion() {
        service.registrarClienteCompleto("pepe", "pass1234", "dir",
                "pepe@email.com", "123", "TOKEN", List.of());
        service.iniciarSesion("pepe", "pass1234");
        service.cerrarSesion();
        assertFalse(service.estaAutenticado());
        assertNull(service.getUsuarioActual());
    }

    @Test
    void getClienteActual_tras_loginCliente() {
        service.registrarClienteCompleto("pepe", "pass1234", "dir",
                "pepe@email.com", "123", "TOKEN", List.of());
        service.iniciarSesion("pepe", "pass1234");
        assertNotNull(service.getClienteActual());
        assertEquals("pepe", service.getClienteActual().getUsername());
    }

    @Test
    void getClienteActual_tras_loginAdmin_esNull() {
        service.registrarAdministrador("admin1", "admin1234", "admin123");
        service.iniciarSesion("admin1", "admin1234");
        assertNull(service.getClienteActual());
        assertNotNull(service.getUsuarioActual());
        assertInstanceOf(Administrador.class, service.getUsuarioActual());
    }

    @Test
    void registrar_password_vacia_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarClienteCompleto("usuario1", "", "dir",
                        "a@b.com", "123", "TOKEN", List.of()));
    }
}
