package emarket.pago;

public class PayPal implements MetodoPago {

    private String emailCuenta = "usuario@paypal.com";

    @Override
    public boolean pagar(double monto) {
        System.out.printf("  Procesando pago con PayPal (%s) por $%.2f... APROBADO%n",
                emailCuenta, monto);
        return true;
    }
}
