package emarket.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Singleton: una única instancia del pool de conexiones para toda la aplicación
public class DataSourceProvider {

    private static DataSourceProvider instancia;
    private final HikariDataSource dataSource;

    private DataSourceProvider() {
        Properties props = cargarPropiedades();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minimumIdle", "2")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "600000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("db.pool.maxLifetime", "1800000")));
        config.setPoolName("LibreriaPool");
        this.dataSource = new HikariDataSource(config);
    }

    public static synchronized DataSourceProvider getInstance() {
        if (instancia == null) {
            instancia = new DataSourceProvider();
        }
        return instancia;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void cerrar() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private Properties cargarPropiedades() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró db.properties en el classpath. " +
                        "Copiá db.properties.example a db.properties y configurá las credenciales.");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer db.properties", e);
        }
        return props;
    }
}
