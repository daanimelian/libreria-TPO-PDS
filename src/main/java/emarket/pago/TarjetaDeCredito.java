package emarket.pago;

public class TarjetaDeCredito implements MetodoPago {

    private final String numero;
    private final String titular;
    private final String fechaExpiracion;

    public TarjetaDeCredito(String numero, String titular, String fechaExpiracion) {
        this.numero          = numero;
        this.titular         = titular;
        this.fechaExpiracion = fechaExpiracion;
    }

    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        String enmascarado = "**** **** **** " + numero.replaceAll("\\s", "").substring(12);
        System.out.printf("  Procesando pago con Tarjeta de Crédito (%s, titular: %s) por $%.2f... APROBADO%n",
                enmascarado, titular.toUpperCase(), monto);
        return true;
    }

    private boolean validar() {
        String digitos = numero == null ? "" : numero.replaceAll("\\s|-", "");
        if (digitos.length() != 16 || !digitos.matches("\\d+")) {
            System.out.println("  ✗ Número de tarjeta inválido (debe tener 16 dígitos).");
            return false;
        }
        if (titular == null || titular.isBlank()) {
            System.out.println("  ✗ El nombre del titular no puede estar vacío.");
            return false;
        }
        if (fechaExpiracion == null || !fechaExpiracion.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            System.out.println("  ✗ Fecha de expiración inválida (formato MM/AA, ej: 12/27).");
            return false;
        }
        return true;
    }
}
