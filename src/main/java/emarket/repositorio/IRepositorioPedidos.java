package emarket.repositorio;

import emarket.pedido.Pedido;
import java.util.List;
import java.util.Optional;

public interface IRepositorioPedidos {
    void guardar(Pedido pedido);
    Optional<Pedido> buscarPorId(int id);
    List<Pedido> listarTodos();
    List<Pedido> listarPorCliente(String usernameCliente);
    void actualizarEstado(int id, String estadoNombre);
    int getProximoId();
}
