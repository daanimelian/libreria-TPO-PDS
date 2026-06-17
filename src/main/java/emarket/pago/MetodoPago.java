package emarket.pago;

/**
 * Contrato del patrón Strategy para los métodos de pago (Strategy — interfaz).
 *
 * <p>Permite seleccionar en tiempo de ejecución el algoritmo de cobro sin modificar
 * el contexto ({@link ProcesadorPagos}). Cada implementación concreta encapsula
 * la lógica de validación y procesamiento propia de su medio de pago:
 * <ul>
 *   <li>{@link TarjetaDeCredito}</li>
 *   <li>{@link PayPal}</li>
 *   <li>{@link MercadoPago}</li>
 *   <li>{@link Transferencia}</li>
 * </ul>
 */
public interface MetodoPago {

    /**
     * Procesa el cobro por el monto indicado.
     *
     * @param monto importe a cobrar (incluye impuestos)
     * @return {@code true} si el pago fue aprobado; {@code false} si los datos son inválidos
     *         o el cobro fue rechazado
     */
    boolean pagar(double monto);
}
