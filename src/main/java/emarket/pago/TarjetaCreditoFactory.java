package emarket.pago;

/**
 * Creador concreto del patrón Factory Method: instancia {@link TarjetaDeCredito}.
 */
public class TarjetaCreditoFactory extends MetodoPagoFactory {

    @Override
    public MetodoPago crearMetodoPago(DatosPago datos) {
        return new TarjetaDeCredito(datos.getNumeroTarjeta(), datos.getTitular(), datos.getFechaExpiracion());
    }
}
