package emarket.pago;

import emarket.util.Validaciones;

/**
 * Implementación concreta del patrón Strategy para pagos con MercadoPago.
 *
 * <p>Valida email de cuenta y access token no vacío antes de aprobar el cobro.
 * La recolección de datos del usuario es responsabilidad exclusiva de la capa
 * de presentación ({@code Main}).
 */
public class MercadoPago implements MetodoPago {

    private final String email;
    private final String accessToken;

    /**
     * @param email       dirección de correo asociada a la cuenta MercadoPago del comprador
     * @param accessToken token de acceso emitido por MercadoPago para autorizar el cobro
     */
    public MercadoPago(String email, String accessToken) {
        this.email       = email;
        this.accessToken = accessToken;
    }

    /**
     * Procesa el cobro validando el email y el access token.
     *
     * @param monto importe a cobrar en la moneda configurada en el sistema
     * @return {@code true} si el pago fue aprobado; {@code false} si los datos son inválidos
     */
    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        System.out.printf("  Procesando pago con MercadoPago (%s) por $%.2f... APROBADO%n", email, monto);
        return true;
    }

    /** Verifica email válido y access token no vacío. */
    private boolean validar() {
        if (!Validaciones.esEmailValido(email)) {
            System.out.println("  ✗ Email de MercadoPago inválido.");
            return false;
        }
        if (accessToken == null || accessToken.isBlank()) {
            System.out.println("  ✗ Access token inválido.");
            return false;
        }
        return true;
    }
}
