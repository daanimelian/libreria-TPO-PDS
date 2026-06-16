public class MercadoPago implements PaymentStrategy {

    private String cuentaEmail;
    private String accessToken;

    public MercadoPago(String cuentaEmail, String accessToken) {
        this.cuentaEmail = cuentaEmail;
        this.accessToken = accessToken;
    }

    @Override
    public boolean procesar(double monto) {
        if (!validarDatos() || monto <= 0) return false;
        System.out.println("Procesando pago con MercadoPago por $" + monto + " desde " + cuentaEmail);
        return true;
    }

    @Override
    public String obtenerDetalles() {
        return "MercadoPago | Cuenta: " + cuentaEmail;
    }

    @Override
    public boolean validarDatos() {
        boolean emailValido = cuentaEmail != null && cuentaEmail.matches("^[\\w.+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
        boolean tokenValido = accessToken != null && !accessToken.isBlank();
        return emailValido && tokenValido;
    }
}
