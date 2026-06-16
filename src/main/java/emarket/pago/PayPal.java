package emarket.pago;

import emarket.util.Validaciones;
import java.util.Scanner;

public class PayPal implements MetodoPago {

    public static DatosPago pedirDatos(Scanner sc) {
        String email;
        do {
            System.out.print("  Email de tu cuenta PayPal : ");
            email = sc.nextLine().trim();
            if (!Validaciones.esEmailValido(email))
                System.out.println("  ✗ Email inválido. Ejemplo: nombre@dominio.com");
            else break;
        } while (true);
        return DatosPago.paraPayPal(email);
    }


    private final String email;

    public PayPal(String email) {
        this.email = email;
    }

    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        System.out.printf("  Procesando pago con PayPal (%s) por $%.2f... APROBADO%n", email, monto);
        return true;
    }

    private boolean validar() {
        if (!Validaciones.esEmailValido(email)) {
            System.out.println("  ✗ Email de PayPal inválido.");
            return false;
        }
        return true;
    }
}
