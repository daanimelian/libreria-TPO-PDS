package emarket.catalogo;

import java.util.ArrayList;
import java.util.List;

// Composite: nodo del árbol, puede contener otros componentes
public class Categoria implements ComponenteCatalogo {

    private int id;
    private String nombre;
    private List<ComponenteCatalogo> hijos = new ArrayList<>();

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public void agregarComponente(ComponenteCatalogo c) {
        hijos.add(c);
    }

    public void eliminarComponente(ComponenteCatalogo c) {
        hijos.remove(c);
    }

    public List<ComponenteCatalogo> getHijos() {
        return hijos;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public String getNombre() { return nombre; }

    /**
     * Devuelve el precio <b>agregado</b> (suma recursiva) de todos los componentes hijo.
     * Semántica Composite: no es un precio unitario sino el total acumulado del subárbol,
     * incluyendo subcategorías anidadas. Contrasta con {@link Producto#getPrecio()},
     * que devuelve el precio unitario del ítem.
     */
    @Override
    public double getPrecio() {
        return hijos.stream().mapToDouble(ComponenteCatalogo::getPrecio).sum();
    }

    /**
     * Devuelve el stock <b>agregado</b> (suma recursiva) de todos los componentes hijo.
     * Semántica Composite: no es el stock de un ítem individual sino el total acumulado
     * del subárbol, incluyendo subcategorías anidadas. Contrasta con
     * {@link Producto#getStock()}, que devuelve el stock individual del producto.
     */
    @Override
    public int getStock() {
        return hijos.stream().mapToInt(ComponenteCatalogo::getStock).sum();
    }
}
