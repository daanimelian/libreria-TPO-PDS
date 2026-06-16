package emarket.repositorio.jdbc;

import emarket.catalogo.Categoria;
import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import emarket.repositorio.IRepositorioCatalogo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcRepositorioCatalogo extends JdbcRepositorioBase
        implements IRepositorioCatalogo {

    @Override
    public void guardarCategoria(Categoria categoria, Categoria padre) {
        ejecutar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO categorias (nombre, padre_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, categoria.getNombre());
                if (padre == null) {
                    ps.setNull(2, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(2, padre.getId());
                }
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) categoria.setId(keys.getInt(1));
                }
            }
            // Agregar al árbol Composite en memoria (misma semántica que InMemory)
            if (padre != null) padre.agregarComponente(categoria);
        });
    }

    @Override
    public void guardarProducto(Producto producto, Categoria categoria) {
        ejecutar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO productos (nombre, precio, stock, categoria_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, producto.getNombre());
                ps.setDouble(2, producto.getPrecio());
                ps.setInt(3, producto.getStock());
                ps.setInt(4, categoria.getId());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) producto.setId(keys.getInt(1));
                }
            }
            categoria.agregarComponente(producto);
        });
    }

    @Override
    public Optional<Producto> buscarProductoPorId(int id) {
        return consultar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, nombre, precio, stock FROM productos WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return Optional.empty();
                    return Optional.of(new Producto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getDouble("precio"),
                            rs.getInt("stock")
                    ));
                }
            }
        });
    }

    @Override
    public void actualizarStock(int idProducto, int nuevoStock) {
        ejecutar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE productos SET stock = ? WHERE id = ?")) {
                ps.setInt(1, nuevoStock);
                ps.setInt(2, idProducto);
                ps.executeUpdate();
            }
        });
    }

    /**
     * Reconstruye el árbol Composite completo desde la DB.
     * Algoritmo:
     *   1. Cargar todas las categorías en un Map<id, Categoria>
     *   2. Enlazar padre↔hijo para reconstruir el árbol
     *   3. Cargar todos los productos y agregarlos a su categoría
     *   4. Devolver el nodo raíz (padre_id IS NULL)
     */
    @Override
    public ComponenteCatalogo obtenerRaiz() {
        return consultar(conn -> {
            Map<Integer, Categoria> categorias = new HashMap<>();
            Map<Integer, Integer> padres = new HashMap<>();

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, nombre, padre_id FROM categorias");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria cat = new Categoria(rs.getString("nombre"));
                    cat.setId(rs.getInt("id"));
                    categorias.put(cat.getId(), cat);
                    int padreId = rs.getInt("padre_id");
                    if (!rs.wasNull()) padres.put(cat.getId(), padreId);
                }
            }

            // Enlazar padres e hijos
            Categoria raiz = null;
            for (Categoria cat : categorias.values()) {
                if (padres.containsKey(cat.getId())) {
                    Categoria padre = categorias.get(padres.get(cat.getId()));
                    padre.agregarComponente(cat);
                } else {
                    raiz = cat;
                }
            }

            // Agregar productos a sus categorías
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, nombre, precio, stock, categoria_id FROM productos");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getDouble("precio"),
                            rs.getInt("stock")
                    );
                    Categoria cat = categorias.get(rs.getInt("categoria_id"));
                    if (cat != null) cat.agregarComponente(p);
                }
            }

            return raiz;
        });
    }
}
