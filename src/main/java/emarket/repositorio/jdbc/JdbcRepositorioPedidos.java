package emarket.repositorio.jdbc;

import emarket.auth.Cliente;
import emarket.estado.EstadoPedidoFactory;
import emarket.notificacion.CanalNotificacion;
import emarket.pago.TipoPago;
import emarket.pedido.ItemPedido;
import emarket.pedido.Pedido;
import emarket.repositorio.IRepositorioPedidos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcRepositorioPedidos extends JdbcRepositorioBase
        implements IRepositorioPedidos {

    @Override
    public void guardar(Pedido pedido) {
        double subtotal = pedido.getItems().stream()
                .mapToDouble(i -> i.getPrecioUnitario() * i.getCantidad())
                .sum();
        ejecutar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pedidos (id, username_cliente, estado, tipo_pago, subtotal, total) " +
                    "VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, pedido.getId());
                ps.setString(2, pedido.getCliente().getUsername());
                ps.setString(3, pedido.getEstado().getNombre());
                ps.setString(4, pedido.getTipoPago().name());
                ps.setDouble(5, subtotal);
                ps.setDouble(6, pedido.getTotal());
                ps.executeUpdate();
            }
            for (ItemPedido item : pedido.getItems()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO items_pedido (pedido_id, nombre_producto, precio_unitario, cantidad) " +
                        "VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, pedido.getId());
                    ps.setString(2, item.getNombreProducto());
                    ps.setDouble(3, item.getPrecioUnitario());
                    ps.setInt(4, item.getCantidad());
                    ps.executeUpdate();
                }
            }
        });
    }

    @Override
    public void actualizarEstado(int id, String estadoNombre) {
        ejecutar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE pedidos SET estado = ? WHERE id = ?")) {
                ps.setString(1, estadoNombre);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        });
    }

    @Override
    public Optional<Pedido> buscarPorId(int id) {
        return consultar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT p.id, p.estado, p.tipo_pago, p.total, " +
                    "       u.username, u.password_hash, " +
                    "       c.direccion, c.email, c.telefono, c.token_dispositivo " +
                    "FROM pedidos p " +
                    "JOIN usuarios u ON u.username = p.username_cliente " +
                    "JOIN clientes c ON c.username = p.username_cliente " +
                    "WHERE p.id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return Optional.empty();
                    return Optional.of(reconstruirPedido(rs, conn));
                }
            }
        });
    }

    @Override
    public List<Pedido> listarTodos() {
        return consultar(conn -> {
            List<Pedido> resultado = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT p.id, p.estado, p.tipo_pago, p.total, " +
                    "       u.username, u.password_hash, " +
                    "       c.direccion, c.email, c.telefono, c.token_dispositivo " +
                    "FROM pedidos p " +
                    "JOIN usuarios u ON u.username = p.username_cliente " +
                    "JOIN clientes c ON c.username = p.username_cliente " +
                    "ORDER BY p.id");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(reconstruirPedido(rs, conn));
                }
            }
            return resultado;
        });
    }

    @Override
    public List<Pedido> listarPorCliente(String usernameCliente) {
        return consultar(conn -> {
            List<Pedido> resultado = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT p.id, p.estado, p.tipo_pago, p.total, " +
                    "       u.username, u.password_hash, " +
                    "       c.direccion, c.email, c.telefono, c.token_dispositivo " +
                    "FROM pedidos p " +
                    "JOIN usuarios u ON u.username = p.username_cliente " +
                    "JOIN clientes c ON c.username = p.username_cliente " +
                    "WHERE p.username_cliente = ? ORDER BY p.id")) {
                ps.setString(1, usernameCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        resultado.add(reconstruirPedido(rs, conn));
                    }
                }
            }
            return resultado;
        });
    }

    @Override
    public int getProximoId() {
        return consultar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT nextval('pedidos_id_seq')");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        });
    }

    // ── Helpers de reconstitución ────────────────────────────────────────────

    private Pedido reconstruirPedido(ResultSet rs, Connection conn) throws java.sql.SQLException {
        int idPedido    = rs.getInt("id");
        TipoPago tipo   = TipoPago.valueOf(rs.getString("tipo_pago"));
        double total    = rs.getDouble("total");
        String estado   = rs.getString("estado");
        String username = rs.getString("username");

        Cliente cliente = reconstruirCliente(rs, username, conn);
        List<ItemPedido> items = cargarItems(idPedido, conn);

        Pedido pedido = new Pedido(idPedido, cliente, items, total, tipo);
        pedido.setEstado(EstadoPedidoFactory.crear(estado));
        return pedido;
    }

    private Cliente reconstruirCliente(ResultSet rs, String username, Connection conn)
            throws java.sql.SQLException {
        Cliente c = new Cliente(username, "placeholder", rs.getString("direccion"));
        c.setPasswordHash(rs.getString("password_hash"));
        c.setEmail(rs.getString("email"));
        c.setTelefono(rs.getString("telefono"));
        c.setTokenDispositivo(rs.getString("token_dispositivo"));
        c.modificarPreferenciasNotificacion(cargarCanales(username, conn));
        return c;
    }

    private List<CanalNotificacion> cargarCanales(String username, Connection conn)
            throws java.sql.SQLException {
        List<CanalNotificacion> canales = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT canal FROM canales_notificacion WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) canales.add(CanalNotificacion.valueOf(rs.getString("canal")));
            }
        }
        return canales;
    }

    private List<ItemPedido> cargarItems(int pedidoId, Connection conn)
            throws java.sql.SQLException {
        List<ItemPedido> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nombre_producto, precio_unitario, cantidad " +
                "FROM items_pedido WHERE pedido_id = ?")) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new ItemPedido(
                            rs.getString("nombre_producto"),
                            rs.getDouble("precio_unitario"),
                            rs.getInt("cantidad")
                    ));
                }
            }
        }
        return items;
    }
}
