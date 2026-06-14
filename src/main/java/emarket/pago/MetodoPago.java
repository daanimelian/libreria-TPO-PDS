package emarket.pago;

// Strategy: contrato para todos los métodos de pago
public interface MetodoPago {
    boolean pagar(double monto);
}
