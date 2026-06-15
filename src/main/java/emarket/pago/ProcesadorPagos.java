package emarket.pago;

import emarket.pedido.Pedido;

public class ProcesadorPagos {

    private MetodoPagoFactory metodoPagoFactory = new MetodoPagoFactory();

    public boolean procesarCobro(Pedido pedido, TipoPago tipo) {
        MetodoPago metodo = metodoPagoFactory.crearMetodoPago(tipo);
        return metodo.pagar(pedido.getTotal());
    }
}
