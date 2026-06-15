package emarket.catalogo;

import java.util.ArrayList;
import java.util.List;

// Composite: nodo del árbol, puede contener otros componentes
public class Categoria implements ComponenteCatalogo {

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

    @Override
    public String getNombre() { return nombre; }

    // Precio total = suma de precios de todos los hijos
    @Override
    public double getPrecio() {
        return hijos.stream().mapToDouble(ComponenteCatalogo::getPrecio).sum();
    }

    // Stock total = suma de stocks de todos los hijos
    @Override
    public int getStock() {
        return hijos.stream().mapToInt(ComponenteCatalogo::getStock).sum();
    }
}
