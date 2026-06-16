package emarket.repositorio.factory;

import emarket.repositorio.IRepositorioCatalogo;
import emarket.repositorio.IRepositorioNotificaciones;
import emarket.repositorio.IRepositorioPedidos;
import emarket.repositorio.IRepositorioUsuarios;
import emarket.repositorio.jdbc.JdbcRepositorioCatalogo;
import emarket.repositorio.jdbc.JdbcRepositorioNotificaciones;
import emarket.repositorio.jdbc.JdbcRepositorioPedidos;
import emarket.repositorio.jdbc.JdbcRepositorioUsuarios;

// Concrete Factory: crea la familia de repositorios JDBC (PostgreSQL)
public class JdbcRepositorioFactory implements RepositorioFactory {

    @Override
    public IRepositorioUsuarios crearRepositorioUsuarios() {
        return new JdbcRepositorioUsuarios();
    }

    @Override
    public IRepositorioPedidos crearRepositorioPedidos() {
        return new JdbcRepositorioPedidos();
    }

    @Override
    public IRepositorioCatalogo crearRepositorioCatalogo() {
        return new JdbcRepositorioCatalogo();
    }

    @Override
    public IRepositorioNotificaciones crearRepositorioNotificaciones() {
        return new JdbcRepositorioNotificaciones();
    }
}
