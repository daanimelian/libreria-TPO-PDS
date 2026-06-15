package emarket.pago;

public class MetodoPagoFactory {

    public MetodoPago crearMetodoPago(TipoPago tipo, DatosPago datos) {
        return switch (tipo) {
            case TARJETA_CREDITO -> new TarjetaDeCredito(
                    datos.getNumeroTarjeta(), datos.getTitular(), datos.getFechaExpiracion());
            case PAYPAL          -> new PayPal(datos.getEmailPayPal());
            case TRANSFERENCIA   -> new Transferencia(datos.getCbu(), datos.getBanco());
        };
    }
}
