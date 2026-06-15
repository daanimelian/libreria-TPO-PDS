package emarket.pago;

public class TarjetaDeCredito implements MetodoPago {

    private String numeroEnmascarado = "**** **** **** 1234";
    private String titular = "JUAN PEREZ";
    private String fechaExpiracion = "12/27";

    @Override
    public boolean pagar(double monto) {
        System.out.printf("  Procesando pago con Tarjeta de Crédito (%s, titular: %s) por $%.2f... APROBADO%n",
                numeroEnmascarado, titular, monto);
        return true;
    }
}
