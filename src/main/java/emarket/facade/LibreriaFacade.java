package emarket.facade;

import emarket.auth.Administrador;
import emarket.auth.AutenticacionService;
import emarket.auth.Cliente;
import emarket.carrito.CarritoService;
import emarket.catalogo.CatalogoService;
import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import emarket.estado.EstadoPedido;
import emarket.notificacion.CanalNotificacion;
import emarket.pago.DatosPago;
import emarket.pago.TipoPago;
import emarket.pedido.Pedido;
import emarket.pedido.PedidoService;
import java.util.List;

// Facade: punto de entrada único para todas las operaciones del sistema
public class LibreriaFacade {

    private AutenticacionService autService;
    private CatalogoService catService;
    private CarritoService carritoService;
    private PedidoService pedidoService;

    public LibreriaFacade() {
        this.autService = new AutenticacionService();
        this.catService = new CatalogoService();
        this.carritoService = new CarritoService(catService);
        this.pedidoService = new PedidoService();
    }

    // ── Autenticación ────────────────────────────────────────────────────────

    public boolean iniciarSesion(String username, String pass) {
        return autService.iniciarSesion(username, pass);
    }

    public void cerrarSesion() {
        autService.cerrarSesion();
    }

    public void registrarCliente(String username, String pass, String direccion) {
        autService.registrarCliente(username, pass, direccion);
    }

    // Registro extendido con todos los datos de contacto para notificaciones
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

    // ── Pedidos ──────────────────────────────────────────────────────────────

    public void confirmarCompra(TipoPago tipoPago, DatosPago datosPago) {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) {
            throw new IllegalStateException("Solo los clientes pueden realizar compras");
        }
        pedidoService.confirmarCompra(cliente, tipoPago, datosPago);
    }

    public void actualizarEstadoPedido(int idPedido, EstadoPedido nuevoEstado) {
        verificarAutenticacion();
        // Solo administradores pueden cambiar el estado de un pedido
        if (!(autService.getUsuarioActual() instanceof Administrador)) {
            throw new IllegalStateException("Solo los administradores pueden actualizar el estado de un pedido");
        }
        pedidoService.actualizarEstadoPedido(idPedido, nuevoEstado);
    }

    public Pedido consultarPedido(int idPedido) {
        verificarAutenticacion();
        return pedidoService.buscarPedido(idPedido);
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
        if (!autService.estaAutenticado()) {
            throw new IllegalStateException("Debe iniciar sesión primero");
        }
    }
}
