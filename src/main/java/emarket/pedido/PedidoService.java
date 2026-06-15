package emarket.pedido;

import emarket.auth.Cliente;
import emarket.carrito.Carrito;
import emarket.carrito.ItemCarrito;
import emarket.config.ConfiguracionSistema;
import emarket.estado.EstadoPedido;
import emarket.estado.EstadoPendiente;
import emarket.notificacion.EstrategiaNotificacionFactory;
import emarket.notificacion.ManagerNotificaciones;
import emarket.pago.DatosPago;
import emarket.pago.ProcesadorPagos;
import emarket.pago.TipoPago;
import java.util.ArrayList;
import java.util.List;

public class PedidoService {

    private RepositorioPedidos repoPedidos;
    private ProcesadorPagos procesadorPagos;
    private ManagerNotificaciones managerNotificaciones;

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

        // Registrar observer y setear estado inicial (dispara notificación PENDIENTE)
        registrarObservadores(pedido);
        pedido.setEstado(new EstadoPendiente());

        // Procesar el cobro
        boolean cobrado = procesadorPagos.procesarCobro(pedido, tipoPago, datosPago);
        if (!cobrado) {
            throw new IllegalStateException("El pago fue rechazado. Verificá los datos ingresados.");
        }

        repoPedidos.guardar(pedido);
        carrito.vaciarCarrito();

        return pedido;
    }

    public void actualizarEstadoPedido(int idPedido, EstadoPedido nuevoEstado) {
        Pedido pedido = repoPedidos.buscarPorId(idPedido);
        if (pedido == null) {
            throw new IllegalStateException("Pedido no encontrado: #" + idPedido);
        }
        pedido.setEstado(nuevoEstado);
    }

    public Pedido buscarPedido(int id) {
        return repoPedidos.buscarPorId(id);
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
