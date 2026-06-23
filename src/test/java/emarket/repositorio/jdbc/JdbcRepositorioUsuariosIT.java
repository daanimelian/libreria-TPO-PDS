package emarket.repositorio.jdbc;

import emarket.auth.Administrador;
import emarket.auth.Cliente;
import emarket.auth.Usuario;
import emarket.notificacion.CanalNotificacion;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcRepositorioUsuariosIT extends JdbcTestBase {

    private final JdbcRepositorioUsuarios repo = new JdbcRepositorioUsuarios();

    // ── Cliente ───────────────────────────────────────────────────────────────

    @Test
    void guardar_cliente_y_buscarPorUsername() {
        Cliente c = crearCliente("juan");
        repo.guardar(c);

        Optional<Usuario> resultado = repo.buscarPorUsername("juan");
        assertTrue(resultado.isPresent());
        assertInstanceOf(Cliente.class, resultado.get());

        Cliente recuperado = (Cliente) resultado.get();
        assertEquals("juan", recuperado.getUsername());
        assertEquals("juan@email.com", recuperado.getEmail());
        assertEquals("Av. Corrientes 123", recuperado.getDireccion());
        assertEquals("1155550000", recuperado.getTelefono());
        assertEquals("TOKEN_JUAN", recuperado.getTokenDispositivo());
    }

    @Test
    void guardar_cliente_conserva_canalesNotificacion() {
        Cliente c = crearCliente("maria");
        c.modificarPreferenciasNotificacion(List.of(CanalNotificacion.EMAIL, CanalNotificacion.PUSH));
        repo.guardar(c);

        Cliente recuperado = (Cliente) repo.buscarPorUsername("maria").orElseThrow();
        assertEquals(2, recuperado.getCanalesPreferidos().size());
        assertTrue(recuperado.getCanalesPreferidos().contains(CanalNotificacion.EMAIL));
        assertTrue(recuperado.getCanalesPreferidos().contains(CanalNotificacion.PUSH));
    }

    @Test
    void guardar_cliente_sin_canales() {
        Cliente c = crearCliente("pedro");
        c.modificarPreferenciasNotificacion(List.of());
        repo.guardar(c);

        Cliente recuperado = (Cliente) repo.buscarPorUsername("pedro").orElseThrow();
        assertTrue(recuperado.getCanalesPreferidos().isEmpty());
    }

    // ── Administrador ─────────────────────────────────────────────────────────

    @Test
    void guardar_administrador_y_buscarPorUsername() {
        Administrador a = new Administrador("admintest", "admin1234");
        repo.guardar(a);

        Optional<Usuario> resultado = repo.buscarPorUsername("admintest");
        assertTrue(resultado.isPresent());
        assertInstanceOf(Administrador.class, resultado.get());
        assertEquals("admintest", resultado.get().getUsername());
    }

    // ── existeUsername ────────────────────────────────────────────────────────

    @Test
    void existeUsername_usuarioExistente_retornaTrue() {
        repo.guardar(crearCliente("existente"));
        assertTrue(repo.existeUsername("existente"));
    }

    @Test
    void existeUsername_usuarioInexistente_retornaFalse() {
        assertFalse(repo.existeUsername("nadie"));
    }

    // ── buscarPorUsername ─────────────────────────────────────────────────────

    @Test
    void buscarPorUsername_inexistente_retornaEmpty() {
        Optional<Usuario> resultado = repo.buscarPorUsername("fantasma");
        assertFalse(resultado.isPresent());
    }

    // ── listarTodos ───────────────────────────────────────────────────────────

    @Test
    void listarTodos_retornaUsuariosGuardados() {
        repo.guardar(crearCliente("ana"));
        repo.guardar(crearCliente("bob"));
        repo.guardar(new Administrador("adminx", "admin1234"));

        List<Usuario> todos = repo.listarTodos();
        assertEquals(3, todos.size());
    }

    @Test
    void listarTodos_sinUsuarios_retornaListaVacia() {
        assertTrue(repo.listarTodos().isEmpty());
    }

    // ── validación de credenciales ────────────────────────────────────────────

    @Test
    void credenciales_recuperadas_son_validas() {
        Cliente c = crearCliente("credtest");
        repo.guardar(c);

        Usuario recuperado = repo.buscarPorUsername("credtest").orElseThrow();
        assertTrue(recuperado.validarCredenciales("pass1234"));
        assertFalse(recuperado.validarCredenciales("wrongpass"));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Cliente crearCliente(String username) {
        Cliente c = new Cliente(username, "pass1234", "Av. Corrientes 123");
        c.setEmail(username + "@email.com");
        c.setTelefono("1155550000");
        c.setTokenDispositivo("TOKEN_" + username.toUpperCase());
        c.modificarPreferenciasNotificacion(List.of(CanalNotificacion.EMAIL));
        return c;
    }
}
