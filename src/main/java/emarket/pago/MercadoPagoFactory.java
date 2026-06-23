package emarket.pago;

/**
 * Creador concreto del patrón Factory Method: instancia {@link MercadoPago}.
 */
public class MercadoPagoFactory extends MetodoPagoFactory {

    @Override
    public MetodoPago crearMetodoPago(DatosPago datos) {
        return new MercadoPago(datos.getEmailMercadoPago(), datos.getAccessToken());
    }
}
