package catalogo;

/**
 * PATRON COMPOSITE - Componente.
 *
 * Interfaz comun para hojas (Producto) y compuestos (Categoria).
 * Permite que CatalogoService y EMarketFacade traten de forma
 * uniforme un producto individual y una categoria con subcategorias,
 * sin necesidad de instanceof ni condicionales.
 */
public interface ComponenteCatalogo {
    String getNombre();
    double getPrecio();
    int getStock();
    void mostrar(String indent);
}
