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

    public void agregarProductoEnCategoria(String nombreCategoria, Producto producto) {
        Categoria cat = buscarCategoriaPorNombre(catalogoRaiz, nombreCategoria);
        if (cat == null) throw new IllegalArgumentException("Categoría no encontrada: " + nombreCategoria);
        cat.agregarComponente(producto);
    }

    public void agregarCategoria(String nombre, String nombrePadre) {
        Categoria padre = buscarCategoriaPorNombre(catalogoRaiz, nombrePadre);
        if (padre == null) throw new IllegalArgumentException("Categoría padre no encontrada: " + nombrePadre);
        padre.agregarComponente(new Categoria(nombre));
    }

    public void modificarStock(int id, int nuevoStock) {
        Producto p = buscarProductoPorId(id);
        if (p == null) throw new IllegalArgumentException("Producto no encontrado: " + id);
        if (nuevoStock < 0) throw new IllegalArgumentException("El stock no puede ser negativo");
        p.setStock(nuevoStock);
    }

    private Categoria buscarCategoriaPorNombre(ComponenteCatalogo componente, String nombre) {
        if (componente instanceof Categoria cat) {
            if (cat.getNombre().equalsIgnoreCase(nombre)) return cat;
            for (ComponenteCatalogo hijo : cat.getHijos()) {
                Categoria encontrada = buscarCategoriaPorNombre(hijo, nombre);
                if (encontrada != null) return encontrada;
            }
        }
        return null;
    }
}
