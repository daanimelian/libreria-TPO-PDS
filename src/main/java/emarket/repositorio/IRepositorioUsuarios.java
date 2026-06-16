package emarket.repositorio;

import emarket.auth.Usuario;
import java.util.List;
import java.util.Optional;

public interface IRepositorioUsuarios {
    void guardar(Usuario usuario);
    Optional<Usuario> buscarPorUsername(String username);
    boolean existeUsername(String username);
    List<Usuario> listarTodos();
}
