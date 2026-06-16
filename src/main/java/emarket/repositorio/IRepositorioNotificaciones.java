package emarket.repositorio;

import emarket.notificacion.Notificacion;
import java.util.List;

public interface IRepositorioNotificaciones {
    void guardar(String usernameCliente, String mensaje);
    List<Notificacion> listarNoVistas(String usernameCliente);
    void marcarTodasComoVistas(String usernameCliente);
}
