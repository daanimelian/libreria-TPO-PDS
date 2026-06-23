package emarket.pago;

import emarket.pedido.Pedido;
import java.util.Map;

/**
 * Contexto del patrón Strategy para el procesamiento de pagos.
 *
 * <p>Selecciona la {@link MetodoPagoFactory} concreta según el {@link TipoPago} elegido
 * y delega la creación del {@link MetodoPago} a esa factory (Factory Method). De este
 * modo no conoce los tipos concretos de métodos de pago ni la lógica de construcción.
 */
public class ProcesadorPagos {

    private static final Map<TipoPago, MetodoPagoFactory> FACTORIES = Map.of(
            TipoPago.TARJETA_CREDITO, new TarjetaCreditoFactory(),
            TipoPago.PAYPAL,          new PayPalFactory(),
            TipoPago.MERCADO_PAGO,    new MercadoPagoFactory(),
            TipoPago.TRANSFERENCIA,   new TransferenciaFactory()
    );

    /**
     * Procesa el cobro del total del pedido usando el método de pago indicado.
     *
     * @param pedido pedido cuyo total se va a cobrar
     * @param tipo   tipo de método de pago elegido por el cliente
     * @param datos  datos del método de pago recolectados por la UI
     * @return {@code true} si el pago fue aprobado; {@code false} si fue rechazado
     */
    public boolean procesarCobro(Pedido pedido, TipoPago tipo, DatosPago datos) {
        MetodoPagoFactory factory = FACTORIES.get(tipo);
        MetodoPago metodo = factory.crearMetodoPago(datos);
        return metodo.pagar(pedido.getTotal());
    }
}
