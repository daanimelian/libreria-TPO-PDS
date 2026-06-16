package emarket.repositorio.inmemory;

import emarket.catalogo.Categoria;
import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import emarket.repositorio.IRepositorioCatalogo;
import java.util.Optional;

public class InMemoryRepositorioCatalogo implements IRepositorioCatalogo {

    private ComponenteCatalogo raiz;

    @Override
    public ComponenteCatalogo obtenerRaiz() {
        return raiz;
    }

    @Override
    public Optional<Producto> buscarProductoPorId(int id) {
        return Optional.ofNullable(buscarRecursivo(raiz, id));
    }

    @Override
    public void guardarCategoria(Categoria categoria, Categoria padre) {
        if (padre == null) {
            raiz = categoria;
        } else {
            padre.agregarComponente(categoria);
        }
    }

    @Override
    public void guardarProducto(Producto producto, Categoria categoria) {
        categoria.agregarComponente(producto);
    }

    @Override
    public void actualizarStock(int idProducto, int nuevoStock) {
        // Los objetos Producto en el árbol Composite son referencias compartidas:
        // reducirStock() ya actualizó el campo en la instancia; no hay copia extra que sincronizar.
    }

    private Producto buscarRecursivo(ComponenteCatalogo componente, int id) {
        if (componente instanceof Producto p && p.getId() == id) return p;
        if (componente instanceof Categoria cat) {
            for (ComponenteCatalogo hijo : cat.getHijos()) {
                Producto encontrado = buscarRecursivo(hijo, id);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}
