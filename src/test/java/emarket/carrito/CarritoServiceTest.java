package emarket.carrito;

import emarket.auth.Cliente;
import emarket.catalogo.CatalogoService;
import emarket.catalogo.Categoria;
import emarket.catalogo.Producto;
import emarket.repositorio.inmemory.InMemoryRepositorioCatalogo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarritoServiceTest {

    private CarritoService carritoService;
    private Cliente cliente;
    private Producto producto;

    @BeforeEach
    void setUp() {
        InMemoryRepositorioCatalogo repoCatalogo = new InMemoryRepositorioCatalogo();
        CatalogoService catService = new CatalogoService(repoCatalogo);
        carritoService = new CarritoService(catService);

        cliente = new Cliente("testuser", "pass1234", "Av. Test 123");

        Categoria raiz = catService.crearCategoria("Catálogo", null);
        producto = new Producto(1, "Libro Test", 1000.0, 10);
        catService.agregarProducto(raiz, producto);
    }

    @Test
    void agregarProducto_exitoso_agrega_item() {
        carritoService.agregarProducto(cliente, 1, 2);

        assertEquals(1, cliente.getCarrito().getItems().size());
        assertEquals(2, cliente.getCarrito().getItems().get(0).getCantidad());
    }

    @Test
    void agregarProducto_mismoProducto_acumulaCantidad() {
        carritoService.agregarProducto(cliente, 1, 2);
        carritoService.agregarProducto(cliente, 1, 3);

        assertEquals(1, cliente.getCarrito().getItems().size());
        assertEquals(5, cliente.getCarrito().getItems().get(0).getCantidad());
    }

    @Test
    void agregarProducto_stockInsuficiente_lanzaExcepcion() {
        assertThrows(IllegalStateException.class, () ->
                carritoService.agregarProducto(cliente, 1, 100));
        assertTrue(cliente.getCarrito().estaVacio());
    }

    @Test
    void eliminarProducto_exitoso() {
        carritoService.agregarProducto(cliente, 1, 2);
        carritoService.eliminarProducto(cliente, producto);

        assertTrue(cliente.getCarrito().estaVacio());
    }

    @Test
    void eliminarProducto_noEstaEnCarrito_lanzaExcepcion() {
        assertThrows(IllegalStateException.class, () ->
                carritoService.eliminarProducto(cliente, producto));
    }

    @Test
    void modificarCantidad_exitoso() {
        carritoService.agregarProducto(cliente, 1, 2);
        carritoService.modificarCantidad(cliente, producto, 7);

        assertEquals(7, cliente.getCarrito().getItems().get(0).getCantidad());
    }

    @Test
    void modificarCantidad_productoNoEnCarrito_lanzaExcepcion() {
        assertThrows(IllegalStateException.class, () ->
                carritoService.modificarCantidad(cliente, producto, 5));
    }

    @Test
    void vaciarCarrito_dejaNulo() {
        carritoService.agregarProducto(cliente, 1, 2);
        carritoService.vaciarCarrito(cliente);

        assertTrue(cliente.getCarrito().estaVacio());
        assertEquals(0, cliente.getCarrito().getItems().size());
    }

    @Test
    void calcularTotal_correcto() {
        carritoService.agregarProducto(cliente, 1, 3);

        assertEquals(3000.0, cliente.getCarrito().calcularTotal(), 0.01);
    }

    @Test
    void carritoNuevo_estaVacio() {
        assertTrue(cliente.getCarrito().estaVacio());
    }
}
