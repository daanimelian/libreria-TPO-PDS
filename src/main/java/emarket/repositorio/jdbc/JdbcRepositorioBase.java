package emarket.repositorio.jdbc;

import emarket.config.DataSourceProvider;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Template Method: define el esqueleto de cada operación JDBC.
 *
 * Las subclases nunca abren ni cierran conexiones directamente; solo proveen
 * el bloque de SQL concreto a través de las interfaces funcionales.
 *
 * Invariantes garantizados por la clase base:
 *   - Toda consulta obtiene y libera la conexión automáticamente.
 *   - Todo comando (escritura) se ejecuta dentro de una transacción:
 *     commit si termina sin error, rollback si lanza excepción.
 */
public abstract class JdbcRepositorioBase {

    // ── Interfaces funcionales (los "pasos variables" del Template Method) ──

    @FunctionalInterface
    protected interface OperacionJdbc<T> {
        T ejecutar(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    protected interface ComandoJdbc {
        void ejecutar(Connection conn) throws SQLException;
    }

    // ── Template Method: consulta (sin transacción) ──────────────────────────

    protected final <T> T consultar(OperacionJdbc<T> operacion) {
        try (Connection conn = obtenerConexion()) {
            return operacion.ejecutar(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error en consulta SQL", e);
        }
    }

    // ── Template Method: comando (con transacción explícita) ─────────────────

    protected final void ejecutar(ComandoJdbc comando) {
        Connection conn = null;
        try {
            conn = obtenerConexion();
            conn.setAutoCommit(false);
            comando.ejecutar(conn);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn, e);
            throw new RuntimeException("Error en comando SQL — rollback ejecutado", e);
        } finally {
            cerrar(conn);
        }
    }

    // ── Pasos invariantes (hooks privados) ───────────────────────────────────

    private Connection obtenerConexion() throws SQLException {
        return DataSourceProvider.getInstance().getDataSource().getConnection();
    }

    private void rollback(Connection conn, SQLException original) {
        if (conn == null) return;
        try {
            conn.rollback();
        } catch (SQLException e) {
            original.addSuppressed(e);
        }
    }

    private void cerrar(Connection conn) {
        if (conn == null) return;
        try {
            conn.close();
        } catch (SQLException ignored) {}
    }
}
