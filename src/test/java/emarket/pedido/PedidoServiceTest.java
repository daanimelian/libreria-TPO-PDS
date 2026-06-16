package emarket.pedido;

import emarket.auth.Cliente;
import emarket.carrito.CarritoService;
import emarket.catalogo.CatalogoService;
import emarket.catalogo.Categoria;
import emarket.catalogo.Producto;
import emarket.estado.*;
import emarket.notificacion.CanalNotificacion;
import emarket.notificacion.ManagerNotificaciones;
import emarket.pago.DatosPago;
import emarket.pago.TipoPago;
import emarket.repositorio.inmemory.InMemoryRepositorioCatalogo;
import emarket.repositorio.inmemory.InMemoryRepositorioNotificaciones;
import emarket.repositorio.inmemory.InMemoryRepositorioPedidos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceTest {

    private PedidoService pedidoService;
    private CarritoService carritoService;
    private CatalogoService catService;
    private InMemoryRepositorioPedidos repoPedidos;
    private InMemoryRepositorioNotificaciones repoNotif;
    private Cliente cliente;
    private Producto producto;

    @BeforeEach
    void setUp() {
        InMemoryRepositorioCatalogo repoCatalogo = new InMemoryRepositorioCatalogo();
        repoPedidos = new InMemoryRepositorioPedidos();
        repoNotif = new InMemoryRepositorioNotificaciones();

        catService = new CatalogoService(repoCatalogo);
        carritoService = new CarritoService(catService);
        ManagerNotificaciones manager = new ManagerNotificaciones(repoNotif);
        pedidoService = new PedidoService(repoPedidos, repoCatalogo, manager);

        cliente = new Cliente("testuser", "pass1234", "Av. Test 123");
        cliente.setEmail("test@email.com");
        cliente.setTelefono("1155550000");
        cliente.setTokenDispositivo("TOKEN_TEST");
        cliente.modificarPreferenciasNotificacion(List.of(CanalNotificacion.EMAIL));

        Categoria raiz = catService.crearCategoria("Catálogo", null);
        producto = new Producto(1, "Libro Test", 1000.0, 10);
        catService.agregarProducto(raiz, producto);
    }

    // ── Confirmar Compra ──────────────────────────────────────────────────────

    @Test
    void confirmarCompra_carritoVacio_lanzaExcepcion() {
        DatosPago datos = DatosPago.paraTarjeta("4111111111111111", "Test User", "12/27");
        assertThrows(IllegalStateException.class, () ->
                pedidoService.confirmarCompra(cliente, TipoPago.TARJETA_CREDITO, datos));
    }

    @Test
    void confirmarCompra_exitosa_retornaPedidoPagado() {
        carritoService.agregarProducto(cliente, 1, 2);
        DatosPago datos = DatosPago.paraTarjeta("4111111111111111", "Test User", "12/27");

        Pedido pedido = pedidoService.confirmarCompra(cliente, TipoPago.TARJETA_CREDITO, datos);

        assertNotNull(pedido);
        assertEquals("PAGADO", pedido.getEstado().getNombre());
    }

    @Test
    void confirmarCompra_vaciaCaritoAlFinalizar() {
        carritoService.agregarProducto(cliente, 1, 2);
        DatosPago datos = DatosPago.paraTarjeta("4111111111111111", "Test User", "12/27");

        pedidoService.confirmarCompra(cliente, TipoPago.TARJETA_CREDITO, datos);

        assertTrue(cliente.getCarrito().estaVacio());
    }

    @Test
    void confirmarCompra_reduceStockDelProducto() {
        carritoService.agregarProducto(cliente, 1, 3);
        DatosPago datos = DatosPago.paraTarjeta("4111111111111111", "Test User", "12/27");

        pedidoService.confirmarCompra(cliente, TipoPago.TARJETA_CREDITO, datos);

        assertEquals(7, producto.getStock()); // 10 - 3
    }

    @Test
    void confirmarCompra_guardaPedidoEnRepositorio() {
        carritoService.agregarProducto(cliente, 1, 1);
        DatosPago datos = DatosPago.paraPayPal("test@paypal.com");

        Pedido pedido = pedidoService.confirmarCompra(cliente, TipoPago.PAYPAL, datos);

        assertTrue(repoPedidos.buscarPorId(pedido.getId()).isPresent());
    }

    @Test
    void confirmarCompra_tarjetaInvalida_lanzaExcepcion() {
        carritoService.agregarProducto(cliente, 1, 1);
        DatosPago datos = DatosPago.paraTarjeta("1234", "Test User", "12/27");

        assertThrows(IllegalStateException.class, () ->
                pedidoService.confirmarCompra(cliente, TipoPago.TARJETA_CREDITO, datos));
    }

    // ── Máquina de estados ────────────────────────────────────────────────────

    @Test
    void avanzarEstado_pendiente_a_pagado() {
        Pedido pedido = crearPedidoEnEstado(new EstadoPendiente());
        pedido.avanzarEstado();
        assertEquals("PAGADO", pedido.getEstado().getNombre());
    }

    @Test
    void avanzarEstado_pagado_a_enviado() {
        Pedido pedido = crearPedidoEnEstado(new EstadoPagado());
        pedido.avanzarEstado();
        assertEquals("ENVIADO", pedido.getEstado().getNombre());
    }

    @Test
    void avanzarEstado_enviado_a_entregado() {
        Pedido pedido = crearPedidoEnEstado(new EstadoEnviado());
        pedido.avanzarEstado();
        assertEquals("ENTREGADO", pedido.getEstado().getNombre());
    }

    @Test
    void avanzarEstado_entregado_lanzaExcepcion() {
        Pedido pedido = crearPedidoEnEstado(new EstadoEntregado());
        assertThrows(IllegalStateException.class, pedido::avanzarEstado);
    }

    // ── Listar Pedidos ────────────────────────────────────────────────────────

    @Test
    void listarPedidosPorCliente_retornaLosSuyos() {
        carritoService.agregarProducto(cliente, 1, 1);
        DatosPago datos = DatosPago.paraTarjeta("4111111111111111", "Test User", "12/27");
        pedidoService.confirmarCompra(cliente, TipoPago.TARJETA_CREDITO, datos);

        List<Pedido> pedidos = pedidoService.listarPedidosPorCliente(cliente);
        assertEquals(1, pedidos.size());
        assertEquals("testuser", pedidos.get(0).getCliente().getUsername());
    }

    @Test
    void listarTodosLosPedidos_retornaLista() {
        carritoService.agregarProducto(cliente, 1, 1);
        DatosPago datos = DatosPago.paraTarjeta("4111111111111111", "Test User", "12/27");
        pedidoService.confirmarCompra(cliente, TipoPago.TARJETA_CREDITO, datos);

        assertEquals(1, pedidoService.listarTodosLosPedidos().size());
    }

    @Test
    void actualizarEstadoPedido_avanzaDesdeRepositorio() {
        carritoService.agregarProducto(cliente, 1, 1);
        DatosPago datos = DatosPago.paraTarjeta("4111111111111111", "Test User", "12/27");
        Pedido pedido = pedidoService.confirmarCompra(cliente, TipoPago.TARJETA_CREDITO, datos);

        Pedido actualizado = pedidoService.actualizarEstadoPedido(pedido.getId());
        assertEquals("ENVIADO", actualizado.getEstado().getNombre());
    }

    @Test
    void actualizarEstadoPedido_idInexistente_lanzaExcepcion() {
        assertThrows(IllegalStateException.class, () ->
                pedidoService.actualizarEstadoPedido(9999));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Pedido crearPedidoEnEstado(emarket.estado.EstadoPedido estado) {
        List<ItemPedido> items = List.of(new ItemPedido("Libro Test", 1000.0, 1));
        Pedido pedido = new Pedido(1, cliente, items, 1210.0, TipoPago.TARJETA_CREDITO);
        pedido.setEstado(estado);
        return pedido;
    }
}
