package emarket.catalogo;

// Composite: hoja del árbol (no tiene hijos)
public class Producto implements ComponenteCatalogo {

    private int id;
    private String nombre;
    private double precio;
    private int stock;

    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public int getId() { return id; }

    @Override
    public String getNombre() { return nombre; }

    @Override
    public double getPrecio() { return precio; }

    @Override
    public int getStock() { return stock; }

    public boolean verificarStock(int cantidad) {
        return stock >= cantidad;
    }

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
