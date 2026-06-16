package emarket.repositorio.inmemory;

import emarket.auth.Usuario;
import emarket.repositorio.IRepositorioUsuarios;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InMemoryRepositorioUsuarios implements IRepositorioUsuarios {

    private final List<Usuario> usuarios = new ArrayList<>();

    @Override
    public void guardar(Usuario usuario) {
        usuarios.add(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarios.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public boolean existeUsername(String username) {
        return usuarios.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    @Override
    public List<Usuario> listarTodos() {
        return Collections.unmodifiableList(usuarios);
    }
}
