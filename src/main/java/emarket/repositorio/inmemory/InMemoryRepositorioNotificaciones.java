package emarket.repositorio.inmemory;

import emarket.notificacion.Notificacion;
import emarket.repositorio.IRepositorioNotificaciones;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryRepositorioNotificaciones implements IRepositorioNotificaciones {

    private final Map<String, List<Notificacion>> notificaciones = new HashMap<>();

    @Override
    public void guardar(String usernameCliente, String mensaje) {
        notificaciones
                .computeIfAbsent(usernameCliente, k -> new ArrayList<>())
                .add(new Notificacion(mensaje));
    }

    @Override
    public List<Notificacion> listarNoVistas(String usernameCliente) {
        return notificaciones.getOrDefault(usernameCliente, List.of()).stream()
                .filter(n -> !n.isVisto())
                .collect(Collectors.toList());
    }

    @Override
    public void marcarTodasComoVistas(String usernameCliente) {
        notificaciones.getOrDefault(usernameCliente, List.of())
                .forEach(Notificacion::marcarVisto);
    }
}
