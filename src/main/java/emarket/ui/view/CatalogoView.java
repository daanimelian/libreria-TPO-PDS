package emarket.ui.view;

import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * Vista pasiva del catálogo: muestra el árbol de categorías/productos (patrón
 * Composite del backend) en un JTree y permite elegir una cantidad para
 * agregar el producto seleccionado al carrito. Solo renderiza datos que ya
 * le llegan armados; no consulta servicios.
 */
public class CatalogoView extends JPanel {

    private final JTree arbol = new JTree();
    private final JLabel lblNombre = new JLabel("-");
    private final JLabel lblPrecio = new JLabel("-");
    private final JLabel lblStock = new JLabel("-");
    private final JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
    private final JButton btnAgregarCarrito = new JButton("Agregar al carrito");
    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    private Producto productoSeleccionado;

    public CatalogoView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        arbol.setRootVisible(true);
        arbol.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        arbol.addTreeSelectionListener(e -> actualizarSeleccion());

        JPanel panelDetalle = new JPanel(new GridBagLayout());
        panelDetalle.setBorder(BorderFactory.createTitledBorder("Detalle del producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelDetalle.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; panelDetalle.add(lblNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelDetalle.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1; panelDetalle.add(lblPrecio, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelDetalle.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1; panelDetalle.add(lblStock, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelDetalle.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1; panelDetalle.add(spinnerCantidad, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panelDetalle.add(btnAgregarCarrito, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panelDetalle.add(lblMensaje, gbc);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(arbol), panelDetalle);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        habilitarFormularioProducto(false);
    }

    /** Reconstruye el árbol visual a partir de la raíz del catálogo ya cargada. */
    public void mostrarCatalogo(ComponenteCatalogo raiz) {
        DefaultMutableTreeNode nodoRaiz = construirNodo(raiz);
        arbol.setModel(new DefaultTreeModel(nodoRaiz));
        productoSeleccionado = null;
        habilitarFormularioProducto(false);
        lblMensaje.setText(" ");
    }

    private DefaultMutableTreeNode construirNodo(ComponenteCatalogo componente) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(componente);
        if (componente instanceof emarket.catalogo.Categoria categoria) {
            for (ComponenteCatalogo hijo : categoria.getHijos()) {
                nodo.add(construirNodo(hijo));
            }
        }
        return nodo;
    }

    private void actualizarSeleccion() {
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
        Object valor = nodo != null ? nodo.getUserObject() : null;
        if (valor instanceof Producto producto) {
            productoSeleccionado = producto;
            lblNombre.setText(producto.getNombre());
            lblPrecio.setText(String.format("$ %.2f", producto.getPrecio()));
            lblStock.setText(String.valueOf(producto.getStock()));
            int maxStock = Math.max(producto.getStock(), 1);
            spinnerCantidad.setModel(new SpinnerNumberModel(1, 1, maxStock, 1));
            habilitarFormularioProducto(true);
        } else {
            productoSeleccionado = null;
            lblNombre.setText("-");
            lblPrecio.setText("-");
            lblStock.setText("-");
            habilitarFormularioProducto(false);
        }
        lblMensaje.setText(" ");
    }

    private void habilitarFormularioProducto(boolean habilitado) {
        spinnerCantidad.setEnabled(habilitado);
        btnAgregarCarrito.setEnabled(habilitado);
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public int getCantidadIngresada() {
        return (Integer) spinnerCantidad.getValue();
    }

    public void mostrarError(String mensaje) {
        lblMensaje.setForeground(java.awt.Color.RED);
        lblMensaje.setText(mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        lblMensaje.setForeground(new java.awt.Color(0, 128, 0));
        lblMensaje.setText(mensaje);
    }

    public void setAgregarCarritoListener(ActionListener listener) {
        btnAgregarCarrito.addActionListener(listener);
    }
}
