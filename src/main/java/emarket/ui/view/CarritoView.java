package emarket.ui.view;

import emarket.carrito.ItemCarrito;
import emarket.ui.view.table.CarritoTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * Vista pasiva del carrito de compras. Muestra los ítems en una tabla y
 * expone los listeners de las acciones disponibles (modificar, eliminar,
 * vaciar, confirmar compra).
 */
public class CarritoView extends JPanel {

    private final CarritoTableModel modeloTabla = new CarritoTableModel();
    private final JTable tabla = new JTable(modeloTabla);
    private final JLabel lblTotal = new JLabel("Total: $ 0.00", SwingConstants.RIGHT);
    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    private final JButton btnModificarCantidad = new JButton("Modificar cantidad");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnVaciar = new JButton("Vaciar carrito");
    private final JButton btnConfirmarCompra = new JButton("Confirmar compra");

    public CarritoView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAcciones.add(btnModificarCantidad);
        panelAcciones.add(btnEliminar);
        panelAcciones.add(btnVaciar);
        panelAcciones.add(btnConfirmarCompra);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelAcciones, BorderLayout.WEST);
        panelSur.add(lblTotal, BorderLayout.EAST);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelSur, BorderLayout.NORTH);
        panelInferior.add(lblMensaje, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);
    }

    public void mostrarItems(List<ItemCarrito> items, double total) {
        modeloTabla.setItems(items);
        lblTotal.setText(String.format("Total: $ %.2f", total));
        lblMensaje.setText(" ");
    }

    public ItemCarrito getItemSeleccionado() {
        int fila = tabla.getSelectedRow();
        return fila >= 0 ? modeloTabla.getItemEn(fila) : null;
    }

    /** Pide al usuario la nueva cantidad mediante un diálogo simple. Devuelve -1 si cancela o el valor es inválido. */
    public int pedirNuevaCantidad(int cantidadActual) {
        String entrada = JOptionPane.showInputDialog(
                this, "Nueva cantidad:", String.valueOf(cantidadActual));
        if (entrada == null) return -1;
        try {
            return Integer.parseInt(entrada.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void mostrarError(String mensaje) {
        lblMensaje.setForeground(java.awt.Color.RED);
        lblMensaje.setText(mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        lblMensaje.setForeground(new java.awt.Color(0, 128, 0));
        lblMensaje.setText(mensaje);
    }

    public void setModificarCantidadListener(ActionListener listener) {
        btnModificarCantidad.addActionListener(listener);
    }

    public void setEliminarListener(ActionListener listener) {
        btnEliminar.addActionListener(listener);
    }

    public void setVaciarListener(ActionListener listener) {
        btnVaciar.addActionListener(listener);
    }

    public void setConfirmarCompraListener(ActionListener listener) {
        btnConfirmarCompra.addActionListener(listener);
    }
}
