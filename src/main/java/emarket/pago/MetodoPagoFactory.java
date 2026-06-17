package emarket.pago;

/**
 * Factory para instanciar la estrategia de pago concreta a partir de un {@link TipoPago}
 * y los {@link DatosPago} ya recolectados por la capa de presentación.
 *
 * <p>Responsabilidad única: traducir el par {@code (TipoPago, DatosPago)} en el objeto
 * {@link MetodoPago} correspondiente. La recolección de datos del usuario (input de
 * consola, formulario, etc.) queda fuera del alcance de esta clase.
 */
public class MetodoPagoFactory {

    /**
     * Crea e instancia el {@link MetodoPago} concreto que corresponde al tipo elegido.
     *
     * @param tipo  tipo de método de pago seleccionado por el usuario
     * @param datos objeto con los datos de pago ya validados y recolectados
     * @return la estrategia de pago concreta lista para ejecutar {@code pagar()}
     */
    public MetodoPago crearMetodoPago(TipoPago tipo, DatosPago datos) {
        return switch (tipo) {
            case TARJETA_CREDITO -> new TarjetaDeCredito(
                    datos.getNumeroTarjeta(), datos.getTitular(), datos.getFechaExpiracion());
            case PAYPAL          -> new PayPal(datos.getEmailPayPal());
            case MERCADO_PAGO    -> new MercadoPago(datos.getEmailMercadoPago(), datos.getAccessToken());
            case TRANSFERENCIA   -> new Transferencia(datos.getCbu(), datos.getBanco());
        };
    }
}
