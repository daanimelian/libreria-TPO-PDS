package emarket.repositorio.jdbc;

import emarket.catalogo.Categoria;
import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcRepositorioCatalogoIT extends JdbcTestBase {

    private final JdbcRepositorioCatalogo repo = new JdbcRepositorioCatalogo();

    // ── guardarCategoria ──────────────────────────────────────────────────────

    @Test
    void guardarCategoria_raiz_asignaId() {
        Categoria raiz = new Categoria("Catálogo de Libros");
        repo.guardarCategoria(raiz, null);

        assertTrue(raiz.getId() > 0, "La raíz debe tener un ID generado por la DB");
    }

    @Test
    void guardarCategoria_hijo_asignaIdYEnlazaPadre() {
        Categoria raiz = new Categoria("Catálogo");
        repo.guardarCategoria(raiz, null);

        Categoria hijo = new Categoria("Ficción");
        repo.guardarCategoria(hijo, raiz);

        assertTrue(hijo.getId() > 0);
        assertTrue(raiz.getHijos().contains(hijo));
    }

    // ── guardarProducto ───────────────────────────────────────────────────────

    @Test
    void guardarProducto_asignaId() {
        Categoria raiz = new Categoria("Catálogo");
        repo.guardarCategoria(raiz, null);

        Producto p = new Producto(0, "Clean Code", 4500.0, 5);
        repo.guardarProducto(p, raiz);

        assertTrue(p.getId() > 0, "El producto debe tener ID asignado por la DB");
    }

    @Test
    void guardarProducto_apareceEnCategoria() {
        Categoria raiz = new Categoria("Catálogo");
        repo.guardarCategoria(raiz, null);

        Producto p = new Producto(0, "Design Patterns", 5000.0, 3);
        repo.guardarProducto(p, raiz);

        assertTrue(raiz.getHijos().contains(p));
    }

    // ── buscarProductoPorId ───────────────────────────────────────────────────

    @Test
    void buscarProductoPorId_encontrado() {
        Categoria raiz = new Categoria("Catálogo");
        repo.guardarCategoria(raiz, null);
        Producto p = new Producto(0, "1984", 2200.0, 6);
        repo.guardarProducto(p, raiz);

        Optional<Producto> resultado = repo.buscarProductoPorId(p.getId());
        assertTrue(resultado.isPresent());
        assertEquals("1984", resultado.get().getNombre());
        assertEquals(2200.0, resultado.get().getPrecio(), 0.01);
        assertEquals(6, resultado.get().getStock());
    }

    @Test
    void buscarProductoPorId_inexistente_retornaEmpty() {
        Optional<Producto> resultado = repo.buscarProductoPorId(999999);
        assertFalse(resultado.isPresent());
    }

    // ── actualizarStock ───────────────────────────────────────────────────────

    @Test
    void actualizarStock_cambia_stock() {
        Categoria raiz = new Categoria("Catálogo");
        repo.guardarCategoria(raiz, null);
        Producto p = new Producto(0, "Sapiens", 3200.0, 10);
        repo.guardarProducto(p, raiz);

        repo.actualizarStock(p.getId(), 7);

        Producto actualizado = repo.buscarProductoPorId(p.getId()).orElseThrow();
        assertEquals(7, actualizado.getStock());
    }

    @Test
    void actualizarStock_a_cero_permitido() {
        Categoria raiz = new Categoria("Catálogo");
        repo.guardarCategoria(raiz, null);
        Producto p = new Producto(0, "El Principito", 1800.0, 5);
        repo.guardarProducto(p, raiz);

        repo.actualizarStock(p.getId(), 0);

        assertEquals(0, repo.buscarProductoPorId(p.getId()).orElseThrow().getStock());
    }

    // ── obtenerRaiz ───────────────────────────────────────────────────────────

    @Test
    void obtenerRaiz_sinDatos_retornaNull() {
        assertNull(repo.obtenerRaiz());
    }

    @Test
    void obtenerRaiz_reconstruyeArbolCompleto() {
        Categoria raiz = new Categoria("Catálogo");
        repo.guardarCategoria(raiz, null);
        Categoria ficcion = new Categoria("Ficción");
        repo.guardarCategoria(ficcion, raiz);
        Producto p = new Producto(0, "Cien años de soledad", 2500.0, 8);
        repo.guardarProducto(p, ficcion);

        ComponenteCatalogo raizReconstruida = repo.obtenerRaiz();

        assertNotNull(raizReconstruida);
        assertEquals("Catálogo", raizReconstruida.getNombre());
        assertInstanceOf(Categoria.class, raizReconstruida);

        Categoria raizCat = (Categoria) raizReconstruida;
        assertEquals(1, raizCat.getHijos().size());

        Categoria ficcionReconstruida = (Categoria) raizCat.getHijos().get(0);
        assertEquals("Ficción", ficcionReconstruida.getNombre());
        assertEquals(1, ficcionReconstruida.getHijos().size());

        Producto prodReconstruido = (Producto) ficcionReconstruida.getHijos().get(0);
        assertEquals("Cien años de soledad", prodReconstruido.getNombre());
        assertEquals(2500.0, prodReconstruido.getPrecio(), 0.01);
        assertEquals(8, prodReconstruido.getStock());
    }

    @Test
    void obtenerRaiz_multiples_subcategorias() {
        Categoria raiz = new Categoria("Catálogo");
        repo.guardarCategoria(raiz, null);
        repo.guardarCategoria(new Categoria("Ficción"), raiz);
        repo.guardarCategoria(new Categoria("Historia"), raiz);
        repo.guardarCategoria(new Categoria("Técnicos"), raiz);

        Categoria raizReconstruida = (Categoria) repo.obtenerRaiz();
        assertEquals(3, raizReconstruida.getHijos().size());
    }
}
