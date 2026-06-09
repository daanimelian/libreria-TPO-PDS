package catalogo;

import java.util.ArrayList;
import java.util.List;

/**
 * PATRON COMPOSITE - Compuesto.
 *
 * Puede contener Productos (hojas) y otras Categorias (compuestos),
 * formando un arbol de profundidad indefinida.
 *
 * getPrecio() y getStock() delegan recursivamente en los hijos, permitiendo
 * que el cliente calcule precio minimo o stock total de toda una rama del
 * catalogo sin conocer su estructura interna.
 */
public class Categoria implements ComponenteCatalogo {

    private final String nombre;
    private final List<ComponenteCatalogo> hijos = new ArrayList<>();

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    // --- Gestion del arbol ---

    public void agregarComponente(ComponenteCatalogo componente) {
        hijos.add(componente);
    }

    public void eliminarComponente(ComponenteCatalogo componente) {
        hijos.remove(componente);
    }

    public List<ComponenteCatalogo> getHijos() {
        return hijos;
    }

    // --- ComponenteCatalogo ---

    @Override
    public String getNombre() { return nombre; }

    /**
     * Devuelve el precio minimo entre todos los productos de la categoria
     * (util para mostrar "desde $X" en el catalogo).
     * Retorna 0.0 si la categoria esta vacia.
     */
    @Override
    public double getPrecio() {
        return hijos.stream()
                .mapToDouble(ComponenteCatalogo::getPrecio)
                .min()
                .orElse(0.0);
    }

    /** Suma el stock de todos los productos bajo esta categoria. */
    @Override
    public int getStock() {
        return hijos.stream()
                .mapToInt(ComponenteCatalogo::getStock)
                .sum();
    }

    /** Imprime el arbol con indentacion recursiva. */
    @Override
    public void mostrar(String indent) {
        System.out.println(indent + "[Categoria] " + nombre);
        for (ComponenteCatalogo hijo : hijos) {
            hijo.mostrar(indent + "  ");
        }
    }

    /**
     * Busqueda recursiva por id. Retorna null si no lo encuentra.
     * Solo Producto tiene id; Categoria lo delega a sus hijos.
     */
    public Producto buscarPorId(int id) {
        for (ComponenteCatalogo hijo : hijos) {
            if (hijo instanceof Producto p && p.getId() == id) return p;
            if (hijo instanceof Categoria c) {
                Producto encontrado = c.buscarPorId(id);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}
