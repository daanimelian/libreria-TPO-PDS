package emarket.pago;

/**
 * Creador concreto del patrón Factory Method: instancia {@link Transferencia}.
 */
public class TransferenciaFactory extends MetodoPagoFactory {

    @Override
    public MetodoPago crearMetodoPago(DatosPago datos) {
        return new Transferencia(datos.getCbu(), datos.getBanco());
    }
}
