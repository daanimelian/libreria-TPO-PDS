package catalogo;

/**
 * PATRON COMPOSITE - Hoja.
 *
 * Representa un producto individual del catalogo. No tiene hijos.
 * Implementa ComponenteCatalogo para ser tratado de forma uniforme
 * junto con las categorias por parte del cliente (CatalogoService).
 */
public class Producto implements ComponenteCatalogo {

    private final int id;
    private String nombre;
    private double precio;
    private int stock;
    private String descripcion;

    public Producto(int id, String nombre, double precio, int stock, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.descripcion = descripcion;
    }

    // --- ComponenteCatalogo ---

    @Override
    public String getNombre() { return nombre; }

    @Override
    public double getPrecio() { return precio; }

    @Override
    public int getStock() { return stock; }

    @Override
    public void mostrar(String indent) {
        System.out.println(indent + "[Producto] #" + id
                + " | " + nombre
                + " | $" + precio
                + " | Stock: " + stock);
    }

    // --- Logica de negocio propia de la hoja ---

    public boolean verificarStock(int cantidad) {
        return stock >= cantidad;
    }

    public void reducirStock(int cantidad) {
        if (!verificarStock(cantidad))
            throw new IllegalStateException("Stock insuficiente para: " + nombre);
        stock -= cantidad;
    }

    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }
}
