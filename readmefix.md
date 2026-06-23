# Fix: Stock total y precio total de categoría (Patrón Composite)

## ¿Qué se modificó?

Se expusieron dos nuevas operaciones sobre el árbol de catálogo:

- **Stock total de una categoría**: suma recursiva del stock de todos los productos que pertenecen a la categoría y sus subcategorías.
- **Precio total de una categoría**: suma recursiva del precio de todos los productos del subárbol.

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `CatalogoService.java` | Agregados `getStockTotalCategoria()` y `getPrecioTotalCategoria()` |
| `LibreriaFacade.java` | Delegación pública hacia los métodos del servicio |

---

## Explicación del patrón Composite

El **Composite** es un patrón estructural que permite tratar de forma uniforme a objetos individuales y a composiciones de objetos.

En este proyecto existe una jerarquía de catálogo:

```
ComponenteCatalogo  (interfaz)
├── Producto        (hoja / Leaf)
└── Categoria       (nodo / Composite)
    ├── Producto
    └── Categoria
        └── Producto
```

`ComponenteCatalogo` declara dos operaciones:

```java
double getPrecio();
int    getStock();
```

### Comportamiento según el tipo de nodo

**`Producto` (hoja):** devuelve su valor unitario.

```java
public double getPrecio() { return precio; }
public int    getStock()  { return stock; }
```

**`Categoria` (nodo compuesto):** suma recursivamente los valores de todos sus hijos, incluyendo subcategorías anidadas.

```java
public double getPrecio() {
    return hijos.stream().mapToDouble(ComponenteCatalogo::getPrecio).sum();
}

public int getStock() {
    return hijos.stream().mapToInt(ComponenteCatalogo::getStock).sum();
}
```

### Por qué esto es Composite

El cliente (CatalogoService, Facade) llama `cat.getPrecio()` sin saber si hay subcategorías adentro. La recursión ocurre dentro del nodo, no en el cliente. Agregar más niveles de anidamiento no requiere cambios en el código que consulta.

---

## Métodos nuevos en CatalogoService

```java
public double getPrecioTotalCategoria(String nombreCategoria) {
    Categoria cat = buscarCategoriaPorNombre(repositorio.obtenerRaiz(), nombreCategoria);
    if (cat == null) throw new IllegalArgumentException("Categoría no encontrada: " + nombreCategoria);
    return cat.getPrecio();
}

public int getStockTotalCategoria(String nombreCategoria) {
    Categoria cat = buscarCategoriaPorNombre(repositorio.obtenerRaiz(), nombreCategoria);
    if (cat == null) throw new IllegalArgumentException("Categoría no encontrada: " + nombreCategoria);
    return cat.getStock();
}
```

## Métodos nuevos en LibreriaFacade

```java
public double getPrecioTotalCategoria(String nombreCategoria) {
    verificarAutenticacion();
    return catService.getPrecioTotalCategoria(nombreCategoria);
}

public int getStockTotalCategoria(String nombreCategoria) {
    verificarAutenticacion();
    return catService.getStockTotalCategoria(nombreCategoria);
}
```

---

## Ejemplo de uso

```java
LibreriaFacade facade = new LibreriaFacade();
facade.iniciarSesion("admin", "admin123");

double precioFiccion = facade.getPrecioTotalCategoria("Ficción");
// Suma: Cien años de soledad (2500) + El principito (1800) + libros de Fantasía (3800) = 8100.0

int stockFiccion = facade.getStockTotalCategoria("Ficción");
// Suma: 8 + 12 + 5 = 25
```

---

## Justificación del diseño

El patrón Composite aplica aquí porque:

1. **Problema**: necesitamos calcular totales sobre un árbol donde los nodos pueden ser hojas (Producto) o composiciones (Categoria con hijos).
2. **Sin el patrón**: el cliente tendría que recorrer el árbol con `instanceof` para distinguir nodos de hojas.
3. **Con el patrón**: el cliente llama la misma operación en cualquier nodo y la suma se propaga sola.
4. **Extensibilidad**: si se agregan nuevos niveles de subcategorías, el cálculo sigue funcionando sin modificar nada.
