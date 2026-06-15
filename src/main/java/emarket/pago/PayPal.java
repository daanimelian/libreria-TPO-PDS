package emarket.pago;

public class PayPal implements MetodoPago {

    private final String email;

    public PayPal(String email) {
        this.email = email;
    }

    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        System.out.printf("  Procesando pago con PayPal (%s) por $%.2f... APROBADO%n", email, monto);
        return true;
    }

    private boolean validar() {
        if (email == null || email.isBlank()) {
            System.out.println("  ✗ El email de PayPal no puede estar vacío.");
            return false;
        }
        int at = email.indexOf('@');
        if (at <= 0 || at >= email.length() - 1) {
            System.out.println("  ✗ Email de PayPal inválido.");
            return false;
        }
        return true;
    }
}
