package emarket.catalogo;

import emarket.repositorio.IRepositorioCatalogo;

/**
 * Servicio de consulta y mantenimiento del catálogo de productos.
 *
 * <p>Delega todas las operaciones de persistencia en {@link IRepositorioCatalogo},
 * respetando el principio DIP. Provee operaciones para buscar productos, verificar
 * disponibilidad de stock y construir la jerarquía de categorías.
 */
public class CatalogoService {

    private final IRepositorioCatalogo repositorio;

    /**
     * @param repositorio implementación de persistencia del catálogo a utilizar
     */
    public CatalogoService(IRepositorioCatalogo repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Busca un producto por su identificador único.
     *
     * @param id identificador del producto
     * @return el producto encontrado, o {@code null} si no existe
     */
    public Producto buscarProductoPorId(int id) {
        return repositorio.buscarProductoPorId(id).orElse(null);
    }

    /**
     * Devuelve la raíz del árbol Composite del catálogo.
     *
     * @return componente raíz (generalmente una {@link Categoria} con toda la jerarquía)
     */
    public ComponenteCatalogo listarCatalogo() {
        return repositorio.obtenerRaiz();
    }

    /**
     * Verifica si hay stock suficiente para la cantidad solicitada de un producto.
     *
     * @param id      identificador del producto
     * @param cantidad cantidad requerida
     * @return {@code true} si existe el producto y su stock es mayor o igual a la cantidad
     */
    public boolean verificarDisponibilidad(int id, int cantidad) {
        return repositorio.buscarProductoPorId(id)
                .map(p -> p.verificarStock(cantidad))
                .orElse(false);
    }

    /**
     * Agrega un producto a una categoría y lo persiste en el repositorio.
     *
     * @param categoria categoría padre donde se incluirá el producto
     * @param producto  producto a agregar
     */
    public void agregarProducto(Categoria categoria, Producto producto) {
        repositorio.guardarProducto(producto, categoria);
    }

    /**
     * Crea una nueva categoría y la asocia a su categoría padre (o la define como raíz
     * si {@code padre} es {@code null}).
     *
     * @param nombre nombre de la nueva categoría
     * @param padre  categoría padre, o {@code null} si es la categoría raíz
     * @return la categoría recién creada
     */
    public Categoria crearCategoria(String nombre, Categoria padre) {
        Categoria nueva = new Categoria(nombre);
        repositorio.guardarCategoria(nueva, padre);
        return nueva;
    }
}
