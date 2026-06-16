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
import emarket.notificacion.EstrategiaNotificacionFactory;
import emarket.notificacion.ManagerNotificaciones;
import emarket.notificacion.Notificacion;
import emarket.pago.DatosPago;
import emarket.pago.MetodoPagoFactory;
import emarket.pago.TipoPago;
import emarket.pedido.Pedido;
import emarket.pedido.PedidoService;
import emarket.repositorio.IRepositorioCatalogo;
import emarket.repositorio.IRepositorioNotificaciones;
import emarket.repositorio.IRepositorioPedidos;
import emarket.repositorio.IRepositorioUsuarios;
import emarket.repositorio.factory.InMemoryRepositorioFactory;
import emarket.repositorio.factory.RepositorioFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Facade: punto de entrada único para todas las operaciones del sistema.
 *
 * También actúa como raíz de composición (Composition Root): recibe una
 * RepositorioFactory y delega la creación de todos los repositorios en ella,
 * sin conocer qué implementación concreta se está usando (DIP).
 */
public class LibreriaFacade {

    private final AutenticacionService autService;
    private final CatalogoService catService;
    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final MetodoPagoFactory metodoPagoFactory;
    private final IRepositorioNotificaciones repoNotificaciones;

    // Constructor principal: recibe la factory → DIP explícito
    public LibreriaFacade(RepositorioFactory factory) {
        IRepositorioUsuarios       repoUsuarios = factory.crearRepositorioUsuarios();
        IRepositorioPedidos        repoPedidos  = factory.crearRepositorioPedidos();
        IRepositorioCatalogo       repoCatalogo = factory.crearRepositorioCatalogo();
        IRepositorioNotificaciones repoNotif    = factory.crearRepositorioNotificaciones();

        ManagerNotificaciones manager = EstrategiaNotificacionFactory.crearManager(repoNotif);

        this.autService          = new AutenticacionService(repoUsuarios);
        this.catService          = new CatalogoService(repoCatalogo);
        this.carritoService      = new CarritoService(catService);
        this.pedidoService       = new PedidoService(repoPedidos, repoCatalogo, manager);
        this.metodoPagoFactory   = new MetodoPagoFactory();
        this.repoNotificaciones  = repoNotif;
    }

    // Constructor de conveniencia: usa la implementación en memoria por defecto
    public LibreriaFacade() {
        this(new InMemoryRepositorioFactory());
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
        List<Notificacion> noVistas = repoNotificaciones.listarNoVistas(cliente.getUsername());
        repoNotificaciones.marcarTodasComoVistas(cliente.getUsername());
        return noVistas.stream().map(Notificacion::toString).collect(Collectors.toList());
    }

    public Pedido actualizarEstadoPedido(int idPedido) {
        verificarAutenticacion();
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

    public boolean estaAutenticado()  { return autService.estaAutenticado(); }
    public boolean esCliente()        { return autService.getClienteActual() != null; }
    public String getUsernameActual() {
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

    // ── Precarga de datos de demo (idempotente) ──────────────────────────────

    public void precargarDatos() {
        // Si el catálogo ya tiene datos (modo JDBC con DB populada), no re-seedear
        if (catService.listarCatalogo() != null) return;

        Categoria raiz = catService.crearCategoria("Catálogo de Libros", null);

        Categoria ficcion = catService.crearCategoria("Ficción", raiz);
        catService.agregarProducto(ficcion, new Producto(1, "Cien años de soledad",  2500.0,  8));
        catService.agregarProducto(ficcion, new Producto(2, "El principito",         1800.0, 12));

        // Subcategoría anidada dentro de Ficción — demuestra el patrón Composite
        // con más de un nivel: Catálogo → Ficción → Fantasía → Producto
        Categoria fantasia = catService.crearCategoria("Fantasía", ficcion);
        catService.agregarProducto(fantasia, new Producto(3, "El Señor de los Anillos", 3800.0, 5));

        Categoria tecnicos = catService.crearCategoria("Técnicos", raiz);
        catService.agregarProducto(tecnicos, new Producto(0, "Clean Code",           4500.0,  5));
        catService.agregarProducto(tecnicos, new Producto(0, "Design Patterns",      5000.0,  3));

        Categoria historia = catService.crearCategoria("Historia", raiz);
        catService.agregarProducto(historia, new Producto(0, "Sapiens",              3200.0, 10));
        catService.agregarProducto(historia, new Producto(0, "El arte de la guerra", 1500.0, 15));

        registrarClienteCompleto(
                "juan", "juan1234", "Av. Corrientes 1234, CABA",
                "juan@email.com", "1155551234", "TOKEN_JUAN",
                Arrays.asList(CanalNotificacion.EMAIL, CanalNotificacion.PUSH));

        registrarAdministrador("admin", "admin123", "admin123");
    }

    // ── Privado ──────────────────────────────────────────────────────────────

    private void verificarAutenticacion() {
        if (!autService.estaAutenticado()) {
            throw new IllegalStateException("Debe iniciar sesión primero");
        }
    }
}
