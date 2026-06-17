package emarket.pago;

import emarket.util.Validaciones;

/**
 * Implementación concreta del patrón Strategy para pagos con tarjeta de crédito.
 *
 * <p>Valida número de 16 dígitos, titular no vacío y fecha de expiración vigente
 * antes de aprobar el cobro. La recolección de datos del usuario es responsabilidad
 * exclusiva de la capa de presentación ({@code Main}).
 */
public class TarjetaDeCredito implements MetodoPago {

    private final String numero;
    private final String titular;
    private final String fechaExpiracion;

    /**
     * @param numero         número de tarjeta de 16 dígitos (puede incluir espacios/guiones;
     *                       se normalizan internamente)
     * @param titular        nombre del titular tal como aparece en la tarjeta
     * @param fechaExpiracion fecha de vencimiento en formato {@code MM/AA}
     */
    public TarjetaDeCredito(String numero, String titular, String fechaExpiracion) {
        this.numero          = numero;
        this.titular         = titular;
        this.fechaExpiracion = fechaExpiracion;
    }

    /**
     * Procesa el cobro validando los datos de la tarjeta.
     *
     * @param monto importe a cobrar en la moneda configurada en el sistema
     * @return {@code true} si el pago fue aprobado; {@code false} si los datos son inválidos
     */
    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        String enmascarado = "**** **** **** " + Validaciones.normalizarNumeroTarjeta(numero).substring(12);
        System.out.printf("  Procesando pago con Tarjeta de Crédito (%s, titular: %s) por $%.2f... APROBADO%n",
                enmascarado, titular.toUpperCase(), monto);
        return true;
    }

    /** Valida que el número, titular y fecha sean correctos y que la tarjeta no esté vencida. */
    private boolean validar() {
        if (!Validaciones.esNumeroTarjetaValido(numero)) {
            System.out.println("  ✗ Número de tarjeta inválido (debe tener 16 dígitos).");
            return false;
        }
        if (titular == null || titular.isBlank()) {
            System.out.println("  ✗ El nombre del titular no puede estar vacío.");
            return false;
        }
        if (!Validaciones.esFechaExpiracionValida(fechaExpiracion)) {
            System.out.println("  ✗ Fecha de expiración inválida (formato MM/AA, ej: 12/27).");
            return false;
        }
        if (Validaciones.estaVencida(fechaExpiracion)) {
            System.out.println("  ✗ La tarjeta está vencida.");
            return false;
        }
        return true;
    }
}
