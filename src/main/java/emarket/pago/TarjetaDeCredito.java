package emarket.pago;

import emarket.util.Validaciones;
import java.util.Scanner;

public class TarjetaDeCredito implements MetodoPago {

    public static DatosPago pedirDatos(Scanner sc) {
        String numero, titular, fecha;

        do {
            System.out.print("  Número de tarjeta (16 dígitos) : ");
            numero = Validaciones.normalizarNumeroTarjeta(sc.nextLine().trim());
            if (!Validaciones.esNumeroTarjetaValido(numero))
                System.out.println("  ✗ Debe tener exactamente 16 dígitos numéricos.");
        } while (!Validaciones.esNumeroTarjetaValido(numero));

        do {
            System.out.print("  Nombre del titular             : ");
            titular = sc.nextLine().trim();
            if (titular.isBlank())
                System.out.println("  ✗ El nombre no puede estar vacío.");
        } while (titular.isBlank());

        do {
            System.out.print("  Fecha de expiración (MM/AA)    : ");
            fecha = sc.nextLine().trim();
            if (!Validaciones.esFechaExpiracionValida(fecha))
                System.out.println("  ✗ Formato inválido. Ejemplo: 12/27");
            else if (Validaciones.estaVencida(fecha))
                System.out.println("  ✗ La tarjeta está vencida.");
            else break;
        } while (true);

        return DatosPago.paraTarjeta(numero, titular, fecha);
    }


    private final String numero;
    private final String titular;
    private final String fechaExpiracion;

    public TarjetaDeCredito(String numero, String titular, String fechaExpiracion) {
        this.numero          = numero;
        this.titular         = titular;
        this.fechaExpiracion = fechaExpiracion;
    }

    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        String enmascarado = "**** **** **** " + Validaciones.normalizarNumeroTarjeta(numero).substring(12);
        System.out.printf("  Procesando pago con Tarjeta de Crédito (%s, titular: %s) por $%.2f... APROBADO%n",
                enmascarado, titular.toUpperCase(), monto);
        return true;
    }

    private boolean validar() {
        if (!Validaciones.esNumeroTarjetaValido(numero)) {
            System.out.println("  ✗ Número de tarjeta inválido (debe tener 16 dígitos).");
            return false;
        }
        if (titular == null || titular.isBlank()) {
            System.out.println("  ✗ El nombre del titular no puede estar vacío.");
            return false;
        }
        if (!Validaciones.esFechaExpiracionValida(fechaExpiracion)) {
            System.out.println("  ✗ Fecha de expiración inválida (formato MM/AA, ej: 12/27).");
            return false;
        }
        if (Validaciones.estaVencida(fechaExpiracion)) {
            System.out.println("  ✗ La tarjeta está vencida.");
            return false;
        }
        return true;
    }
}
