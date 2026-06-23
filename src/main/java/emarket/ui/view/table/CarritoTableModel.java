package emarket.ui.view.table;

import emarket.carrito.ItemCarrito;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Adaptador puramente visual entre una lista de {@link ItemCarrito} y un
 * JTable. No contiene lógica de negocio: solo formatea datos ya calculados
 * por el backend para mostrarlos en columnas.
 */
public class CarritoTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {"Producto", "Precio unitario", "Cantidad", "Subtotal"};

    private List<ItemCarrito> items = new ArrayList<>();

    public void setItems(List<ItemCarrito> items) {
        this.items = items != null ? items : new ArrayList<>();
        fireTableDataChanged();
    }

    public ItemCarrito getItemEn(int fila) {
        if (fila < 0 || fila >= items.size()) return null;
        return items.get(fila);
    }

    @Override
    public int getRowCount() { return items.size(); }

    @Override
    public int getColumnCount() { return COLUMNAS.length; }

    @Override
    public String getColumnName(int column) { return COLUMNAS[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ItemCarrito item = items.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getProducto().getNombre();
            case 1 -> String.format("$ %.2f", item.getProducto().getPrecio());
            case 2 -> item.getCantidad();
            case 3 -> String.format("$ %.2f", item.getSubtotal());
            default -> null;
        };
    }
}
