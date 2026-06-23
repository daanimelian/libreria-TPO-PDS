package emarket.catalogo;

/**
 * Componente base del patrón Composite para el árbol de catálogo.
 *
 * <p>Semántica de {@link #getPrecio()} y {@link #getStock()} según el tipo de nodo:
 * <ul>
 *   <li>{@link Producto} (Leaf): devuelve el valor <b>unitario</b> del producto.</li>
 *   <li>{@link Categoria} (Composite): devuelve el valor <b>agregado</b> (suma recursiva
 *       de todos los hijos). No representa un precio o stock individual, sino el total
 *       acumulado del subárbol, incluyendo subcategorías anidadas.</li>
 * </ul>
 * Los clientes que iteran listas mixtas de Categoria y Producto deben tener en cuenta
 * esta diferencia de semántica para evitar interpretaciones incorrectas (tensión LSP).
 */
public interface ComponenteCatalogo {
    String getNombre();
    double getPrecio();
    int getStock();
}
