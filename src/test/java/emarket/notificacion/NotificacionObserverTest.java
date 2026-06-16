package emarket.notificacion;

import emarket.auth.Cliente;
import emarket.estado.EstadoPagado;
import emarket.estado.EstadoPendiente;
import emarket.pago.TipoPago;
import emarket.pedido.ItemPedido;
import emarket.pedido.Pedido;
import emarket.repositorio.inmemory.InMemoryRepositorioNotificaciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionObserverTest {

    private InMemoryRepositorioNotificaciones repoNotif;
    private ManagerNotificaciones manager;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        repoNotif = new InMemoryRepositorioNotificaciones();
        manager = new ManagerNotificaciones(repoNotif);

        cliente = new Cliente("testuser", "pass1234", "Av. Test 123");
        cliente.setEmail("test@email.com");
        cliente.setTelefono("1155550000");
        cliente.setTokenDispositivo("TOKEN_TEST");
        cliente.modificarPreferenciasNotificacion(List.of(CanalNotificacion.EMAIL));
    }

    @Test
    void observer_recibeEvento_guardaNotificacion() {
        Pedido pedido = crearPedido();
        pedido.agregarObservador(manager);

        pedido.avanzarEstado(); // PENDIENTE → PAGADO

        List<Notificacion> notifs = repoNotif.listarNoVistas("testuser");
        assertFalse(notifs.isEmpty());
        assertTrue(notifs.get(0).getMensaje().contains("PAGADO"));
    }

    @Test
    void sinObservador_noGeneraNotificacion() {
        Pedido pedido = crearPedido();
        pedido.avanzarEstado(); // Sin observer registrado

        assertTrue(repoNotif.listarNoVistas("testuser").isEmpty());
    }

    @Test
    void listarNoVistas_retornaPendientes() {
        repoNotif.guardar("testuser", "Mensaje 1");
        repoNotif.guardar("testuser", "Mensaje 2");

        List<Notificacion> noVistas = repoNotif.listarNoVistas("testuser");
        assertEquals(2, noVistas.size());
        assertTrue(noVistas.stream().noneMatch(Notificacion::isVisto));
    }

    @Test
    void marcarTodasComoVistas_vaciaSiguienteConsulta() {
        repoNotif.guardar("testuser", "Mensaje A");
        repoNotif.guardar("testuser", "Mensaje B");

        repoNotif.marcarTodasComoVistas("testuser");

        assertTrue(repoNotif.listarNoVistas("testuser").isEmpty());
    }

    @Test
    void notificacion_otroUsuario_noAparece() {
        repoNotif.guardar("otrouser", "Mensaje ajeno");

        assertTrue(repoNotif.listarNoVistas("testuser").isEmpty());
    }

    @Test
    void multiples_cambiosDeEstado_generanMultiplesNotificaciones() {
        Pedido pedido = crearPedido();
        pedido.agregarObservador(manager);

        pedido.avanzarEstado(); // PENDIENTE → PAGADO
        pedido.avanzarEstado(); // PAGADO → ENVIADO

        List<Notificacion> notifs = repoNotif.listarNoVistas("testuser");
        assertEquals(2, notifs.size());
    }

    @Test
    void eventNotificacion_contieneIdPedidoYEstado() {
        Pedido pedido = crearPedido();
        pedido.agregarObservador(manager);

        pedido.avanzarEstado(); // PENDIENTE → PAGADO

        String mensaje = repoNotif.listarNoVistas("testuser").get(0).getMensaje();
        assertTrue(mensaje.contains("1")); // ID del pedido
        assertTrue(mensaje.contains("PAGADO"));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Pedido crearPedido() {
        List<ItemPedido> items = List.of(new ItemPedido("Libro", 1000.0, 1));
        Pedido pedido = new Pedido(1, cliente, items, 1210.0, TipoPago.PAYPAL);
        pedido.setEstado(new EstadoPendiente());
        return pedido;
    }
}
