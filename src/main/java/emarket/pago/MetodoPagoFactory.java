package emarket.pago;

import java.util.Scanner;

public class MetodoPagoFactory {

    public DatosPago pedirDatos(TipoPago tipo, Scanner sc) {
        System.out.println();
        return switch (tipo) {
            case TARJETA_CREDITO -> TarjetaDeCredito.pedirDatos(sc);
            case PAYPAL          -> PayPal.pedirDatos(sc);
            case MERCADO_PAGO    -> MercadoPago.pedirDatos(sc);
            case TRANSFERENCIA   -> Transferencia.pedirDatos(sc);
        };
    }

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
