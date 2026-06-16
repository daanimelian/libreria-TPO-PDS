package emarket.facade;

import emarket.auth.Administrador;
import emarket.auth.AutenticacionService;
import emarket.auth.Cliente;
import emarket.carrito.CarritoService;
import emarket.catalogo.CatalogoService;
import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import emarket.notificacion.CanalNotificacion;
import emarket.pago.DatosPago;
import emarket.pago.MetodoPagoFactory;
import emarket.pago.TipoPago;
import java.util.Scanner;
import emarket.pedido.Pedido;
import emarket.pedido.PedidoService;
import java.util.List;

// Facade: punto de entrada único para todas las operaciones del sistema
public class LibreriaFacade {

    private final AutenticacionService autService;
    private final CatalogoService catService;
    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final MetodoPagoFactory metodoPagoFactory;

    public LibreriaFacade() {
        this.autService = new AutenticacionService();
        this.catService = new CatalogoService();
        this.carritoService = new CarritoService(catService);
        this.pedidoService = new PedidoService();
        this.metodoPagoFactory = new MetodoPagoFactory();
    }

    // ── Autenticación ────────────────────────────────────────────────────────

    public boolean iniciarSesion(String username, String pass) {
        return autService.iniciarSesion(username, pass);
    }

    public void cerrarSesion() {
        autService.cerrarSesion();
    }

    public Cliente registrarClienteCompleto(String username, String pass, String direccion,
                                             String email, String telefono, String tokenDispositivo,
                                             List<CanalNotificacion> canalesPreferidos) {
        return autService.registrarClienteCompleto(
                username, pass, direccion, email, telefono, tokenDispositivo, canalesPreferidos);
    }

    public void registrarAdministrador(String username, String pass) {
        autService.registrarAdministrador(username, pass);
    }

    // ── Catálogo ─────────────────────────────────────────────────────────────

    public Producto buscarProducto(int id) {
        verificarAutenticacion();
        return catService.buscarProductoPorId(id);
    }

    public ComponenteCatalogo listarCatalogo() {
        verificarAutenticacion();
        return catService.listarCatalogo();
    }

    // ── Carrito ──────────────────────────────────────────────────────────────

    public void agregarProductoAlCarrito(int idProducto, int cantidad) {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) {
            throw new IllegalStateException("Solo los clientes pueden agregar productos al carrito");
        }
        carritoService.agregarProducto(cliente, idProducto, cantidad);
    }

    public void eliminarProductoDelCarrito(int idProducto) {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) throw new IllegalStateException("Solo los clientes tienen carrito");
        Producto p = catService.buscarProductoPorId(idProducto);
        if (p == null) throw new IllegalStateException("Producto no encontrado: id=" + idProducto);
        carritoService.eliminarProducto(cliente, p);
    }

    public void modificarCantidadEnCarrito(int idProducto, int cantidad) {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) throw new IllegalStateException("Solo los clientes tienen carrito");
        if (!catService.verificarDisponibilidad(idProducto, cantidad)) {
            Producto p = catService.buscarProductoPorId(idProducto);
            String nombre = p != null ? p.getNombre() : "id=" + idProducto;
            throw new IllegalStateException("Stock insuficiente para: " + nombre);
        }
        Producto p = catService.buscarProductoPorId(idProducto);
        if (p == null) throw new IllegalStateException("Producto no encontrado: id=" + idProducto);
        carritoService.modificarCantidad(cliente, p, cantidad);
    }

    public void vaciarCarrito() {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) throw new IllegalStateException("Solo los clientes tienen carrito");
        carritoService.vaciarCarrito(cliente);
    }

    // ── Pedidos ──────────────────────────────────────────────────────────────

    public DatosPago pedirDatosPago(TipoPago tipoPago, Scanner sc) {
        return metodoPagoFactory.pedirDatos(tipoPago, sc);
    }

    public void confirmarCompra(TipoPago tipoPago, DatosPago datosPago) {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) {
            throw new IllegalStateException("Solo los clientes pueden realizar compras");
        }
        pedidoService.confirmarCompra(cliente, tipoPago, datosPago);
        // El cliente estuvo presente → las notificaciones generadas se marcan como vistas
        cliente.tomarNotificaciones();
    }

    public List<String> tomarNotificaciones() {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) return List.of();
        return cliente.tomarNotificaciones();
    }

    public Pedido actualizarEstadoPedido(int idPedido) {
        verificarAutenticacion();
        // Solo administradores pueden cambiar el estado de un pedido
        if (!(autService.getUsuarioActual() instanceof Administrador)) {
            throw new IllegalStateException("Solo los administradores pueden actualizar el estado de un pedido");
        }
        return pedidoService.actualizarEstadoPedido(idPedido);
    }

    public List<Pedido> listarPedidosCliente() {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) {
            throw new IllegalStateException("Solo los clientes tienen pedidos asociados");
        }
        return pedidoService.listarPedidosPorCliente(cliente);
    }

    public List<Pedido> listarTodosLosPedidos() {
        verificarAutenticacion();
        if (!(autService.getUsuarioActual() instanceof Administrador)) {
            throw new IllegalStateException("Solo los administradores pueden ver todos los pedidos");
        }
        return pedidoService.listarTodosLosPedidos();
    }

    // ── Acceso a servicios (para setup de demo) ───────────────────────────────

    public CatalogoService getCatService() { return catService; }
    public AutenticacionService getAutService() { return autService; }

    // ── Privado ──────────────────────────────────────────────────────────────

    private void verificarAutenticacion() {
        if (autService.estaAutenticado()) {
            throw new IllegalStateException("Debe iniciar sesión primero");
        }
    }
}
