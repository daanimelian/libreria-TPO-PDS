package emarket.repositorio.inmemory;

import emarket.pedido.Pedido;
import emarket.repositorio.IRepositorioPedidos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryRepositorioPedidos implements IRepositorioPedidos {

    private final Map<Integer, Pedido> pedidos = new HashMap<>();
    private int proximoId = 1;

    @Override
    public void guardar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    @Override
    public Optional<Pedido> buscarPorId(int id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public List<Pedido> listarTodos() {
        return new ArrayList<>(pedidos.values());
    }

    @Override
    public List<Pedido> listarPorCliente(String usernameCliente) {
        return pedidos.values().stream()
                .filter(p -> p.getCliente().getUsername().equals(usernameCliente))
                .collect(Collectors.toList());
    }

    @Override
    public void actualizarEstado(int id, String estadoNombre) {
        // El objeto Pedido en el mapa ya fue modificado por referencia directa; no se necesita acción extra.
    }

    @Override
    public int getProximoId() {
        return proximoId++;
    }
}
