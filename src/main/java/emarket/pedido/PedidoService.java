package emarket.pedido;

import emarket.auth.Cliente;
import emarket.carrito.Carrito;
import emarket.carrito.ItemCarrito;
import emarket.config.ConfiguracionSistema;
import emarket.estado.EstadoPendiente;
import emarket.notificacion.EstrategiaNotificacionFactory;
import emarket.notificacion.ManagerNotificaciones;
import emarket.pago.DatosPago;
import emarket.pago.ProcesadorPagos;
import emarket.pago.TipoPago;
import java.util.ArrayList;
import java.util.List;

public class PedidoService {

    private final RepositorioPedidos repoPedidos;
    private final ProcesadorPagos procesadorPagos;
    private final ManagerNotificaciones managerNotificaciones;

    public PedidoService() {
        this.repoPedidos = new RepositorioPedidos();
        this.procesadorPagos = new ProcesadorPagos();
        this.managerNotificaciones = EstrategiaNotificacionFactory.crearManager();
    }

    public Pedido confirmarCompra(Cliente cliente, TipoPago tipoPago, DatosPago datosPago) {
        Carrito carrito = cliente.getCarrito();
        if (carrito.estaVacio()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        // Snapshot de ítems: guarda nombre y precio al momento de la compra
        List<ItemPedido> items = generarItemsPedido(carrito);

        // Total con IVA según ConfiguracionSistema (Singleton)
        double subtotal = carrito.calcularTotal();
        double impuestos = ConfiguracionSistema.getInstance().getImpuestos();
        double total = subtotal * (1 + impuestos);

        int id = repoPedidos.getProximoId();
        Pedido pedido = new Pedido(id, cliente, items, total);

        // Reducir stock de cada producto antes de registrar el pedido
        for (ItemCarrito item : carrito.getItems()) {
            item.getProducto().reducirStock(item.getCantidad());
        }

        System.out.printf("  Pedido #%d creado | Subtotal: $%.2f | IVA (%.0f%%): $%.2f | Total: $%.2f%n",
                id, subtotal, impuestos * 100, total - subtotal, total);

        // Estado inicial ANTES de registrar observers → no dispara notificación de PENDIENTE
        pedido.setEstado(new EstadoPendiente());
        registrarObservadores(pedido);

        // Procesar el cobro
        boolean cobrado = procesadorPagos.procesarCobro(pedido, tipoPago, datosPago);
        if (!cobrado) {
            throw new IllegalStateException("El pago fue rechazado. Verificá los datos ingresados.");
        }

        // Pago exitoso: el pedido avanza de PENDIENTE a PAGADO (State + Observer)
        pedido.avanzarEstado();

        repoPedidos.guardar(pedido);
        carrito.vaciarCarrito();

        return pedido;
    }

    // Avanza el pedido al siguiente estado de la cadena PENDIENTE→PAGADO→ENVIADO→ENTREGADO,
    // delegando en el estado actual (State) la transición y la validación de si es posible.
    public Pedido actualizarEstadoPedido(int idPedido) {
        Pedido pedido = repoPedidos.buscarPorId(idPedido);
        if (pedido == null) {
            throw new IllegalStateException("Pedido no encontrado: #" + idPedido);
        }
        pedido.avanzarEstado();
        return pedido;
    }

    public List<Pedido> listarPedidosPorCliente(Cliente cliente) {
        return repoPedidos.listarPorCliente(cliente);
    }

    public List<Pedido> listarTodosLosPedidos() {
        return repoPedidos.listarTodos();
    }

    private void registrarObservadores(Pedido pedido) {
        pedido.agregarObservador(managerNotificaciones);
    }

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
