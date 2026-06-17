package emarket.pago;

import emarket.pedido.Pedido;

/**
 * Contexto del patrón Strategy para el procesamiento de pagos.
 *
 * <p>Delega el cobro al {@link MetodoPago} concreto creado por {@link MetodoPagoFactory},
 * sin conocer qué estrategia específica se está usando. Esto permite agregar nuevos
 * métodos de pago sin modificar esta clase (principio OCP).
 */
public class ProcesadorPagos {

    private final MetodoPagoFactory metodoPagoFactory = new MetodoPagoFactory();

    /**
     * Procesa el cobro del total del pedido usando el método de pago indicado.
     *
     * @param pedido   pedido cuyo total se va a cobrar
     * @param tipo     tipo de método de pago elegido por el cliente
     * @param datos    datos del método de pago recolectados por la UI
     * @return {@code true} si el pago fue aprobado; {@code false} si fue rechazado
     */
    public boolean procesarCobro(Pedido pedido, TipoPago tipo, DatosPago datos) {
        MetodoPago metodo = metodoPagoFactory.crearMetodoPago(tipo, datos);
        return metodo.pagar(pedido.getTotal());
    }
}
