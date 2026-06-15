package emarket.config;

import java.util.HashMap;
import java.util.Map;

// Singleton: una sola instancia con parámetros del sistema
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

    public static ConfiguracionSistema getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracionSistema();
        }
        return instancia;
    }

    public double getImpuestos() {
        return impuestos;
    }

    public String getParametro(String clave) {
        return parametros.getOrDefault(clave, "");
    }
}
