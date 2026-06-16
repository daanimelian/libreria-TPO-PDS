package emarket.ui.view;

import emarket.pedido.Pedido;
import emarket.ui.view.table.PedidoTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * Vista pasiva con el listado de todos los pedidos (solo administradores),
 * con la posibilidad de ver el detalle o avanzar el estado del pedido
 * seleccionado.
 */
public class AdminPedidosView extends JPanel {

    private final PedidoTableModel modeloTabla = new PedidoTableModel();
    private final JTable tabla = new JTable(modeloTabla);
    private final JButton btnVerDetalle = new JButton("Ver detalle");
    private final JButton btnAvanzarEstado = new JButton("Avanzar estado");
    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    public AdminPedidosView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAcciones.add(btnVerDetalle);
        panelAcciones.add(btnAvanzarEstado);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelAcciones, BorderLayout.NORTH);
        panelInferior.add(lblMensaje, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public void mostrarPedidos(List<Pedido> pedidos) {
        modeloTabla.setPedidos(pedidos);
        lblMensaje.setText(" ");
    }

    public Pedido getPedidoSeleccionado() {
        int fila = tabla.getSelectedRow();
        return fila >= 0 ? modeloTabla.getPedidoEn(fila) : null;
    }

    public void mostrarError(String mensaje) {
        lblMensaje.setForeground(java.awt.Color.RED);
        lblMensaje.setText(mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        lblMensaje.setForeground(new java.awt.Color(0, 128, 0));
        lblMensaje.setText(mensaje);
    }

    public void setVerDetalleListener(ActionListener listener) {
        btnVerDetalle.addActionListener(listener);
    }

    public void setAvanzarEstadoListener(ActionListener listener) {
        btnAvanzarEstado.addActionListener(listener);
    }
}
