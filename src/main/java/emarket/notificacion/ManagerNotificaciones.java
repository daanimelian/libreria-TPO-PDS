package emarket.notificacion;

import emarket.auth.Cliente;

// Observer concreto: recibe cambios de estado y los reenvía por cada canal preferido del cliente
public class ManagerNotificaciones implements ObservadorNotificacion {

    @Override
    public void actualizar(EventoNotificacion evento) {
        Cliente cliente = evento.getCliente();
        String mensaje = buildMensaje(evento);
        registrarEnInbox(cliente, mensaje);
        enviarACanales(cliente, mensaje);
    }

    private String buildMensaje(EventoNotificacion evento) {
        return "Tu pedido #" + evento.getIdPedido()
                + " cambió a estado: " + evento.getEstadoNombre();
    }

    private void registrarEnInbox(Cliente cliente, String mensaje) {
        cliente.agregarNotificacion(mensaje);
    }

    private void enviarACanales(Cliente cliente, String mensaje) {
        for (CanalNotificacion canal : cliente.getCanalesPreferidos()) {
            EstrategiaNotificacion estrategia = EstrategiaNotificacionFactory.crearNotificacion(canal);
            estrategia.enviarMensaje(mensaje, cliente.getDestinatarioPara(canal));
        }
    }
}
