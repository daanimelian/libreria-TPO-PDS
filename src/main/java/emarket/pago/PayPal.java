package emarket.pago;

import emarket.util.Validaciones;

/**
 * Implementación concreta del patrón Strategy para pagos con PayPal.
 *
 * <p>Valida que el email de la cuenta PayPal tenga formato correcto antes de
 * aprobar el cobro. La recolección de datos del usuario es responsabilidad
 * exclusiva de la capa de presentación ({@code Main}).
 */
public class PayPal implements MetodoPago {

    private final String email;

    /**
     * @param email dirección de correo asociada a la cuenta PayPal del comprador
     */
    public PayPal(String email) {
        this.email = email;
    }

    /**
     * Procesa el cobro validando el email de la cuenta PayPal.
     *
     * @param monto importe a cobrar en la moneda configurada en el sistema
     * @return {@code true} si el pago fue aprobado; {@code false} si el email es inválido
     */
    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        System.out.printf("  Procesando pago con PayPal (%s) por $%.2f... APROBADO%n", email, monto);
        return true;
    }

    /** Verifica que el email tenga formato válido. */
    private boolean validar() {
        if (!Validaciones.esEmailValido(email)) {
            System.out.println("  ✗ Email de PayPal inválido.");
            return false;
        }
        return true;
    }
}
