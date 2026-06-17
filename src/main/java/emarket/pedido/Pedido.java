package emarket.pedido;

import emarket.auth.Cliente;
import emarket.estado.EstadoPedido;
import emarket.notificacion.EventoNotificacion;
import emarket.notificacion.ObservadorNotificacion;
import emarket.notificacion.SujetoObservable;
import emarket.pago.TipoPago;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una orden de compra del cliente (patrón State + patrón Observer — Subject).
 *
 * <p><b>Patrón State</b>: el pedido delega el avance de estado al objeto
 * {@link EstadoPedido} actual mediante {@link #avanzarEstado()}. Cada estado
 * concreto determina su sucesor sin que el pedido conozca la lógica de transición.
 *
 * <p><b>Patrón Observer</b>: implementa {@link SujetoObservable}. Cada vez que el
 * estado cambia ({@link #setEstado(EstadoPedido)}), se notifica automáticamente a
 * todos los observadores registrados (ej: {@link emarket.notificacion.ManagerNotificaciones}).
 */
public class Pedido implements SujetoObservable {

    private final int id;
    private EstadoPedido estadoActual;
    private final List<ObservadorNotificacion> observadores = new ArrayList<>();
    private final List<ItemPedido> items;
    private final Cliente cliente;
    private final TipoPago tipoPago;
    private double total;

    /**
     * @param id       identificador único del pedido (generado por el repositorio)
     * @param cliente  cliente propietario del pedido
     * @param items    snapshot inmutable de los productos al momento de la compra
     * @param total    monto total con impuestos incluidos
     * @param tipoPago método de pago utilizado
     */
    public Pedido(int id, Cliente cliente, List<ItemPedido> items, double total, TipoPago tipoPago) {
        this.id       = id;
        this.cliente  = cliente;
        this.items    = items;
        this.total    = total;
        this.tipoPago = tipoPago;
    }

    /**
     * Cambia el estado actual y notifica automáticamente a todos los observadores.
     *
     * @param estado nuevo estado del pedido
     */
    public void setEstado(EstadoPedido estado) {
        this.estadoActual = estado;
        notificarCambios();
    }

    /** @return estado actual del pedido */
    public EstadoPedido getEstado() { return estadoActual; }

    /**
     * Delega al estado actual para avanzar al siguiente estado en la cadena.
     *
     * @throws IllegalStateException si el pedido ya está en estado terminal ({@code ENTREGADO})
     */
    public void avanzarEstado() {
        estadoActual.procesar(this);
    }

    @Override
    public void agregarObservador(ObservadorNotificacion o) {
        observadores.add(o);
    }

    @Override
    public void eliminarObservador(ObservadorNotificacion o) {
        observadores.remove(o);
    }

    /**
     * Notifica a todos los observadores registrados con el estado actual.
     * Crea un {@link EventoNotificacion} para desacoplar el Observer del tipo {@code Pedido}.
     */
    @Override
    public void notificarCambios() {
        EventoNotificacion evento = new EventoNotificacion(
                id,
                estadoActual != null ? estadoActual.getNombre() : "SIN ESTADO",
                cliente
        );
        for (ObservadorNotificacion o : observadores) {
            o.actualizar(evento);
        }
    }

    /** @return identificador único de este pedido */
    public int getId()          { return id; }

    /** @return cliente propietario del pedido */
    public Cliente getCliente() { return cliente; }

    /** @return monto total con impuestos */
    public double getTotal()    { return total; }

    /** @return lista inmutable de ítems snapshot del pedido */
    public List<ItemPedido> getItems() { return items; }

    /** @return método de pago utilizado para este pedido */
    public TipoPago getTipoPago()      { return tipoPago; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Pedido #%d | Estado: %s | Pago: %s | Total: $%.2f%n",
                id,
                estadoActual != null ? estadoActual.getNombre() : "SIN ESTADO",
                tipoPago,
                total));
        for (ItemPedido item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}
