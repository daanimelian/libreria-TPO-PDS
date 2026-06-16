package emarket.ui.view.table;

import emarket.pedido.Pedido;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Adaptador visual entre una lista de {@link Pedido} y un JTable. Se reutiliza
 * tanto para "Mis pedidos" (cliente) como para "Todos los pedidos" (admin).
 */
public class PedidoTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {"ID", "Cliente", "Estado", "Total"};

    private List<Pedido> pedidos = new ArrayList<>();

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos != null ? pedidos : new ArrayList<>();
        fireTableDataChanged();
    }

    public Pedido getPedidoEn(int fila) {
        if (fila < 0 || fila >= pedidos.size()) return null;
        return pedidos.get(fila);
    }

    @Override
    public int getRowCount() { return pedidos.size(); }

    @Override
    public int getColumnCount() { return COLUMNAS.length; }

    @Override
    public String getColumnName(int column) { return COLUMNAS[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Pedido pedido = pedidos.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> pedido.getId();
            case 1 -> pedido.getCliente().getUsername();
            case 2 -> pedido.getEstado() != null ? pedido.getEstado().getNombre() : "-";
            case 3 -> String.format("$ %.2f", pedido.getTotal());
            default -> null;
        };
    }
}
