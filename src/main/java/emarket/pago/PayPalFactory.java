package emarket.pago;

/**
 * Creador concreto del patrón Factory Method: instancia {@link PayPal}.
 */
public class PayPalFactory extends MetodoPagoFactory {

    @Override
    public MetodoPago crearMetodoPago(DatosPago datos) {
        return new PayPal(datos.getEmailPayPal());
    }
}
