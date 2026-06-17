package emarket.ui.view.table;

import emarket.pedido.ItemPedido;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Adaptador visual entre una lista de {@link ItemPedido} (snapshot de compra)
 * y un JTable, usado en el detalle de un pedido.
 */
public class ItemPedidoTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {"Producto", "Precio unitario", "Cantidad", "Subtotal"};

    private List<ItemPedido> items = new ArrayList<>();

    public void setItems(List<ItemPedido> items) {
        this.items = items != null ? items : new ArrayList<>();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() { return items.size(); }

    @Override
    public int getColumnCount() { return COLUMNAS.length; }

    @Override
    public String getColumnName(int column) { return COLUMNAS[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ItemPedido item = items.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getNombreProducto();
            case 1 -> String.format("$ %.2f", item.getPrecioUnitario());
            case 2 -> item.getCantidad();
            case 3 -> String.format("$ %.2f", item.getSubtotal());
            default -> null;
        };
    }
}
