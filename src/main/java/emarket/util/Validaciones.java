package emarket.util;

import java.time.YearMonth;

/**
 * Utilidades estáticas de validación de formato para datos de entrada del usuario.
 *
 * <p>Clase no instanciable (constructor privado). Todos los métodos son estáticos
 * y no tienen efectos secundarios.
 */
public class Validaciones {

    private Validaciones() {}

    /**
     * Verifica que el email tenga formato básico (texto@dominio).
     *
     * @param email cadena a validar
     * @return {@code true} si contiene {@code @} con texto antes y después
     */
    public static boolean esEmailValido(String email) {
        if (email == null) return false;
        int at = email.indexOf('@');
        return at > 0 && at < email.length() - 1;
    }

    /**
     * Elimina espacios y guiones de un número de tarjeta.
     *
     * @param numero número de tarjeta posiblemente con separadores
     * @return cadena con solo dígitos, o cadena vacía si {@code numero} es {@code null}
     */
    public static String normalizarNumeroTarjeta(String numero) {
        return numero == null ? "" : numero.replaceAll("[\\s-]", "");
    }

    /**
     * Verifica que el número de tarjeta tenga exactamente 16 dígitos.
     *
     * @param numero número de tarjeta (puede contener espacios/guiones; se normaliza antes)
     * @return {@code true} si el número normalizado tiene 16 dígitos numéricos
     */
    public static boolean esNumeroTarjetaValido(String numero) {
        String digitos = normalizarNumeroTarjeta(numero);
        return digitos.length() == 16 && digitos.matches("\\d+");
    }

    /**
     * Verifica que la fecha de expiración tenga formato {@code MM/AA}.
     *
     * @param fecha cadena a validar
     * @return {@code true} si el formato es {@code MM/AA} con mes entre 01 y 12
     */
    public static boolean esFechaExpiracionValida(String fecha) {
        return fecha != null && fecha.matches("(0[1-9]|1[0-2])/\\d{2}");
    }

    /**
     * Verifica si una tarjeta está vencida comparando con el mes/año actual.
     *
     * @param fechaMMAA fecha en formato {@code MM/AA} (ej: {@code "03/26"})
     * @return {@code true} si la tarjeta está vencida; {@code false} si es vigente o el formato es inválido
     */
    public static boolean estaVencida(String fechaMMAA) {
        if (!esFechaExpiracionValida(fechaMMAA)) return false;
        String[] partes = fechaMMAA.split("/");
        YearMonth expiracion = YearMonth.of(2000 + Integer.parseInt(partes[1]), Integer.parseInt(partes[0]));
        return expiracion.isBefore(YearMonth.now());
    }

    /**
     * Verifica que el CBU tenga exactamente 22 dígitos numéricos.
     *
     * @param cbu Clave Bancaria Uniforme a validar
     * @return {@code true} si el CBU tiene exactamente 22 dígitos
     */
    public static boolean esCbuValido(String cbu) {
        return cbu != null && cbu.matches("\\d{22}");
    }
}
