package emarket.pago;

/**
 * Creador abstracto del patrón Factory Method para instanciar estrategias de pago.
 *
 * <p>Cada subclase concreta sobreescribe {@link #crearMetodoPago(DatosPago)} para
 * construir la implementación de {@link MetodoPago} que le corresponde, sin que
 * esta clase conozca los tipos concretos (principio OCP).
 *
 * <p>La selección de qué factory usar según el {@link TipoPago} elegido por el
 * usuario es responsabilidad de {@link ProcesadorPagos}.
 */
public abstract class MetodoPagoFactory {

    /**
     * Factory method: crea e instancia el {@link MetodoPago} concreto.
     *
     * @param datos datos de pago ya validados y recolectados por la UI
     * @return la estrategia de pago concreta lista para ejecutar {@code pagar()}
     */
    public abstract MetodoPago crearMetodoPago(DatosPago datos);
}
