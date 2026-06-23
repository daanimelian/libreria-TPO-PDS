package emarket.repositorio.jdbc;

import emarket.auth.Cliente;
import emarket.notificacion.CanalNotificacion;
import emarket.notificacion.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcRepositorioNotificacionesIT extends JdbcTestBase {

    private final JdbcRepositorioUsuarios repoUsuarios = new JdbcRepositorioUsuarios();
    private final JdbcRepositorioNotificaciones repoNotif = new JdbcRepositorioNotificaciones();

    @BeforeEach
    void crearClienteEnDB() {
        Cliente c = new Cliente("notifuser", "pass1234", "Av. Test 123");
        c.setEmail("notifuser@email.com");
        c.setTelefono("1155550000");
        c.setTokenDispositivo("TOKEN_NOTIF");
        c.modificarPreferenciasNotificacion(List.of(CanalNotificacion.EMAIL));
        repoUsuarios.guardar(c);
    }

    // ── guardar ───────────────────────────────────────────────────────────────

    @Test
    void guardar_notificacion_aparece_en_listarNoVistas() {
        repoNotif.guardar("notifuser", "Tu pedido #1 cambió a estado: PAGADO");

        List<Notificacion> resultado = repoNotif.listarNoVistas("notifuser");
        assertEquals(1, resultado.size());
        assertEquals("Tu pedido #1 cambió a estado: PAGADO", resultado.get(0).getMensaje());
        assertFalse(resultado.get(0).isVisto());
    }

    @Test
    void guardar_multiples_notificaciones() {
        repoNotif.guardar("notifuser", "Mensaje 1");
        repoNotif.guardar("notifuser", "Mensaje 2");
        repoNotif.guardar("notifuser", "Mensaje 3");

        assertEquals(3, repoNotif.listarNoVistas("notifuser").size());
    }

    // ── listarNoVistas ────────────────────────────────────────────────────────

    @Test
    void listarNoVistas_sinNotificaciones_retornaListaVacia() {
        assertTrue(repoNotif.listarNoVistas("notifuser").isEmpty());
    }

    @Test
    void listarNoVistas_usuarioInexistente_retornaListaVacia() {
        assertTrue(repoNotif.listarNoVistas("fantasma").isEmpty());
    }

    @Test
    void listarNoVistas_soloRetornaPropias() {
        // Necesitamos un segundo cliente en DB
        Cliente otro = new Cliente("otronotif", "pass1234", "Otra dir");
        otro.setEmail("otro@email.com");
        otro.setTelefono("1155551111");
        otro.setTokenDispositivo("TOKEN_OTRO");
        otro.modificarPreferenciasNotificacion(List.of());
        repoUsuarios.guardar(otro);

        repoNotif.guardar("notifuser", "Solo para notifuser");
        repoNotif.guardar("otronotif", "Solo para otronotif");

        List<Notificacion> resultado = repoNotif.listarNoVistas("notifuser");
        assertEquals(1, resultado.size());
        assertEquals("Solo para notifuser", resultado.get(0).getMensaje());
    }

    // ── marcarTodasComoVistas ─────────────────────────────────────────────────

    @Test
    void marcarTodasComoVistas_vaciaSiguienteConsulta() {
        repoNotif.guardar("notifuser", "Mensaje A");
        repoNotif.guardar("notifuser", "Mensaje B");

        repoNotif.marcarTodasComoVistas("notifuser");

        assertTrue(repoNotif.listarNoVistas("notifuser").isEmpty());
    }

    @Test
    void marcarTodasComoVistas_soloAfectaAlUsuarioIndicado() {
        Cliente otro = new Cliente("otrousr3", "pass1234", "Otra dir");
        otro.setEmail("otro3@email.com");
        otro.setTelefono("1155552222");
        otro.setTokenDispositivo("TOKEN_OTRO3");
        otro.modificarPreferenciasNotificacion(List.of());
        repoUsuarios.guardar(otro);

        repoNotif.guardar("notifuser", "Notif usuario 1");
        repoNotif.guardar("otrousr3", "Notif usuario 2");

        repoNotif.marcarTodasComoVistas("notifuser");

        assertTrue(repoNotif.listarNoVistas("notifuser").isEmpty());
        assertEquals(1, repoNotif.listarNoVistas("otrousr3").size());
    }

    @Test
    void marcarTodasComoVistas_sinNotificaciones_noFalla() {
        assertDoesNotThrow(() -> repoNotif.marcarTodasComoVistas("notifuser"));
    }

    // ── conserva timestamp ────────────────────────────────────────────────────

    @Test
    void notificacion_tiene_timestamp_no_nulo() {
        repoNotif.guardar("notifuser", "Mensaje con timestamp");

        Notificacion n = repoNotif.listarNoVistas("notifuser").get(0);
        assertNotNull(n.getTimestamp());
    }
}
