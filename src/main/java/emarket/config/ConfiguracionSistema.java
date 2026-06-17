package emarket.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Parámetros globales del sistema (patrón Singleton).
 *
 * <p>Provee acceso a la tasa de IVA y a parámetros de configuración general
 * (moneda, país, versión). Al usar Singleton se garantiza que todos los
 * componentes consulten los mismos valores sin pasarlos como dependencias.
 *
 * <p>La instancia se crea de forma lazy y sincronizada para ser segura en
 * entornos multihilo.
 */
public class ConfiguracionSistema {

    private static ConfiguracionSistema instancia;

    private final double impuestos = 0.21;
    private final Map<String, String> parametros = new HashMap<>();

    private ConfiguracionSistema() {
        parametros.put("moneda", "ARS");
        parametros.put("pais", "Argentina");
        parametros.put("version", "1.0");
        parametros.put("nombre_sistema", "Librería TPO PDS");
    }

    /**
     * Devuelve la única instancia de {@code ConfiguracionSistema}, creándola si es necesario.
     * Sincronizado para evitar condiciones de carrera en entornos multihilo.
     *
     * @return instancia única del sistema
     */
    public static synchronized ConfiguracionSistema getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracionSistema();
        }
        return instancia;
    }

    /**
     * Devuelve la tasa de impuestos (IVA) configurada.
     *
     * @return valor decimal de la tasa; por ejemplo {@code 0.21} representa el 21 %
     */
    public double getImpuestos() {
        return impuestos;
    }

    /**
     * Devuelve el valor de un parámetro de configuración por su clave.
     *
     * @param clave nombre del parámetro (ej: {@code "moneda"}, {@code "pais"})
     * @return valor asociado, o cadena vacía si la clave no existe
     */
    public String getParametro(String clave) {
        return parametros.getOrDefault(clave, "");
    }
}
