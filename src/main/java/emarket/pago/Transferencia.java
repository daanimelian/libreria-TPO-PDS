package emarket.pago;

import emarket.util.Validaciones;

/**
 * Implementación concreta del patrón Strategy para pagos por transferencia bancaria.
 *
 * <p>Valida el CBU (22 dígitos) y que el nombre del banco no esté vacío antes de
 * aprobar el cobro. La recolección de datos del usuario es responsabilidad exclusiva
 * de la capa de presentación ({@code Main}).
 */
public class Transferencia implements MetodoPago {

    private final String cbu;
    private final String banco;

    /**
     * @param cbu   Clave Bancaria Uniforme de 22 dígitos de la cuenta destino
     * @param banco nombre de la entidad bancaria (ej: "Banco Nación", "Santander")
     */
    public Transferencia(String cbu, String banco) {
        this.cbu   = cbu;
        this.banco = banco;
    }

    /**
     * Procesa el cobro validando el CBU y el nombre del banco.
     *
     * @param monto importe a cobrar en la moneda configurada en el sistema
     * @return {@code true} si el pago fue aprobado; {@code false} si los datos son inválidos
     */
    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        System.out.printf("  Procesando transferencia bancaria (%s - CBU: %s) por $%.2f... APROBADO%n",
                banco, cbu, monto);
        return true;
    }

    /** Verifica que el CBU tenga 22 dígitos y que el banco no esté en blanco. */
    private boolean validar() {
        if (!Validaciones.esCbuValido(cbu)) {
            System.out.println("  ✗ CBU inválido (debe tener exactamente 22 dígitos).");
            return false;
        }
        if (banco == null || banco.isBlank()) {
            System.out.println("  ✗ El nombre del banco no puede estar vacío.");
            return false;
        }
        return true;
    }
}
