package emarket.repositorio;

import emarket.catalogo.Categoria;
import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import java.util.Optional;

public interface IRepositorioCatalogo {
    ComponenteCatalogo obtenerRaiz();
    Optional<Producto> buscarProductoPorId(int id);
    void guardarCategoria(Categoria categoria, Categoria padre);
    void guardarProducto(Producto producto, Categoria categoria);
    void actualizarStock(int idProducto, int nuevoStock);
}
