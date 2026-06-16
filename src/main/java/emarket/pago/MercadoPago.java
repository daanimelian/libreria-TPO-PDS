package emarket.pago;

import emarket.util.Validaciones;
import java.util.Scanner;

public class MercadoPago implements MetodoPago {

    public static DatosPago pedirDatos(Scanner sc) {
        String email;
        do {
            System.out.print("  Email de tu cuenta MercadoPago : ");
            email = sc.nextLine().trim();
            if (!Validaciones.esEmailValido(email))
                System.out.println("  ✗ Email inválido. Ejemplo: nombre@dominio.com");
            else break;
        } while (true);

        String accessToken;
        do {
            System.out.print("  Access Token                   : ");
            accessToken = sc.nextLine().trim();
            if (accessToken.isBlank())
                System.out.println("  ✗ El access token no puede estar vacío.");
            else break;
        } while (true);

        return DatosPago.paraMercadoPago(email, accessToken);
    }


    private final String email;
    private final String accessToken;

    public MercadoPago(String email, String accessToken) {
        this.email       = email;
        this.accessToken = accessToken;
    }

    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        System.out.printf("  Procesando pago con MercadoPago (%s) por $%.2f... APROBADO%n", email, monto);
        return true;
    }

    private boolean validar() {
        if (!Validaciones.esEmailValido(email)) {
            System.out.println("  ✗ Email de MercadoPago inválido.");
            return false;
        }
        if (accessToken == null || accessToken.isBlank()) {
            System.out.println("  ✗ Access token inválido.");
            return false;
        }
        return true;
    }
}
