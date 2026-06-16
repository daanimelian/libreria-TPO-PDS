package emarket.catalogo;

import emarket.repositorio.IRepositorioCatalogo;

public class CatalogoService {

    private final IRepositorioCatalogo repositorio;

    public CatalogoService(IRepositorioCatalogo repositorio) {
        this.repositorio = repositorio;
    }

    public Producto buscarProductoPorId(int id) {
        return repositorio.buscarProductoPorId(id).orElse(null);
    }

    public ComponenteCatalogo listarCatalogo() {
        return repositorio.obtenerRaiz();
    }

    public boolean verificarDisponibilidad(int id, int cantidad) {
        return repositorio.buscarProductoPorId(id)
                .map(p -> p.verificarStock(cantidad))
                .orElse(false);
    }

    public void agregarProducto(Categoria categoria, Producto producto) {
        repositorio.guardarProducto(producto, categoria);
    }

    public Categoria crearCategoria(String nombre, Categoria padre) {
        Categoria nueva = new Categoria(nombre);
        repositorio.guardarCategoria(nueva, padre);
        return nueva;
    }
}
