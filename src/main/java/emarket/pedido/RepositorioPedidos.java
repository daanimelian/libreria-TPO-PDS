package emarket.pedido;

import emarket.auth.Cliente;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositorioPedidos {

    private Map<Integer, Pedido> pedidos = new HashMap<>();
    private int proximoId = 1;

    public void guardar(Pedido p) {
        pedidos.put(p.getId(), p);
    }

    public Pedido buscarPorId(int id) {
        return pedidos.get(id);
    }

    public List<Pedido> listarTodos() {
        return new ArrayList<>(pedidos.values());
    }

    public List<Pedido> listarPorCliente(Cliente cliente) {
        return pedidos.values().stream()
                .filter(p -> p.getCliente().getUsername().equals(cliente.getUsername()))
                .collect(Collectors.toList());
    }

    public int getProximoId() {
        return proximoId++;
    }
}
