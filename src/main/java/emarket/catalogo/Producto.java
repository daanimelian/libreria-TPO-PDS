package emarket.catalogo;

/**
 * Nodo hoja del árbol Composite del catálogo (patrón Composite — Leaf).
 *
 * <p>{@link #getPrecio()} y {@link #getStock()} devuelven los valores <b>unitarios</b>
 * del producto, a diferencia de {@link Categoria} que devuelve la suma agregada de su subárbol.
 */
public class Producto implements ComponenteCatalogo {

    private int id;
    private String nombre;
    private double precio;
    private int stock;

    /**
     * @param id     identificador único del producto (0 indica que será asignado por la BD)
     * @param nombre nombre descriptivo del libro o producto
     * @param precio precio unitario en la moneda configurada (ARS por defecto)
     * @param stock  cantidad de unidades disponibles en inventario
     */
    public Producto(int id, String nombre, double precio, int stock) {
        this.id     = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock  = stock;
    }

    /** @return identificador único del producto */
    public int getId() { return id; }

    /** Actualiza el id; uso exclusivo de la capa de persistencia al insertar en BD. */
    public void setId(int id) { this.id = id; }

    /** @return nombre del producto */
    @Override
    public String getNombre() { return nombre; }

    /** @return precio unitario del producto en ARS */
    @Override
    public double getPrecio() { return precio; }

    /** @return cantidad de unidades disponibles en stock */
    @Override
    public int getStock() { return stock; }

    /**
     * Verifica si hay suficiente stock para la cantidad solicitada.
     *
     * @param cantidad cantidad requerida
     * @return {@code true} si {@code stock >= cantidad}
     */
    public boolean verificarStock(int cantidad) {
        return stock >= cantidad;
    }

    /**
     * Reduce el stock en la cantidad indicada.
     *
     * @param cantidad unidades a descontar
     * @throws IllegalStateException si el stock resultante sería negativo
     */
    public void reducirStock(int cantidad) {
        if (stock < cantidad) {
            throw new IllegalStateException("Stock insuficiente para: " + nombre);
        }
        stock -= cantidad;
    }

    @Override
    public String toString() {
        return String.format("Producto[id=%d, nombre=%s, precio=%.2f, stock=%d]",
                id, nombre, precio, stock);
    }
}
