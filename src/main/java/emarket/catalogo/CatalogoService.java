package emarket.catalogo;

public class CatalogoService {

    private ComponenteCatalogo catalogoRaiz;

    public CatalogoService() {
        this.catalogoRaiz = new Categoria("Catálogo");
    }

    public void setCatalogoRaiz(ComponenteCatalogo raiz) {
        this.catalogoRaiz = raiz;
    }

    public Producto buscarProductoPorId(int id) {
        return buscarRecursivo(catalogoRaiz, id);
    }

    private Producto buscarRecursivo(ComponenteCatalogo componente, int id) {
        if (componente instanceof Producto p) {
            if (p.getId() == id) return p;
        } else if (componente instanceof Categoria cat) {
            for (ComponenteCatalogo hijo : cat.getHijos()) {
                Producto encontrado = buscarRecursivo(hijo, id);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    public ComponenteCatalogo listarCatalogo() {
        return catalogoRaiz;
    }

    public boolean verificarDisponibilidad(int id, int cantidad) {
        Producto p = buscarProductoPorId(id);
        return p != null && p.verificarStock(cantidad);
    }

    public void agregarProducto(Categoria categoria, Producto producto) {
        categoria.agregarComponente(producto);
    }

    public Categoria crearCategoria(String nombre, Categoria padre) {
        Categoria nueva = new Categoria(nombre);
        if (padre != null) padre.agregarComponente(nueva);
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
