package emarket.pedido;

import emarket.auth.Cliente;
import emarket.carrito.Carrito;
import emarket.carrito.ItemCarrito;
import emarket.catalogo.Producto;
import emarket.config.ConfiguracionSistema;
import emarket.estado.EstadoPendiente;
import emarket.notificacion.ManagerNotificaciones;
import emarket.pago.DatosPago;
import emarket.pago.ProcesadorPagos;
import emarket.pago.TipoPago;
import emarket.repositorio.IRepositorioCatalogo;
import emarket.repositorio.IRepositorioPedidos;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que orquesta el ciclo de vida de los pedidos.
 *
 * <p>Coordina la creación de pedidos ({@link #confirmarCompra}), la reducción de
 * stock, el procesamiento del pago y las transiciones de estado con sus notificaciones.
 * Aplica el principio Creator (GRASP): crea {@link Pedido} e {@link ItemPedido} porque
 * posee todos los datos necesarios (carrito y cliente).
 */
public class PedidoService {

    private final IRepositorioPedidos repoPedidos;
    private final IRepositorioCatalogo repoCatalogo;
    private final ProcesadorPagos procesadorPagos;
    private final ManagerNotificaciones managerNotificaciones;

    /**
     * @param repoPedidos           repositorio para persistir pedidos
     * @param repoCatalogo          repositorio para actualizar stock de productos
     * @param managerNotificaciones observador que envía notificaciones al cliente
     */
    public PedidoService(IRepositorioPedidos repoPedidos,
                         IRepositorioCatalogo repoCatalogo,
                         ManagerNotificaciones managerNotificaciones) {
        this.repoPedidos           = repoPedidos;
        this.repoCatalogo          = repoCatalogo;
        this.procesadorPagos       = new ProcesadorPagos();
        this.managerNotificaciones = managerNotificaciones;
    }

    /**
     * Confirma la compra del cliente: genera el pedido, reduce stock, procesa el pago
     * y avanza el estado de {@code PENDIENTE} a {@code PAGADO} disparando el Observer.
     *
     * <p>Flujo de ejecución:
     * <ol>
     *   <li>Valida que el carrito no esté vacío.</li>
     *   <li>Genera {@link ItemPedido} como snapshot inmutable de los productos.</li>
     *   <li>Calcula el total con IVA obtenido de {@link ConfiguracionSistema}.</li>
     *   <li>Reduce el stock de cada producto y persiste el cambio.</li>
     *   <li>Asigna estado inicial {@code PENDIENTE} (sin observers aún: no genera notificación).</li>
     *   <li>Registra {@code ManagerNotificaciones} como observer.</li>
     *   <li>Procesa el pago con la estrategia elegida.</li>
     *   <li>Avanza al estado {@code PAGADO} → dispara Observer → notifica al cliente.</li>
     *   <li>Persiste el pedido y vacía el carrito.</li>
     * </ol>
     *
     * @param cliente    cliente que realiza la compra
     * @param tipoPago   método de pago elegido
     * @param datosPago  datos del método de pago ya recolectados por la UI
     * @return el pedido creado con estado {@code PAGADO}
     * @throws IllegalStateException si el carrito está vacío o el pago es rechazado
     */
    public Pedido confirmarCompra(Cliente cliente, TipoPago tipoPago, DatosPago datosPago) {
        Carrito carrito = cliente.getCarrito();
        if (carrito.estaVacio()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        List<ItemPedido> items = generarItemsPedido(carrito);

        double subtotal  = carrito.calcularTotal();
        double impuestos = ConfiguracionSistema.getInstance().getImpuestos();
        double total     = subtotal * (1 + impuestos);

        int id        = repoPedidos.getProximoId();
        Pedido pedido = new Pedido(id, cliente, items, total, tipoPago);

        for (ItemCarrito item : carrito.getItems()) {
            Producto p = item.getProducto();
            p.reducirStock(item.getCantidad());
            repoCatalogo.actualizarStock(p.getId(), p.getStock());
        }

        System.out.printf("  Pedido #%d creado | Subtotal: $%.2f | IVA (%.0f%%): $%.2f | Total: $%.2f%n",
                id, subtotal, impuestos * 100, total - subtotal, total);

        // Estado inicial ANTES de registrar observers → no dispara notificación de PENDIENTE
        pedido.setEstado(new EstadoPendiente());
        pedido.agregarObservador(managerNotificaciones);

        boolean cobrado = procesadorPagos.procesarCobro(pedido, tipoPago, datosPago);
        if (!cobrado) {
            throw new IllegalStateException("El pago fue rechazado. Verificá los datos ingresados.");
        }

        // Pago exitoso: PENDIENTE → PAGADO (State + Observer)
        pedido.avanzarEstado();

        repoPedidos.guardar(pedido);
        repoPedidos.actualizarEstado(pedido.getId(), pedido.getEstado().getNombre());
        carrito.vaciarCarrito();

        return pedido;
    }

    /**
     * Avanza el estado de un pedido existente al siguiente en la cadena.
     * Re-registra el observer porque los pedidos cargados desde BD no tienen observadores.
     *
     * @param idPedido identificador del pedido a avanzar
     * @return el pedido con su nuevo estado
     * @throws IllegalStateException si el pedido no existe o ya está en estado terminal
     */
    public Pedido actualizarEstadoPedido(int idPedido) {
        Pedido pedido = repoPedidos.buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalStateException("Pedido no encontrado: #" + idPedido));
        pedido.agregarObservador(managerNotificaciones);
        pedido.avanzarEstado();
        repoPedidos.actualizarEstado(pedido.getId(), pedido.getEstado().getNombre());
        return pedido;
    }

    /**
     * Lista todos los pedidos de un cliente específico.
     *
     * @param cliente cliente cuyos pedidos se desean consultar
     * @return lista de pedidos del cliente, posiblemente vacía
     */
    public List<Pedido> listarPedidosPorCliente(Cliente cliente) {
        return repoPedidos.listarPorCliente(cliente.getUsername());
    }

    /**
     * Lista todos los pedidos registrados en el sistema (acceso solo para administradores).
     *
     * @return lista completa de pedidos
     */
    public List<Pedido> listarTodosLosPedidos() {
        return repoPedidos.listarTodos();
    }

    /** Genera los ítems del pedido como snapshot inmutable de nombre y precio del carrito. */
    private List<ItemPedido> generarItemsPedido(Carrito carrito) {
        List<ItemPedido> items = new ArrayList<>();
        for (ItemCarrito itemCarrito : carrito.getItems()) {
            items.add(new ItemPedido(
                    itemCarrito.getProducto().getNombre(),
                    itemCarrito.getProducto().getPrecio(),
                    itemCarrito.getCantidad()
            ));
        }
        return items;
    }
}
