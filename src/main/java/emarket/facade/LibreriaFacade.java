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
import java.util.stream.Collectors;

/**
 * Punto de entrada único al sistema (patrón Facade).
 *
 * <p>Encapsula todos los subsistemas ({@code auth}, {@code catalogo}, {@code carrito},
 * {@code pedido}) detrás de una interfaz cohesiva. La capa de presentación ({@code Main}
 * o la UI Swing) solo interactúa con esta clase; nunca con los servicios internos.
 *
 * <p>También actúa como raíz de composición (<em>Composition Root</em>): recibe una
 * {@link RepositorioFactory} e instancia todos los servicios con sus dependencias
 * concretas, sin que ningún servicio conozca la implementación de persistencia
 * (principio DIP).
 */
public class LibreriaFacade {

    private final AutenticacionService autService;
    private final CatalogoService catService;
    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final IRepositorioNotificaciones repoNotificaciones;

    /**
     * Constructor principal: recibe la factory de repositorios (DIP explícito).
     *
     * @param factory implementación de persistencia a usar ({@code InMemory} o {@code Jdbc})
     */
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
        this.repoNotificaciones  = repoNotif;
    }

    /**
     * Constructor de conveniencia: usa la implementación en memoria (sin base de datos).
     * Útil para pruebas y demos rápidas.
     */
    public LibreriaFacade() {
        this(new InMemoryRepositorioFactory());
    }

    // ── Autenticación ────────────────────────────────────────────────────────

    /**
     * Intenta autenticar al usuario con las credenciales proporcionadas.
     *
     * @param username nombre de usuario registrado
     * @param pass     contraseña en texto plano (se verifica contra el hash almacenado)
     * @return {@code true} si las credenciales son válidas y la sesión fue iniciada
     */
    public boolean iniciarSesion(String username, String pass) {
        return autService.iniciarSesion(username, pass);
    }

    /** Cierra la sesión del usuario actualmente autenticado. */
    public void cerrarSesion() {
        autService.cerrarSesion();
    }

    /**
     * Registra un nuevo cliente en el sistema.
     *
     * @param username          nombre de usuario (mín. 4 caracteres, único)
     * @param pass              contraseña (mín. 8 caracteres, debe contener al menos un dígito)
     * @param direccion         domicilio de entrega
     * @param email             correo electrónico para notificaciones
     * @param telefono          teléfono de 10 dígitos para notificaciones SMS
     * @param tokenDispositivo  token de dispositivo para notificaciones push
     * @param canalesPreferidos canales de notificación preferidos ({@code EMAIL}, {@code SMS}, {@code PUSH})
     * @return el cliente recién creado
     * @throws IllegalArgumentException si algún dato no cumple las validaciones
     */
    public Cliente registrarClienteCompleto(String username, String pass, String direccion,
                                             String email, String telefono, String tokenDispositivo,
                                             List<CanalNotificacion> canalesPreferidos) {
        return autService.registrarClienteCompleto(
                username, pass, direccion, email, telefono, tokenDispositivo, canalesPreferidos);
    }

    /**
     * Registra un nuevo administrador en el sistema.
     *
     * @param username   nombre de usuario (mín. 4 caracteres, único)
     * @param pass       contraseña (mín. 8 caracteres, debe contener al menos un dígito)
     * @param claveAdmin clave secreta de autorización para crear administradores
     * @throws IllegalArgumentException si algún dato no cumple las validaciones
     */
    public void registrarAdministrador(String username, String pass, String claveAdmin) {
        autService.registrarAdministrador(username, pass, claveAdmin);
    }

    // ── Catálogo ─────────────────────────────────────────────────────────────

    /**
     * Busca un producto por su identificador único.
     *
     * @param id identificador del producto
     * @return el producto encontrado, o {@code null} si no existe
     * @throws IllegalStateException si no hay sesión activa
     */
    public Producto buscarProducto(int id) {
        verificarAutenticacion();
        return catService.buscarProductoPorId(id);
    }

    /**
     * Devuelve la raíz del árbol de catálogo (patrón Composite).
     *
     * @return componente raíz que contiene toda la jerarquía de categorías y productos
     * @throws IllegalStateException si no hay sesión activa
     */
    public ComponenteCatalogo listarCatalogo() {
        verificarAutenticacion();
        return catService.listarCatalogo();
    }

    // ── Carrito ──────────────────────────────────────────────────────────────

    /**
     * Agrega un producto al carrito del cliente autenticado.
     *
     * @param idProducto identificador del producto a agregar
     * @param cantidad   cantidad deseada (debe ser mayor a 0)
     * @throws IllegalStateException si no hay sesión de cliente, si el producto no existe
     *                               o si el stock es insuficiente
     */
    public void agregarProductoAlCarrito(int idProducto, int cantidad) {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) {
            throw new IllegalStateException("Solo los clientes pueden agregar productos al carrito");
        }
        carritoService.agregarProducto(cliente, idProducto, cantidad);
    }

    /**
     * Elimina un producto del carrito del cliente autenticado.
     *
     * @param idProducto identificador del producto a eliminar
     * @throws IllegalStateException si el producto no está en el carrito
     */
    public void eliminarProductoDelCarrito(int idProducto) {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) throw new IllegalStateException("Solo los clientes tienen carrito");
        Producto p = catService.buscarProductoPorId(idProducto);
        if (p == null) throw new IllegalStateException("Producto no encontrado: id=" + idProducto);
        carritoService.eliminarProducto(cliente, p);
    }

    /**
     * Modifica la cantidad de un producto ya presente en el carrito.
     *
     * @param idProducto identificador del producto
     * @param cantidad   nueva cantidad deseada (debe ser mayor a 0)
     * @throws IllegalStateException si el stock es insuficiente o el producto no está en el carrito
     */
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

    /** Vacía por completo el carrito del cliente autenticado. */
    public void vaciarCarrito() {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) throw new IllegalStateException("Solo los clientes tienen carrito");
        carritoService.vaciarCarrito(cliente);
    }

    // ── Pedidos ──────────────────────────────────────────────────────────────

    /**
     * Confirma la compra usando el método de pago y los datos proporcionados.
     *
     * <p>Flujo interno: genera ítems de pedido (snapshot), calcula total con IVA,
     * reduce stock, procesa el pago con la estrategia elegida y avanza el estado
     * de {@code PENDIENTE} a {@code PAGADO} disparando el Observer de notificaciones.
     *
     * @param tipoPago  método de pago seleccionado
     * @param datosPago datos recolectados por la UI para el método elegido
     * @throws IllegalStateException si el carrito está vacío o el pago es rechazado
     */
    public void confirmarCompra(TipoPago tipoPago, DatosPago datosPago) {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) {
            throw new IllegalStateException("Solo los clientes pueden realizar compras");
        }
        pedidoService.confirmarCompra(cliente, tipoPago, datosPago);
    }

    /**
     * Devuelve las notificaciones no leídas del cliente autenticado y las marca como vistas.
     *
     * @return lista de mensajes de notificación en formato legible
     */
    public List<String> tomarNotificaciones() {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) return List.of();
        List<Notificacion> noVistas = repoNotificaciones.listarNoVistas(cliente.getUsername());
        repoNotificaciones.marcarTodasComoVistas(cliente.getUsername());
        return noVistas.stream().map(Notificacion::toString).collect(Collectors.toList());
    }

    /**
     * Avanza el estado de un pedido al siguiente en la cadena de estados (solo administradores).
     *
     * @param idPedido identificador del pedido a actualizar
     * @return el pedido con su nuevo estado
     * @throws IllegalStateException si el usuario no es administrador, si el pedido no existe
     *                               o si el pedido ya está en estado {@code ENTREGADO}
     */
    public Pedido actualizarEstadoPedido(int idPedido) {
        verificarAutenticacion();
        if (!(autService.getUsuarioActual() instanceof Administrador)) {
            throw new IllegalStateException("Solo los administradores pueden actualizar el estado de un pedido");
        }
        return pedidoService.actualizarEstadoPedido(idPedido);
    }

    /**
     * Lista todos los pedidos del cliente autenticado.
     *
     * @return lista de pedidos del cliente, posiblemente vacía
     * @throws IllegalStateException si el usuario autenticado no es un cliente
     */
    public List<Pedido> listarPedidosCliente() {
        verificarAutenticacion();
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) {
            throw new IllegalStateException("Solo los clientes tienen pedidos asociados");
        }
        return pedidoService.listarPedidosPorCliente(cliente);
    }

    /**
     * Lista todos los pedidos del sistema (solo administradores).
     *
     * @return lista completa de pedidos registrados
     * @throws IllegalStateException si el usuario autenticado no es un administrador
     */
    public List<Pedido> listarTodosLosPedidos() {
        verificarAutenticacion();
        if (!(autService.getUsuarioActual() instanceof Administrador)) {
            throw new IllegalStateException("Solo los administradores pueden ver todos los pedidos");
        }
        return pedidoService.listarTodosLosPedidos();
    }

    // ── Estado de sesión ─────────────────────────────────────────────────────

    /** @return {@code true} si hay un usuario autenticado en este momento */
    public boolean estaAutenticado()  { return autService.estaAutenticado(); }

    /** @return {@code true} si el usuario autenticado es un cliente (no administrador) */
    public boolean esCliente()        { return autService.getClienteActual() != null; }

    /**
     * @return nombre de usuario de la sesión activa, o {@code null} si no hay sesión
     */
    public String getUsernameActual() {
        Usuario u = autService.getUsuarioActual();
        return u != null ? u.getUsername() : null;
    }

    // ── Carrito (consulta) ───────────────────────────────────────────────────

    /**
     * Devuelve una copia de los ítems del carrito del cliente autenticado.
     *
     * @return lista de ítems; nunca {@code null}
     */
    public List<ItemCarrito> getItemsCarrito() {
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) throw new IllegalStateException("Solo los clientes tienen carrito");
        return cliente.getCarrito().getItems();
    }

    /**
     * Calcula el subtotal del carrito (sin impuestos).
     *
     * @return suma de los subtotales de cada ítem
     */
    public double getTotalCarrito() {
        Cliente cliente = autService.getClienteActual();
        if (cliente == null) throw new IllegalStateException("Solo los clientes tienen carrito");
        return cliente.getCarrito().calcularTotal();
    }

    // ── Precarga de datos de demo (idempotente) ──────────────────────────────

    /**
     * Precarga datos de ejemplo: catálogo de libros y usuarios de prueba.
     * Es idempotente: si el catálogo ya tiene datos (ej. modo JDBC con BD populada)
     * no realiza ninguna acción.
     */
    public void precargarDatos() {
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
        catService.agregarProducto(tecnicos, new Producto(4, "Clean Code",           4500.0,  5));
        catService.agregarProducto(tecnicos, new Producto(5, "Design Patterns",      5000.0,  3));

        Categoria historia = catService.crearCategoria("Historia", raiz);
        catService.agregarProducto(historia, new Producto(6, "Sapiens",              3200.0, 10));
        catService.agregarProducto(historia, new Producto(7, "El arte de la guerra", 1500.0, 15));

        registrarClienteCompleto(
                "juan", "juan1234", "Av. Corrientes 1234, CABA",
                "juan@email.com", "1155551234", "TOKEN_JUAN",
                Arrays.asList(CanalNotificacion.EMAIL, CanalNotificacion.PUSH));

        registrarAdministrador("admin", "admin123", "admin123");
    }

    // ── Privado ──────────────────────────────────────────────────────────────

    /** Lanza {@link IllegalStateException} si no hay usuario autenticado. */
    private void verificarAutenticacion() {
        if (!autService.estaAutenticado()) {
            throw new IllegalStateException("Debe iniciar sesión primero");
        }
    }
}
