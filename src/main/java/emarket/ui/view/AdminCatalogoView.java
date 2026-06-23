package emarket.ui.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminCatalogoView extends JPanel {

    // --- Agregar producto ---
    private final JTextField txtCategoria      = new JTextField(12);
    private final JTextField txtId             = new JTextField(6);
    private final JTextField txtNombre         = new JTextField(14);
    private final JTextField txtPrecio         = new JTextField(8);
    private final JTextField txtStock          = new JTextField(6);
    private final JButton    btnAgregarProducto = new JButton("Agregar producto");

    // --- Agregar categoría ---
    private final JTextField txtNombreCategoria = new JTextField(14);
    private final JTextField txtCategoriaPadre  = new JTextField(14);
    private final JButton    btnAgregarCategoria = new JButton("Agregar categoría");

    // --- Modificar stock ---
    private final JTextField txtIdStock        = new JTextField(6);
    private final JTextField txtNuevoStock     = new JTextField(6);
    private final JButton    btnModificarStock  = new JButton("Modificar stock");

    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    public AdminCatalogoView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormularios = new JPanel(new GridLayout(1, 3, 10, 0));
        panelFormularios.add(panelAgregarProducto());
        panelFormularios.add(panelAgregarCategoria());
        panelFormularios.add(panelModificarStock());

        add(panelFormularios, BorderLayout.CENTER);
        add(lblMensaje, BorderLayout.SOUTH);
    }

    private JPanel panelAgregarProducto() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Nuevo producto", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        agregarFila(p, gbc, row++, "Categoría:",  txtCategoria);
        agregarFila(p, gbc, row++, "ID:",          txtId);
        agregarFila(p, gbc, row++, "Nombre:",      txtNombre);
        agregarFila(p, gbc, row++, "Precio ($):",  txtPrecio);
        agregarFila(p, gbc, row++, "Stock:",       txtStock);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        p.add(btnAgregarProducto, gbc);
        return p;
    }

    private JPanel panelAgregarCategoria() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Nueva categoría", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        agregarFila(p, gbc, row++, "Nombre:",         txtNombreCategoria);
        agregarFila(p, gbc, row++, "Categoría padre:", txtCategoriaPadre);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        p.add(btnAgregarCategoria, gbc);
        return p;
    }

    private JPanel panelModificarStock() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Modificar stock", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        agregarFila(p, gbc, row++, "ID producto:",  txtIdStock);
        agregarFila(p, gbc, row++, "Nuevo stock:",  txtNuevoStock);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        p.add(btnModificarStock, gbc);
        return p;
    }

    private void agregarFila(JPanel p, GridBagConstraints gbc, int row, String label, JComponent campo) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        p.add(campo, gbc);
    }

    // --- Getters ---
    public String getCategoria()       { return txtCategoria.getText().trim(); }
    public String getIdProducto()      { return txtId.getText().trim(); }
    public String getNombreProducto()  { return txtNombre.getText().trim(); }
    public String getPrecio()          { return txtPrecio.getText().trim(); }
    public String getStock()           { return txtStock.getText().trim(); }
    public String getNombreCategoria() { return txtNombreCategoria.getText().trim(); }
    public String getCategoriaPadre()  { return txtCategoriaPadre.getText().trim(); }
    public String getIdStock()         { return txtIdStock.getText().trim(); }
    public String getNuevoStock()      { return txtNuevoStock.getText().trim(); }

    // --- Listeners ---
    public void setAgregarProductoListener(ActionListener l)  { btnAgregarProducto.addActionListener(l); }
    public void setAgregarCategoriaListener(ActionListener l) { btnAgregarCategoria.addActionListener(l); }
    public void setModificarStockListener(ActionListener l)   { btnModificarStock.addActionListener(l); }

    // --- Feedback ---
    public void mostrarMensaje(String msg) {
        lblMensaje.setForeground(new Color(0, 128, 0));
        lblMensaje.setText(msg);
    }

    public void mostrarError(String msg) {
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setText(msg);
    }

    public void limpiarCamposProducto() {
        txtCategoria.setText("");
        txtId.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
    }

    public void limpiarCamposCategoria() {
        txtNombreCategoria.setText("");
        txtCategoriaPadre.setText("");
    }

    public void limpiarCamposStock() {
        txtIdStock.setText("");
        txtNuevoStock.setText("");
    }
}
