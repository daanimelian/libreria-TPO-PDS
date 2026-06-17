package emarket.pago;

/**
 * Contenedor inmutable de los datos recolectados para un pago (patrón Builder).
 *
 * <p>Agrupa todos los posibles campos de los distintos métodos de pago. Los campos
 * no aplicables a un método particular quedan en {@code null}. Los factory methods
 * estáticos ({@link #paraTarjeta}, {@link #paraPayPal}, etc.) construyen la instancia
 * con solo los campos relevantes para cada tipo, dejando el resto vacíos.
 *
 * <p>Esta clase no contiene lógica de validación de formato; esa responsabilidad
 * corresponde a las implementaciones concretas de {@link MetodoPago} (para dominio)
 * y a la capa de presentación (para formato de entrada del usuario).
 */
public class DatosPago {

    // Tarjeta de crédito
    private final String numeroTarjeta;
    private final String titular;
    private final String fechaExpiracion;

    // PayPal
    private final String emailPayPal;

    // MercadoPago
    private final String emailMercadoPago;
    private final String accessToken;

    // Transferencia
    private final String cbu;
    private final String banco;

    private DatosPago(Builder b) {
        this.numeroTarjeta    = b.numeroTarjeta;
        this.titular          = b.titular;
        this.fechaExpiracion  = b.fechaExpiracion;
        this.emailPayPal      = b.emailPayPal;
        this.emailMercadoPago = b.emailMercadoPago;
        this.accessToken      = b.accessToken;
        this.cbu              = b.cbu;
        this.banco            = b.banco;
    }

    /**
     * Construye {@code DatosPago} para pago con tarjeta de crédito.
     *
     * @param numero          número de tarjeta de 16 dígitos
     * @param titular         nombre del titular
     * @param fechaExpiracion fecha de expiración en formato {@code MM/AA}
     */
    public static DatosPago paraTarjeta(String numero, String titular, String fechaExpiracion) {
        return new Builder().numeroTarjeta(numero).titular(titular).fechaExpiracion(fechaExpiracion).build();
    }

    /**
     * Construye {@code DatosPago} para pago con PayPal.
     *
     * @param email dirección de correo de la cuenta PayPal
     */
    public static DatosPago paraPayPal(String email) {
        return new Builder().emailPayPal(email).build();
    }

    /**
     * Construye {@code DatosPago} para pago con MercadoPago.
     *
     * @param email       dirección de correo de la cuenta MercadoPago
     * @param accessToken token de acceso de la cuenta
     */
    public static DatosPago paraMercadoPago(String email, String accessToken) {
        return new Builder().emailMercadoPago(email).accessToken(accessToken).build();
    }

    /**
     * Construye {@code DatosPago} para pago por transferencia bancaria.
     *
     * @param cbu   CBU de 22 dígitos de la cuenta destino
     * @param banco nombre de la entidad bancaria
     */
    public static DatosPago paraTransferencia(String cbu, String banco) {
        return new Builder().cbu(cbu).banco(banco).build();
    }

    /** @return número de tarjeta de crédito (o {@code null} si no aplica) */
    public String getNumeroTarjeta()   { return numeroTarjeta; }

    /** @return nombre del titular de la tarjeta (o {@code null} si no aplica) */
    public String getTitular()         { return titular; }

    /** @return fecha de expiración de la tarjeta en formato {@code MM/AA} (o {@code null} si no aplica) */
    public String getFechaExpiracion() { return fechaExpiracion; }

    /** @return email de la cuenta PayPal (o {@code null} si no aplica) */
    public String getEmailPayPal()      { return emailPayPal; }

    /** @return email de la cuenta MercadoPago (o {@code null} si no aplica) */
    public String getEmailMercadoPago() { return emailMercadoPago; }

    /** @return access token de MercadoPago (o {@code null} si no aplica) */
    public String getAccessToken()      { return accessToken; }

    /** @return CBU de 22 dígitos (o {@code null} si no aplica) */
    public String getCbu()              { return cbu; }

    /** @return nombre del banco (o {@code null} si no aplica) */
    public String getBanco()           { return banco; }

    /** Builder interno para construir instancias de {@code DatosPago}. */
    private static class Builder {
        String numeroTarjeta, titular, fechaExpiracion, emailPayPal, emailMercadoPago, accessToken, cbu, banco;

        Builder numeroTarjeta(String v)    { this.numeroTarjeta    = v; return this; }
        Builder titular(String v)          { this.titular          = v; return this; }
        Builder fechaExpiracion(String v)  { this.fechaExpiracion  = v; return this; }
        Builder emailPayPal(String v)      { this.emailPayPal      = v; return this; }
        Builder emailMercadoPago(String v) { this.emailMercadoPago = v; return this; }
        Builder accessToken(String v)      { this.accessToken      = v; return this; }
        Builder cbu(String v)              { this.cbu              = v; return this; }
        Builder banco(String v)            { this.banco            = v; return this; }

        DatosPago build() { return new DatosPago(this); }
    }
}
