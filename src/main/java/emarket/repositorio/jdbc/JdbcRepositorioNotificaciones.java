package emarket.repositorio.jdbc;

import emarket.notificacion.Notificacion;
import emarket.repositorio.IRepositorioNotificaciones;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JdbcRepositorioNotificaciones extends JdbcRepositorioBase
        implements IRepositorioNotificaciones {

    @Override
    public void guardar(String usernameCliente, String mensaje) {
        ejecutar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO notificaciones (username, mensaje) VALUES (?, ?)")) {
                ps.setString(1, usernameCliente);
                ps.setString(2, mensaje);
                ps.executeUpdate();
            }
        });
    }

    @Override
    public List<Notificacion> listarNoVistas(String usernameCliente) {
        return consultar(conn -> {
            List<Notificacion> resultado = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT mensaje, timestamp, visto FROM notificaciones " +
                    "WHERE username = ? AND visto = FALSE ORDER BY timestamp ASC")) {
                ps.setString(1, usernameCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        resultado.add(Notificacion.reconstruirDesdeDB(
                                rs.getString("mensaje"),
                                rs.getTimestamp("timestamp").toLocalDateTime(),
                                rs.getBoolean("visto")
                        ));
                    }
                }
            }
            return resultado;
        });
    }

    @Override
    public void marcarTodasComoVistas(String usernameCliente) {
        ejecutar(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE notificaciones SET visto = TRUE WHERE username = ? AND visto = FALSE")) {
                ps.setString(1, usernameCliente);
                ps.executeUpdate();
            }
        });
    }
}
