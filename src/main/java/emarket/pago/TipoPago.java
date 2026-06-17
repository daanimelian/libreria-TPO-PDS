package emarket.pago;

/**
 * Enumeración de los métodos de pago disponibles en el sistema.
 *
 * <p>Se usa para seleccionar en tiempo de ejecución qué implementación de
 * {@link MetodoPago} instanciar mediante {@link MetodoPagoFactory}.
 */
public enum TipoPago {
    /** Pago con tarjeta de crédito (requiere número, titular y fecha de expiración). */
    TARJETA_CREDITO,
    /** Pago a través de una cuenta PayPal (requiere email registrado). */
    PAYPAL,
    /** Pago a través de MercadoPago (requiere email y access token). */
    MERCADO_PAGO,
    /** Pago por transferencia bancaria (requiere CBU de 22 dígitos y nombre del banco). */
    TRANSFERENCIA
}
