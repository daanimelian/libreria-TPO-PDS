package emarket.facade;

import emarket.auth.Administrador;
import emarket.auth.AutenticacionService;
import emarket.auth.Cliente;
import emarket.auth.Usuario;
import emarket.carrito.CarritoService;
import emarket.carrito.ItemCarrito;
import emarket.catalogo.Categoria;
import emarket.catalogo.CatalogoService;
import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import emarket.notificacion.CanalNotificacion;
import emarket.pago.DatosPago;
import emarket.pago.MetodoPagoFactory;
import emarket.pago.TipoPago;
import java.util.Arrays;
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

    public void registrarAdministrador(String username, String pass, String claveAdmin) {
        autService.registrarAdministrador(username, pass, claveAdmin);
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
        Producto p = catService.buscarProductoPorId(idProducto);
        if (p == null) throw new IllegalStateException("Producto no encontrado: id=" + idProducto);
        if (!catService.verificarDisponibilidad(idProducto, cantidad))
            throw new IllegalStateException("Stock insuficiente para: " + p.getNombre());
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

    // ── Estado de sesión ─────────────────────────────────────────────────────

    public boolean estaAutenticado()   { return autService.estaAutenticado(); }
    public boolean esCliente()         { return autService.getClienteActual() != null; }
    public String getUsernameActual()  {
        Usuario u = autService.getUsuarioActual();
        return u != null ? u.getUsername() : null;
    }

    // ── Carrito (consulta) ───────────────────────────────────────────────────

    public List<ItemCarrito> getItemsCarrito() {
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) throw new IllegalStateException("Solo los clientes tienen carrito");
        return cliente.getCarrito().getItems();
    }

    public double getTotalCarrito() {
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) throw new IllegalStateException("Solo los clientes tienen carrito");
        return cliente.getCarrito().calcularTotal();
    }

    // ── Precarga de datos de demo ────────────────────────────────────────────

    public void precargarDatos() {
        Categoria raiz = new Categoria("Catálogo de Libros");
        catService.setCatalogoRaiz(raiz);

        Categoria ficcion = catService.crearCategoria("Ficción", raiz);
        catService.agregarProducto(ficcion, new Producto(1, "Cien años de soledad",  2500.0,  8));
        catService.agregarProducto(ficcion, new Producto(2, "El principito",         1800.0, 12));
        catService.agregarProducto(ficcion, new Producto(3, "1984",                  2200.0,  6));

        Categoria tecnicos = catService.crearCategoria("Técnicos", raiz);
        catService.agregarProducto(tecnicos, new Producto(4, "Clean Code",           4500.0,  5));
        catService.agregarProducto(tecnicos, new Producto(5, "Design Patterns",      5000.0,  3));

        Categoria historia = catService.crearCategoria("Historia", raiz);
        catService.agregarProducto(historia, new Producto(6, "Sapiens",              3200.0, 10));
        catService.agregarProducto(historia, new Producto(7, "El arte de la guerra", 1500.0, 15));

        registrarClienteCompleto(
                "juan", "1234", "Av. Corrientes 1234, CABA",
                "juan@email.com", "1155551234", "TOKEN_JUAN",
                Arrays.asList(CanalNotificacion.EMAIL, CanalNotificacion.PUSH));

        registrarAdministrador("admin", "admin123");
    }

    // ── Privado ──────────────────────────────────────────────────────────────

    private void verificarAutenticacion() {
        if (!autService.estaAutenticado()) {
            throw new IllegalStateException("Debe iniciar sesión primero");
        }
    }
}
