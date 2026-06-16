package emarket.util;

import java.time.YearMonth;

public class Validaciones {

    private Validaciones() {}

    public static boolean esEmailValido(String email) {
        if (email == null) return false;
        int at = email.indexOf('@');
        return at > 0 && at < email.length() - 1;
    }

    public static String normalizarNumeroTarjeta(String numero) {
        return numero == null ? "" : numero.replaceAll("[\\s-]", "");
    }

    public static boolean esNumeroTarjetaValido(String numero) {
        String digitos = normalizarNumeroTarjeta(numero);
        return digitos.length() == 16 && digitos.matches("\\d+");
    }

    public static boolean esFechaExpiracionValida(String fecha) {
        return fecha != null && fecha.matches("(0[1-9]|1[0-2])/\\d{2}");
    }

    public static boolean estaVencida(String fechaMMAA) {
        if (!esFechaExpiracionValida(fechaMMAA)) return false;
        String[] partes = fechaMMAA.split("/");
        YearMonth expiracion = YearMonth.of(2000 + Integer.parseInt(partes[1]), Integer.parseInt(partes[0]));
        return expiracion.isBefore(YearMonth.now());
    }

    public static boolean esCbuValido(String cbu) {
        return cbu != null && cbu.matches("\\d{22}");
    }
}
