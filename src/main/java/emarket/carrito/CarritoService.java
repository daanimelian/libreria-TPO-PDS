package emarket.carrito;

import emarket.auth.Cliente;
import emarket.catalogo.CatalogoService;
import emarket.catalogo.Producto;

public class CarritoService {

    private CatalogoService catService;

    public CarritoService(CatalogoService catService) {
        this.catService = catService;
    }

    public void agregarProducto(Cliente cliente, int idProducto, int cantidad) {
        // Validar stock antes de agregar
        if (!catService.verificarDisponibilidad(idProducto, cantidad)) {
            Producto p = catService.buscarProductoPorId(idProducto);
            String nombre = p != null ? p.getNombre() : "id=" + idProducto;
            throw new IllegalStateException("Stock insuficiente para: " + nombre);
        }
        Producto p = catService.buscarProductoPorId(idProducto);
        cliente.getCarrito().agregarProducto(p, cantidad);
    }

    public void eliminarProducto(Cliente cliente, Producto producto) {
        cliente.getCarrito().eliminarProducto(producto);
    }

    public void modificarCantidad(Cliente cliente, Producto producto, int cantidad) {
        cliente.getCarrito().modificarCantidad(producto, cantidad);
    }

    public Carrito getCarrito(Cliente cliente) {
        return cliente.getCarrito();
    }

    public void vaciarCarrito(Cliente cliente) {
        cliente.getCarrito().vaciarCarrito();
    }
}
