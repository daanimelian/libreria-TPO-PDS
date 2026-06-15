package emarket.pago;

import emarket.pedido.Pedido;

public class ProcesadorPagos {

    private final MetodoPagoFactory metodoPagoFactory = new MetodoPagoFactory();

    public boolean procesarCobro(Pedido pedido, TipoPago tipo, DatosPago datos) {
        MetodoPago metodo = metodoPagoFactory.crearMetodoPago(tipo, datos);
        return metodo.pagar(pedido.getTotal());
    }
}
