package emarket.pago;

public class MetodoPagoFactory {

    public MetodoPago crearMetodoPago(TipoPago tipo) {
        return switch (tipo) {
            case TARJETA_CREDITO -> new TarjetaDeCredito();
            case PAYPAL          -> new PayPal();
            case TRANSFERENCIA   -> new Transferencia();
        };
    }
}
