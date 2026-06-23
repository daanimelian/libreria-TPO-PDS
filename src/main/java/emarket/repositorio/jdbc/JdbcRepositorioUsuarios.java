package emarket.repositorio.jdbc;

import emarket.auth.Administrador;
import emarket.auth.Cliente;
import emarket.auth.Usuario;
import emarket.notificacion.CanalNotificacion;
import emarket.repositorio.IRepositorioUsuarios;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcRepositorioUsuarios extends JdbcRepositorioBase
        implements IRepositorioUsuarios {

    @Override
    public void guardar(Usuario usuario) {
        if (usuario instanceof Cliente c) {
            guardarCliente(c);
        } else if (usuario instanceof Administrador a) {
            guardarAdministrador(a);
        }
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return consultar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT u.username, u.password_hash, u.tipo, " +
                    "       c.direccion, c.email, c.telefono, c.token_dispositivo " +
                    "FROM usuarios u " +
                    "LEFT JOIN clientes c ON c.username = u.username " +
                    "WHERE u.username = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return Optional.empty();
                    return Optional.of(reconstruirUsuario(rs, conn));
                }
            }
        });
    }

    @Override
    public boolean existeUsername(String username) {
        return consultar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT EXISTS (SELECT 1 FROM usuarios WHERE username = ?)")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean(1);
                }
            }
        });
    }

    @Override
    public List<Usuario> listarTodos() {
        return consultar(conn -> {
            List<Usuario> resultado = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT u.username, u.password_hash, u.tipo, " +
                    "       c.direccion, c.email, c.telefono, c.token_dispositivo " +
                    "FROM usuarios u " +
                    "LEFT JOIN clientes c ON c.username = u.username")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        resultado.add(reconstruirUsuario(rs, conn));
                    }
                }
            }
            return resultado;
        });
    }

    // ── Helpers privados ────────────────────────────────────────────────────

    private void guardarCliente(Cliente c) {
        ejecutar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO usuarios (username, password_hash, tipo) VALUES (?, ?, 'CLIENTE')")) {
                ps.setString(1, c.getUsername());
                ps.setString(2, c.getPasswordHash());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO clientes (username, direccion, email, telefono, token_dispositivo) " +
                    "VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, c.getUsername());
                ps.setString(2, c.getDireccion());
                ps.setString(3, c.getEmail());
                ps.setString(4, c.getTelefono());
                ps.setString(5, c.getTokenDispositivo());
                ps.executeUpdate();
            }
            for (CanalNotificacion canal : c.getCanalesPreferidos()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO canales_notificacion (username, canal) VALUES (?, ?)")) {
                    ps.setString(1, c.getUsername());
                    ps.setString(2, canal.name());
                    ps.executeUpdate();
                }
            }
        });
    }

    private void guardarAdministrador(Administrador a) {
        ejecutar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO usuarios (username, password_hash, tipo) VALUES (?, ?, 'ADMINISTRADOR')")) {
                ps.setString(1, a.getUsername());
                ps.setString(2, a.getPasswordHash());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO administradores (username, legajo) VALUES (?, ?)")) {
                ps.setString(1, a.getUsername());
                ps.setString(2, a.getLegajo());
                ps.executeUpdate();
            }
        });
    }

    private Usuario reconstruirUsuario(ResultSet rs, java.sql.Connection conn) throws java.sql.SQLException {
        String username     = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String tipo         = rs.getString("tipo");

        if ("CLIENTE".equals(tipo)) {
            Cliente c = new Cliente(username, "placeholder", rs.getString("direccion"));
            c.setPasswordHash(passwordHash);
            c.setEmail(rs.getString("email"));
            c.setTelefono(rs.getString("telefono"));
            c.setTokenDispositivo(rs.getString("token_dispositivo"));
            c.modificarPreferenciasNotificacion(cargarCanales(username, conn));
            return c;
        } else {
            Administrador a = new Administrador(username, "placeholder");
            a.setPasswordHash(passwordHash);
            return a;
        }
    }

    private List<CanalNotificacion> cargarCanales(String username, java.sql.Connection conn)
            throws java.sql.SQLException {
        List<CanalNotificacion> canales = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT canal FROM canales_notificacion WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    canales.add(CanalNotificacion.valueOf(rs.getString("canal")));
                }
            }
        }
        return canales;
    }
}
