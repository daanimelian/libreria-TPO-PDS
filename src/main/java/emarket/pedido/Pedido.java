package emarket.pedido;

import emarket.auth.Cliente;
import emarket.estado.EstadoPedido;
import emarket.notificacion.EventoNotificacion;
import emarket.notificacion.ObservadorNotificacion;
import emarket.notificacion.SujetoObservable;
import java.util.ArrayList;
import java.util.List;

// Observer sujeto + State: el pedido notifica cambios de estado a sus observadores
public class Pedido implements SujetoObservable {

    private final int id;
    private EstadoPedido estadoActual;
    private final List<ObservadorNotificacion> observadores = new ArrayList<>();
    private final List<ItemPedido> items;
    private final Cliente cliente;
    private double total;

    public Pedido(int id, Cliente cliente, List<ItemPedido> items, double total) {
        this.id = id;
        this.cliente = cliente;
        this.items = items;
        this.total = total;
    }

    // Cambia el estado y notifica automáticamente a los observadores
    public void setEstado(EstadoPedido estado) {
        this.estadoActual = estado;
        notificarCambios();
    }

    public EstadoPedido getEstado() { return estadoActual; }

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

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public double getTotal() { return total; }
    public List<ItemPedido> getItems() { return items; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Pedido #%d | Estado: %s | Total: $%.2f%n",
                id, estadoActual != null ? estadoActual.getNombre() : "SIN ESTADO", total));
        for (ItemPedido item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}
