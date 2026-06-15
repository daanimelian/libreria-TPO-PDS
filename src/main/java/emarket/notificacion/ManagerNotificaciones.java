package emarket.notificacion;

import emarket.auth.Cliente;

// Observer concreto: recibe cambios de estado y los reenvía por cada canal preferido del cliente
public class ManagerNotificaciones implements ObservadorNotificacion {

    @Override
    public void actualizar(EventoNotificacion evento) {
        Cliente cliente = evento.getCliente();
        String mensaje = "Tu pedido #" + evento.getIdPedido()
                + " cambió a estado: " + evento.getEstadoNombre();
        enviarACanales(mensaje, cliente);
    }

    private void enviarACanales(String mensaje, Cliente cliente) {
        for (CanalNotificacion canal : cliente.getCanalesPreferidos()) {
            EstrategiaNotificacion estrategia = EstrategiaNotificacionFactory.crearNotificacion(canal);
            String destinatario = cliente.getDestinatarioPara(canal);
            estrategia.enviarMensaje(mensaje, destinatario);
        }
    }
}
