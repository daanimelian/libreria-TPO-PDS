package emarket.ui.view;

import emarket.pedido.Pedido;
import emarket.ui.view.table.ItemPedidoTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Vista pasiva con el detalle de un pedido: cabecera (id, estado, total) y
 * tabla de ítems comprados.
 */
public class PedidoDetalleView extends JPanel {

    private final ItemPedidoTableModel modeloTabla = new ItemPedidoTableModel();
    private final JTable tabla = new JTable(modeloTabla);
    private final JLabel lblId = new JLabel("-");
    private final JLabel lblEstado = new JLabel("-");
    private final JLabel lblTotal = new JLabel("-");
    private final JButton btnCerrar = new JButton("Cerrar");

    public PedidoDetalleView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelCabecera = new JPanel(new GridLayout(1, 3, 10, 0));
        panelCabecera.add(crearEtiquetaConValor("Pedido #", lblId));
        panelCabecera.add(crearEtiquetaConValor("Estado: ", lblEstado));
        panelCabecera.add(crearEtiquetaConValor("Total: ", lblTotal));

        add(panelCabecera, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.add(btnCerrar);
        add(panelInferior, BorderLayout.SOUTH);

        setPreferredSize(new java.awt.Dimension(520, 360));
    }

    private JPanel crearEtiquetaConValor(String etiqueta, JLabel valor) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(etiqueta));
        panel.add(valor);
        return panel;
    }

    public void mostrarDetalle(Pedido pedido) {
        lblId.setText(String.valueOf(pedido.getId()));
        lblEstado.setText(pedido.getEstado() != null ? pedido.getEstado().getNombre() : "-");
        lblTotal.setText(String.format("$ %.2f", pedido.getTotal()));
        modeloTabla.setItems(pedido.getItems());
    }

    public void setCerrarListener(ActionListener listener) {
        btnCerrar.addActionListener(listener);
    }
}
