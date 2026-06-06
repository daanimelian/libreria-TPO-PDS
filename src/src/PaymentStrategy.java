public interface PaymentStrategy {

public boolean procesar(double monto);
public String obtenerDetalles();
public boolean validarDatos();
}
