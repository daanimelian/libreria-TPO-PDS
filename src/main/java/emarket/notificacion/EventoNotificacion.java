package emarket.notificacion;

import emarket.auth.Cliente;

// DTO que el sujeto pasa a los observadores, evitando que la interfaz Observer dependa de Pedido
public class EventoNotificacion {

    private final int idPedido;
    private final String estadoNombre;
    private final Cliente cliente;

    public EventoNotificacion(int idPedido, String estadoNombre, Cliente cliente) {
        this.idPedido = idPedido;
        this.estadoNombre = estadoNombre;
        this.cliente = cliente;
    }

    public int getIdPedido()        { return idPedido; }
    public String getEstadoNombre() { return estadoNombre; }
    public Cliente getCliente()     { return cliente; }
}
