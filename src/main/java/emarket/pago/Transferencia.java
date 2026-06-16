package emarket.pago;

import emarket.util.Validaciones;
import java.util.Scanner;

public class Transferencia implements MetodoPago {

    public static DatosPago pedirDatos(Scanner sc) {
        String cbu, banco;

        do {
            System.out.print("  CBU (22 dígitos) : ");
            cbu = sc.nextLine().trim();
            if (!Validaciones.esCbuValido(cbu))
                System.out.println("  ✗ El CBU debe tener exactamente 22 dígitos numéricos.");
        } while (!Validaciones.esCbuValido(cbu));

        do {
            System.out.print("  Banco            : ");
            banco = sc.nextLine().trim();
            if (banco.isBlank())
                System.out.println("  ✗ El nombre del banco no puede estar vacío.");
        } while (banco.isBlank());

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
        if (!Validaciones.esCbuValido(cbu)) {
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
