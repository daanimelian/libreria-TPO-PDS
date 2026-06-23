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

public class PedidoService {

    private final IRepositorioPedidos repoPedidos;
    private final IRepositorioCatalogo repoCatalogo;
    private final ProcesadorPagos procesadorPagos;
    private final ManagerNotificaciones managerNotificaciones;

    public PedidoService(IRepositorioPedidos repoPedidos,
                         IRepositorioCatalogo repoCatalogo,
                         ManagerNotificaciones managerNotificaciones) {
        this.repoPedidos           = repoPedidos;
        this.repoCatalogo          = repoCatalogo;
        this.procesadorPagos       = new ProcesadorPagos();
        this.managerNotificaciones = managerNotificaciones;
    }

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

        // Reducir stock en dominio y persistir el nuevo valor
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

    public Pedido actualizarEstadoPedido(int idPedido) {
        Pedido pedido = repoPedidos.buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalStateException("Pedido no encontrado: #" + idPedido));
        // Re-atar el observer: los pedidos cargados desde DB no tienen observadores registrados
        pedido.agregarObservador(managerNotificaciones);
        pedido.avanzarEstado();
        repoPedidos.actualizarEstado(pedido.getId(), pedido.getEstado().getNombre());
        return pedido;
    }

    public List<Pedido> listarPedidosPorCliente(Cliente cliente) {
        return repoPedidos.listarPorCliente(cliente.getUsername());
    }

    public List<Pedido> listarTodosLosPedidos() {
        return repoPedidos.listarTodos();
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
