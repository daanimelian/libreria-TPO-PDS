package emarket.carrito;

import emarket.auth.Cliente;
import emarket.catalogo.CatalogoService;
import emarket.catalogo.Producto;

/**
 * Servicio de operaciones sobre el carrito de compras.
 *
 * <p>Coordina la validación de stock (usando {@link CatalogoService}) antes de
 * delegar las modificaciones al {@link Carrito} del cliente. Implementa el
 * principio de Información Experta (GRASP): el carrito conoce su estructura,
 * pero la validación de stock requiere datos del catálogo.
 */
public class CarritoService {

    private final CatalogoService catService;

    /**
     * @param catService servicio de catálogo usado para verificar disponibilidad de stock
     */
    public CarritoService(CatalogoService catService) {
        this.catService = catService;
    }

    /**
     * Agrega un producto al carrito del cliente después de validar el stock disponible.
     *
     * @param cliente    cliente dueño del carrito
     * @param idProducto identificador del producto a agregar
     * @param cantidad   cantidad deseada (debe ser mayor a 0)
     * @throws IllegalStateException si el stock es insuficiente o el producto no existe
     */
    public void agregarProducto(Cliente cliente, int idProducto, int cantidad) {
        if (!catService.verificarDisponibilidad(idProducto, cantidad)) {
            Producto p = catService.buscarProductoPorId(idProducto);
            String nombre = p != null ? p.getNombre() : "id=" + idProducto;
            throw new IllegalStateException("Stock insuficiente para: " + nombre);
        }
        Producto p = catService.buscarProductoPorId(idProducto);
        cliente.getCarrito().agregarProducto(p, cantidad);
    }

    /**
     * Elimina un producto del carrito del cliente.
     *
     * @param cliente  cliente dueño del carrito
     * @param producto producto a eliminar
     * @throws IllegalStateException si el producto no está en el carrito
     */
    public void eliminarProducto(Cliente cliente, Producto producto) {
        cliente.getCarrito().eliminarProducto(producto);
    }

    /**
     * Modifica la cantidad de un producto en el carrito, verificando stock previamente.
     *
     * @param cliente  cliente dueño del carrito
     * @param producto producto cuya cantidad se desea modificar
     * @param cantidad nueva cantidad deseada (debe ser mayor a 0)
     * @throws IllegalArgumentException si el stock es insuficiente
     * @throws IllegalStateException    si el producto no está en el carrito
     */
    public void modificarCantidad(Cliente cliente, Producto producto, int cantidad) {
        if (!catService.verificarDisponibilidad(producto.getId(), cantidad)) {
            throw new IllegalArgumentException("Stock insuficiente para: " + producto.getNombre());
        }
        cliente.getCarrito().modificarCantidad(producto, cantidad);
    }

    /**
     * Vacía por completo el carrito del cliente.
     *
     * @param cliente cliente cuyo carrito se desea vaciar
     */
    public void vaciarCarrito(Cliente cliente) {
        cliente.getCarrito().vaciarCarrito();
    }
}
