package emarket.repositorio.jdbc;

import emarket.config.DataSourceProvider;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

abstract class JdbcTestBase {

    @BeforeEach
    void limpiarDB() throws SQLException {
        try (Connection conn = DataSourceProvider.getInstance().getDataSource().getConnection();
             Statement st = conn.createStatement()) {
            // Truncate in dependency order; CASCADE handles FK chains automatically
            st.execute("""
                    TRUNCATE TABLE
                        items_pedido,
                        notificaciones,
                        canales_notificacion,
                        pedidos,
                        productos,
                        clientes,
                        administradores,
                        usuarios,
                        categorias
                    RESTART IDENTITY CASCADE
                    """);
        }
    }
}
