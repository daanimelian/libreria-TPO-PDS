package emarket.repositorio.factory;

import emarket.repositorio.IRepositorioCatalogo;
import emarket.repositorio.IRepositorioNotificaciones;
import emarket.repositorio.IRepositorioPedidos;
import emarket.repositorio.IRepositorioUsuarios;

/**
 * Abstract Factory: define la interfaz para crear familias de repositorios.
 *
 * Garantiza que todos los repositorios usados en una misma sesión sean
 * del mismo tipo (todos en memoria O todos JDBC), eliminando mezclas inconsistentes.
 */
public interface RepositorioFactory {
    IRepositorioUsuarios       crearRepositorioUsuarios();
    IRepositorioPedidos        crearRepositorioPedidos();
    IRepositorioCatalogo       crearRepositorioCatalogo();
    IRepositorioNotificaciones crearRepositorioNotificaciones();
}
