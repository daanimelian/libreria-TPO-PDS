public class TarjetaPago implements PaymentStrategy{
    @Override
    public boolean procesar(double monto) {
        return false;
    }

    @Override
    public String obtenerDetalles() {
        return "";
    }

    @Override
    public boolean validarDatos() {
        return false;
    }

    private boolean validarLongitud(){
        return false;
    }

    private boolean validarFecha(){
        return false;
    }
}
