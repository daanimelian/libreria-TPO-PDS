import java.io.File;

public class TransferPago implements PaymentStrategy {

    private String codigoReferencia;
    private String archivoComprobante;
    private String estadoValidacion;

    public TransferPago(String codigoReferencia, String archivoComprobante) {
        this.codigoReferencia = codigoReferencia;
        this.archivoComprobante = archivoComprobante;
        this.estadoValidacion = "PENDIENTE";
    }

    @Override
    public boolean procesar(double monto) {
        if (!validarDatos() || monto <= 0) {
            estadoValidacion = "RECHAZADO";
            return false;
        }
        estadoValidacion = "APROBADO";
        System.out.println("Transferencia procesada por $" + monto + " | Referencia: " + codigoReferencia);
        return true;
    }

    @Override
    public String obtenerDetalles() {
        return "Transferencia | Referencia: " + codigoReferencia + " | Estado: " + estadoValidacion;
    }

    @Override
    public boolean validarDatos() {
        return validarReferencia() && validarComprobante();
    }

    private boolean validarComprobante() {
        if (archivoComprobante == null || archivoComprobante.isBlank()) return false;
        return new File(archivoComprobante).exists();
    }

    private boolean validarReferencia() {
        return codigoReferencia != null && codigoReferencia.matches("[A-Za-z0-9]{6,20}");
    }
}
