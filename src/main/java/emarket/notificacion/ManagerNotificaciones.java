package emarket.notificacion;

import emarket.auth.Cliente;
import emarket.pedido.Pedido;

// Observer concreto: recibe cambios de estado y los reenvía por cada canal preferido del cliente
public class ManagerNotificaciones implements ObservadorNotificacion {

    private EstrategiaNotificacionFactory notificacionFactory = new EstrategiaNotificacionFactory();

    @Override
    public void actualizar(Pedido pedido) {
        Cliente cliente = pedido.getCliente();
        String mensaje = "Tu pedido #" + pedido.getId()
                + " cambió a estado: " + pedido.getEstado().getNombre();
        enviarACanales(mensaje, cliente);
    }

    private void enviarACanales(String mensaje, Cliente cliente) {
        for (CanalNotificacion canal : cliente.getCanalesPreferidos()) {
            EstrategiaNotificacion estrategia = notificacionFactory.crearNotificacion(canal);
            String destinatario = cliente.getDestinatarioPara(canal);
            estrategia.enviarMensaje(mensaje, destinatario);
        }
    }
}
