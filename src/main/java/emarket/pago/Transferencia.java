package emarket.pago;

import java.util.Scanner;

public class Transferencia implements MetodoPago {

    public static DatosPago pedirDatos(Scanner sc) {
        System.out.print("  CBU (22 dígitos) : ");
        String cbu = sc.nextLine().trim();
        System.out.print("  Banco            : ");
        String banco = sc.nextLine().trim();
        return DatosPago.paraTransferencia(cbu, banco);
    }


    private final String cbu;
    private final String banco;

    public Transferencia(String cbu, String banco) {
        this.cbu   = cbu;
        this.banco = banco;
    }

    @Override
    public boolean pagar(double monto) {
        if (!validar()) return false;
        System.out.printf("  Procesando transferencia bancaria (%s - CBU: %s) por $%.2f... APROBADO%n",
                banco, cbu, monto);
        return true;
    }

    private boolean validar() {
        if (cbu == null || !cbu.matches("\\d{22}")) {
            System.out.println("  ✗ CBU inválido (debe tener exactamente 22 dígitos).");
            return false;
        }
        if (banco == null || banco.isBlank()) {
            System.out.println("  ✗ El nombre del banco no puede estar vacío.");
            return false;
        }
        return true;
    }
}
