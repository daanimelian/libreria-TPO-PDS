package emarket.catalogo;

import emarket.repositorio.inmemory.InMemoryRepositorioCatalogo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoServiceTest {

    private CatalogoService service;

    @BeforeEach
    void setUp() {
        service = new CatalogoService(new InMemoryRepositorioCatalogo());
    }

    @Test
    void buscarProductoPorId_encontrado() {
        Categoria raiz = service.crearCategoria("Catálogo", null);
        Producto p = new Producto(1, "Libro Test", 1000.0, 5);
        service.agregarProducto(raiz, p);

        Producto encontrado = service.buscarProductoPorId(1);
        assertNotNull(encontrado);
        assertEquals("Libro Test", encontrado.getNombre());
        assertEquals(1000.0, encontrado.getPrecio(), 0.01);
    }

    @Test
    void buscarProductoPorId_noEncontrado_retornaNull() {
        assertNull(service.buscarProductoPorId(9999));
    }

    @Test
    void verificarDisponibilidad_stockSuficiente_retornaTrue() {
        Categoria raiz = service.crearCategoria("Catálogo", null);
        service.agregarProducto(raiz, new Producto(1, "Libro", 1000.0, 10));

        assertTrue(service.verificarDisponibilidad(1, 5));
        assertTrue(service.verificarDisponibilidad(1, 10));
    }

    @Test
    void verificarDisponibilidad_stockInsuficiente_retornaFalse() {
        Categoria raiz = service.crearCategoria("Catálogo", null);
        service.agregarProducto(raiz, new Producto(1, "Libro", 1000.0, 2));

        assertFalse(service.verificarDisponibilidad(1, 3));
        assertFalse(service.verificarDisponibilidad(1, 100));
    }

    @Test
    void verificarDisponibilidad_productoInexistente_retornaFalse() {
        assertFalse(service.verificarDisponibilidad(9999, 1));
    }

    @Test
    void listarCatalogo_retornaRaiz() {
        Categoria raiz = service.crearCategoria("Raíz del catálogo", null);
        ComponenteCatalogo catalogo = service.listarCatalogo();

        assertNotNull(catalogo);
        assertEquals("Raíz del catálogo", catalogo.getNombre());
    }

    @Test
    void listarCatalogo_sinDatos_retornaNull() {
        assertNull(service.listarCatalogo());
    }

    @Test
    void crearCategoria_raizEHijo_jerarquiaCorrecta() {
        Categoria raiz = service.crearCategoria("Catálogo", null);
        Categoria sub = service.crearCategoria("Técnicos", raiz);

        assertNotNull(sub);
        assertEquals("Técnicos", sub.getNombre());
        assertTrue(raiz.getHijos().contains(sub));
    }

    @Test
    void agregarProducto_incluyeEnCategoria() {
        Categoria raiz = service.crearCategoria("Catálogo", null);
        Producto p = new Producto(1, "Clean Code", 4500.0, 5);
        service.agregarProducto(raiz, p);

        assertTrue(raiz.getHijos().contains(p));
        assertNotNull(service.buscarProductoPorId(1));
    }

    @Test
    void calcularStockTotal_sumas_hijos() {
        Categoria raiz = service.crearCategoria("Catálogo", null);
        service.agregarProducto(raiz, new Producto(1, "Libro A", 1000.0, 5));
        service.agregarProducto(raiz, new Producto(2, "Libro B", 2000.0, 3));

        assertEquals(8, raiz.getStock());
    }

    @Test
    void buscarProducto_enSubcategoria() {
        Categoria raiz = service.crearCategoria("Catálogo", null);
        Categoria sub = service.crearCategoria("Técnicos", raiz);
        service.agregarProducto(sub, new Producto(42, "Design Patterns", 5000.0, 3));

        Producto encontrado = service.buscarProductoPorId(42);
        assertNotNull(encontrado);
        assertEquals("Design Patterns", encontrado.getNombre());
    }
}
