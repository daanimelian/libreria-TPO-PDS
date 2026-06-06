import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TarjetaPago implements PaymentStrategy {

    private String numeroTarjeta;
    private String cvv;
    private String fechaVencimiento; // formato MM/yy

    public TarjetaPago(String numeroTarjeta, String cvv, String fechaVencimiento) {
        this.numeroTarjeta = numeroTarjeta;
        this.cvv = cvv;
        this.fechaVencimiento = fechaVencimiento;
    }

    @Override
    public boolean procesar(double monto) {
        if (!validarDatos() || monto <= 0) return false;
        System.out.println("Procesando pago con tarjeta por $" + monto);
        return true;
    }

    @Override
    public String obtenerDetalles() {
        String numeroCensurado = "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);
        return "Tarjeta: " + numeroCensurado + " | Vencimiento: " + fechaVencimiento;
    }

    @Override
    public boolean validarDatos() {
        return validarLongitud() && validarFecha() && cvv != null && cvv.matches("\\d{3,4}");
    }

    private boolean validarLongitud() {
        return numeroTarjeta != null && numeroTarjeta.replaceAll("\\s", "").matches("\\d{16}");
    }

    private boolean validarFecha() {
        if (fechaVencimiento == null) return false;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth vencimiento = YearMonth.parse(fechaVencimiento, fmt);
            return vencimiento.isAfter(YearMonth.now()) || vencimiento.equals(YearMonth.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
