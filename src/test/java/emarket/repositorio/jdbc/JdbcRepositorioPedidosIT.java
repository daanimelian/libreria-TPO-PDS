package emarket.repositorio.jdbc;

import emarket.auth.Cliente;
import emarket.estado.EstadoPagado;
import emarket.estado.EstadoPendiente;
import emarket.notificacion.CanalNotificacion;
import emarket.pago.TipoPago;
import emarket.pedido.ItemPedido;
import emarket.pedido.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcRepositorioPedidosIT extends JdbcTestBase {

    private final JdbcRepositorioUsuarios repoUsuarios = new JdbcRepositorioUsuarios();
    private final JdbcRepositorioPedidos repoPedidos = new JdbcRepositorioPedidos();

    private Cliente clienteEnDB;

    @BeforeEach
    void crearClienteEnDB() {
        clienteEnDB = new Cliente("pedidouser", "pass1234", "Av. Test 123");
        clienteEnDB.setEmail("pedidouser@email.com");
        clienteEnDB.setTelefono("1155550000");
        clienteEnDB.setTokenDispositivo("TOKEN_PEDIDO");
        clienteEnDB.modificarPreferenciasNotificacion(List.of(CanalNotificacion.EMAIL));
        repoUsuarios.guardar(clienteEnDB);
    }

    // ── getProximoId ──────────────────────────────────────────────────────────

    @Test
    void getProximoId_retornaEnteroPositivo() {
        int id = repoPedidos.getProximoId();
        assertTrue(id > 0);
    }

    // ── guardar ───────────────────────────────────────────────────────────────

    @Test
    void guardar_pedido_y_buscarPorId() {
        Pedido pedido = crearPedido(clienteEnDB, TipoPago.PAYPAL);
        repoPedidos.guardar(pedido);

        Optional<Pedido> resultado = repoPedidos.buscarPorId(pedido.getId());
        assertTrue(resultado.isPresent());

        Pedido recuperado = resultado.get();
        assertEquals(pedido.getId(), recuperado.getId());
        assertEquals("pedidouser", recuperado.getCliente().getUsername());
        assertEquals(TipoPago.PAYPAL, recuperado.getTipoPago());
        assertEquals("PAGADO", recuperado.getEstado().getNombre());
    }

    @Test
    void guardar_pedido_conserva_items() {
        Pedido pedido = crearPedido(clienteEnDB, TipoPago.TARJETA_CREDITO);
        repoPedidos.guardar(pedido);

        Pedido recuperado = repoPedidos.buscarPorId(pedido.getId()).orElseThrow();
        assertEquals(2, recuperado.getItems().size());

        ItemPedido item = recuperado.getItems().stream()
                .filter(i -> i.getNombreProducto().equals("Libro A"))
                .findFirst().orElseThrow();
        assertEquals(1000.0, item.getPrecioUnitario(), 0.01);
        assertEquals(2, item.getCantidad());
    }

    // ── actualizarEstado ──────────────────────────────────────────────────────

    @Test
    void actualizarEstado_cambia_estado_en_db() {
        Pedido pedido = crearPedido(clienteEnDB, TipoPago.MERCADO_PAGO);
        repoPedidos.guardar(pedido);

        repoPedidos.actualizarEstado(pedido.getId(), "ENVIADO");

        Pedido recuperado = repoPedidos.buscarPorId(pedido.getId()).orElseThrow();
        assertEquals("ENVIADO", recuperado.getEstado().getNombre());
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    void buscarPorId_inexistente_retornaEmpty() {
        assertFalse(repoPedidos.buscarPorId(999999).isPresent());
    }

    // ── listarPorCliente ──────────────────────────────────────────────────────

    @Test
    void listarPorCliente_retorna_pedidos_del_cliente() {
        repoPedidos.guardar(crearPedido(clienteEnDB, TipoPago.PAYPAL));
        repoPedidos.guardar(crearPedido(clienteEnDB, TipoPago.TRANSFERENCIA));

        List<Pedido> pedidos = repoPedidos.listarPorCliente("pedidouser");
        assertEquals(2, pedidos.size());
        assertTrue(pedidos.stream().allMatch(p -> p.getCliente().getUsername().equals("pedidouser")));
    }

    @Test
    void listarPorCliente_sinPedidos_retornaListaVacia() {
        assertTrue(repoPedidos.listarPorCliente("pedidouser").isEmpty());
    }

    @Test
    void listarPorCliente_filtradoPorUsername() {
        // Crear segundo cliente
        Cliente otroCliente = new Cliente("otrousr2", "pass1234", "Otra dir");
        otroCliente.setEmail("otro@email.com");
        otroCliente.setTelefono("1155551111");
        otroCliente.setTokenDispositivo("TOKEN_OTRO");
        otroCliente.modificarPreferenciasNotificacion(List.of());
        repoUsuarios.guardar(otroCliente);

        repoPedidos.guardar(crearPedido(clienteEnDB, TipoPago.PAYPAL));
        repoPedidos.guardar(crearPedido(otroCliente, TipoPago.PAYPAL));

        List<Pedido> pedidos = repoPedidos.listarPorCliente("pedidouser");
        assertEquals(1, pedidos.size());
    }

    // ── listarTodos ───────────────────────────────────────────────────────────

    @Test
    void listarTodos_retornaTodasLosPedidos() {
        repoPedidos.guardar(crearPedido(clienteEnDB, TipoPago.PAYPAL));
        repoPedidos.guardar(crearPedido(clienteEnDB, TipoPago.MERCADO_PAGO));

        assertEquals(2, repoPedidos.listarTodos().size());
    }

    @Test
    void listarTodos_sinPedidos_retornaListaVacia() {
        assertTrue(repoPedidos.listarTodos().isEmpty());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Pedido crearPedido(Cliente cliente, TipoPago tipoPago) {
        int id = repoPedidos.getProximoId();
        List<ItemPedido> items = List.of(
                new ItemPedido("Libro A", 1000.0, 2),
                new ItemPedido("Libro B", 2000.0, 1)
        );
        double total = 3000.0 * 1.21;
        Pedido pedido = new Pedido(id, cliente, items, total, tipoPago);
        pedido.setEstado(new EstadoPagado());
        return pedido;
    }
}
