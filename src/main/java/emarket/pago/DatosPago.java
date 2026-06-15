package emarket.pago;

public class DatosPago {

    // Tarjeta de crédito
    private final String numeroTarjeta;
    private final String titular;
    private final String fechaExpiracion;

    // PayPal
    private final String emailPayPal;

    // Transferencia
    private final String cbu;
    private final String banco;

    private DatosPago(Builder b) {
        this.numeroTarjeta    = b.numeroTarjeta;
        this.titular          = b.titular;
        this.fechaExpiracion  = b.fechaExpiracion;
        this.emailPayPal      = b.emailPayPal;
        this.cbu              = b.cbu;
        this.banco            = b.banco;
    }

    public static DatosPago paraTarjeta(String numero, String titular, String fechaExpiracion) {
        return new Builder().numeroTarjeta(numero).titular(titular).fechaExpiracion(fechaExpiracion).build();
    }

    public static DatosPago paraPayPal(String email) {
        return new Builder().emailPayPal(email).build();
    }

    public static DatosPago paraTransferencia(String cbu, String banco) {
        return new Builder().cbu(cbu).banco(banco).build();
    }

    public String getNumeroTarjeta()   { return numeroTarjeta; }
    public String getTitular()         { return titular; }
    public String getFechaExpiracion() { return fechaExpiracion; }
    public String getEmailPayPal()     { return emailPayPal; }
    public String getCbu()             { return cbu; }
    public String getBanco()           { return banco; }

    private static class Builder {
        String numeroTarjeta, titular, fechaExpiracion, emailPayPal, cbu, banco;

        Builder numeroTarjeta(String v)   { this.numeroTarjeta   = v; return this; }
        Builder titular(String v)         { this.titular         = v; return this; }
        Builder fechaExpiracion(String v) { this.fechaExpiracion = v; return this; }
        Builder emailPayPal(String v)     { this.emailPayPal     = v; return this; }
        Builder cbu(String v)             { this.cbu             = v; return this; }
        Builder banco(String v)           { this.banco           = v; return this; }

        DatosPago build() { return new DatosPago(this); }
    }
}
