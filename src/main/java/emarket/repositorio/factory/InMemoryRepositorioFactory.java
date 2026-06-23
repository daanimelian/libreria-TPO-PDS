package emarket.repositorio.factory;

import emarket.repositorio.IRepositorioCatalogo;
import emarket.repositorio.IRepositorioNotificaciones;
import emarket.repositorio.IRepositorioPedidos;
import emarket.repositorio.IRepositorioUsuarios;
import emarket.repositorio.inmemory.InMemoryRepositorioCatalogo;
import emarket.repositorio.inmemory.InMemoryRepositorioNotificaciones;
import emarket.repositorio.inmemory.InMemoryRepositorioPedidos;
import emarket.repositorio.inmemory.InMemoryRepositorioUsuarios;

// Concrete Factory: crea la familia de repositorios en memoria
public class InMemoryRepositorioFactory implements RepositorioFactory {

    @Override
    public IRepositorioUsuarios crearRepositorioUsuarios() {
        return new InMemoryRepositorioUsuarios();
    }

    @Override
    public IRepositorioPedidos crearRepositorioPedidos() {
        return new InMemoryRepositorioPedidos();
    }

    @Override
    public IRepositorioCatalogo crearRepositorioCatalogo() {
        return new InMemoryRepositorioCatalogo();
    }

    @Override
    public IRepositorioNotificaciones crearRepositorioNotificaciones() {
        return new InMemoryRepositorioNotificaciones();
    }
}
