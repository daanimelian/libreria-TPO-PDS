package emarket.ui.controller;

import emarket.facade.LibreriaFacade;
import emarket.pago.DatosPago;
import emarket.pago.TipoPago;
import emarket.ui.view.PagoView;
import emarket.util.Validaciones;
import javax.swing.JDialog;

/**
 * Controla el diálogo de checkout: arma el {@link DatosPago} según el
 * {@link TipoPago} elegido en la vista, valida formato con las utilidades
 * de negocio existentes y delega la confirmación al facade.
 */
public class PagoController {

    private final LibreriaFacade facade;
    private final PagoView view;
    private final JDialog dialogo;
    private final Runnable alConfirmarExito;

    public PagoController(LibreriaFacade facade, PagoView view, JDialog dialogo, Runnable alConfirmarExito) {
        this.facade = facade;
        this.view = view;
        this.dialogo = dialogo;
        this.alConfirmarExito = alConfirmarExito;
        view.setConfirmarListener(e -> confirmarPago());
        view.setCancelarListener(e -> dialogo.dispose());
    }

    private void confirmarPago() {
        TipoPago tipo = view.getTipoPagoSeleccionado();
        DatosPago datos;
        try {
            datos = construirDatosPago(tipo);
        } catch (IllegalArgumentException ex) {
            view.mostrarError(ex.getMessage());
            return;
        }

        try {
            facade.confirmarCompra(tipo, datos);
        } catch (RuntimeException ex) {
            view.mostrarError(ex.getMessage());
            return;
        }

        dialogo.dispose();
        alConfirmarExito.run();
    }

    private DatosPago construirDatosPago(TipoPago tipo) {
        return switch (tipo) {
            case TARJETA_CREDITO -> {
                String numero = view.getNumeroTarjeta();
                String titular = view.getTitularTarjeta();
                String fecha = view.getFechaExpiracionTarjeta();
                if (!Validaciones.esNumeroTarjetaValido(numero))
                    throw new IllegalArgumentException("Número de tarjeta inválido (debe tener 16 dígitos).");
                if (titular.isBlank())
                    throw new IllegalArgumentException("Ingresá el titular de la tarjeta.");
                if (!Validaciones.esFechaExpiracionValida(fecha))
                    throw new IllegalArgumentException("Fecha de expiración inválida (formato MM/AA).");
                if (Validaciones.estaVencida(fecha))
                    throw new IllegalArgumentException("La tarjeta está vencida.");
                yield DatosPago.paraTarjeta(Validaciones.normalizarNumeroTarjeta(numero), titular, fecha);
            }
            case PAYPAL -> {
                String email = view.getEmailPayPal();
                if (!Validaciones.esEmailValido(email))
                    throw new IllegalArgumentException("Ingresá un email de PayPal válido.");
                yield DatosPago.paraPayPal(email);
            }
            case MERCADO_PAGO -> {
                String email = view.getEmailMercadoPago();
                String token = view.getAccessToken();
                if (!Validaciones.esEmailValido(email))
                    throw new IllegalArgumentException("Ingresá un email de MercadoPago válido.");
                if (token.isBlank())
                    throw new IllegalArgumentException("Ingresá el access token de MercadoPago.");
                yield DatosPago.paraMercadoPago(email, token);
            }
            case TRANSFERENCIA -> {
                String cbu = view.getCbu();
                String banco = view.getBanco();
                if (!Validaciones.esCbuValido(cbu))
                    throw new IllegalArgumentException("El CBU debe tener 22 dígitos.");
                if (banco.isBlank())
                    throw new IllegalArgumentException("Ingresá el nombre del banco.");
                yield DatosPago.paraTransferencia(cbu, banco);
            }
        };
    }
}
