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
}
