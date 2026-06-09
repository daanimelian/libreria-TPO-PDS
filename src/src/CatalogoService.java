package catalogo;

/**
 * Servicio del modulo de catalogo.
 *
 * Punto de acceso centralizado al arbol Composite. Es el unico que
 * conoce la raiz y expone operaciones de alto nivel a EMarketFacade,
 * CarritoService y PedidoService sin que estos dependan de la estructura
 * interna del arbol.
 *
 * INTEGRACION: EMarketFacade instancia este servicio y lo inyecta donde
 * sea necesario (CarritoService, PedidoService).
 */
public class CatalogoService {

    private final Categoria catalogoRaiz;

    public CatalogoService(String nombreTienda) {
        this.catalogoRaiz = new Categoria(nombreTienda);
    }

    // --- API publica para EMarketFacade ---

    public ComponenteCatalogo listarCatalogo() {
        return catalogoRaiz;
    }

    public Producto buscarProductoPorId(int id) {
        return catalogoRaiz.buscarPorId(id);
    }

    public boolean verificarDisponibilidad(int idProducto, int cantidad) {
        Producto p = buscarProductoPorId(idProducto);
        return p != null && p.verificarStock(cantidad);
    }

    // --- Gestion del arbol (uso administrativo) ---

    /**
     * Agrega un producto a una categoria existente del arbol.
     * Si la categoria es null, lo agrega directamente a la raiz.
     */
    public void agregarProducto(Categoria categoria, Producto producto) {
        if (categoria != null) {
            categoria.agregarComponente(producto);
        } else {
            catalogoRaiz.agregarComponente(producto);
        }
    }

    /**
     * Crea una nueva categoria y la agrega bajo 'padre'.
     * Si padre es null, la agrega como categoria de primer nivel.
     */
    public Categoria crearCategoria(String nombre, Categoria padre) {
        Categoria nueva = new Categoria(nombre);
        if (padre != null) {
            padre.agregarComponente(nueva);
        } else {
            catalogoRaiz.agregarComponente(nueva);
        }
        return nueva;
    }

    /** Imprime el catalogo completo en consola (util para demo y debug). */
    public void mostrarCatalogo() {
        catalogoRaiz.mostrar("");
    }
}
