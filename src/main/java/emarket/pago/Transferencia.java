package emarket.pago;

public class Transferencia implements MetodoPago {

    private String cbu = "0000003100012345678901";
    private String banco = "Banco Nación";

    @Override
    public boolean pagar(double monto) {
        System.out.printf("  Procesando transferencia bancaria (%s - CBU: %s) por $%.2f... APROBADO%n",
                banco, cbu, monto);
        return true;
    }
}
