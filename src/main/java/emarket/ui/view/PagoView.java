package emarket.ui.view;

import emarket.pago.TipoPago;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Vista pasiva de checkout: permite elegir un {@link TipoPago} y completar
 * los datos correspondientes. El cambio de formulario visible según el tipo
 * elegido es puramente visual (CardLayout interno) y no involucra lógica de
 * negocio ni llamadas al backend.
 */
public class PagoView extends JPanel {

    private static final String CARD_TARJETA = "TARJETA";
    private static final String CARD_PAYPAL = "PAYPAL";
    private static final String CARD_MERCADOPAGO = "MERCADOPAGO";
    private static final String CARD_TRANSFERENCIA = "TRANSFERENCIA";

    private final JComboBox<TipoPago> comboTipoPago = new JComboBox<>(TipoPago.values());
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelFormularios = new JPanel(cardLayout);

    private final JTextField campoNumeroTarjeta = new JTextField(16);
    private final JTextField campoTitular = new JTextField(16);
    private final JTextField campoFechaExpiracion = new JTextField(6);

    private final JTextField campoEmailPayPal = new JTextField(18);

    private final JTextField campoEmailMercadoPago = new JTextField(18);
    private final JTextField campoAccessToken = new JTextField(18);

    private final JTextField campoCbu = new JTextField(22);
    private final JTextField campoBanco = new JTextField(18);

    private final JLabel lblTotal = new JLabel("Total a pagar: $ 0.00", SwingConstants.CENTER);
    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnConfirmar = new JButton("Confirmar pago");
    private final JButton btnCancelar = new JButton("Cancelar");

    public PagoView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        panelSuperior.add(new JLabel("Método de pago:"), gbc);
        gbc.gridx = 1;
        panelSuperior.add(comboTipoPago, gbc);

        panelFormularios.add(crearPanelTarjeta(), CARD_TARJETA);
        panelFormularios.add(crearPanelPayPal(), CARD_PAYPAL);
        panelFormularios.add(crearPanelMercadoPago(), CARD_MERCADOPAGO);
        panelFormularios.add(crearPanelTransferencia(), CARD_TRANSFERENCIA);

        comboTipoPago.addActionListener(e -> mostrarFormularioSegunTipo());

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelSuperior, BorderLayout.NORTH);
        panelNorte.add(lblTotal, BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
        add(panelFormularios, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        panelInferior.add(lblMensaje, BorderLayout.NORTH);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);

        mostrarFormularioSegunTipo();
        setPreferredSize(new java.awt.Dimension(420, 320));
    }

    private void mostrarFormularioSegunTipo() {
        TipoPago tipo = getTipoPagoSeleccionado();
        cardLayout.show(panelFormularios, claveParaTipo(tipo));
    }

    private String claveParaTipo(TipoPago tipo) {
        return switch (tipo) {
            case TARJETA_CREDITO -> CARD_TARJETA;
            case PAYPAL -> CARD_PAYPAL;
            case MERCADO_PAGO -> CARD_MERCADOPAGO;
            case TRANSFERENCIA -> CARD_TRANSFERENCIA;
        };
    }

    private JPanel crearPanelTarjeta() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = camposGbc();
        agregarFila(panel, gbc, 0, "Número de tarjeta:", campoNumeroTarjeta);
        agregarFila(panel, gbc, 1, "Titular:", campoTitular);
        agregarFila(panel, gbc, 2, "Expiración (MM/AA):", campoFechaExpiracion);
        return panel;
    }

    private JPanel crearPanelPayPal() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = camposGbc();
        agregarFila(panel, gbc, 0, "Email de PayPal:", campoEmailPayPal);
        return panel;
    }

    private JPanel crearPanelMercadoPago() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = camposGbc();
        agregarFila(panel, gbc, 0, "Email de MercadoPago:", campoEmailMercadoPago);
        agregarFila(panel, gbc, 1, "Access token:", campoAccessToken);
        return panel;
    }

    private JPanel crearPanelTransferencia() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = camposGbc();
        agregarFila(panel, gbc, 0, "CBU (22 dígitos):", campoCbu);
        agregarFila(panel, gbc, 1, "Banco:", campoBanco);
        return panel;
    }

    private GridBagConstraints camposGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JTextField campo) {
        gbc.gridx = 0; gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    public void setTotal(double total) {
        lblTotal.setText(String.format("Total a pagar: $ %.2f", total));
    }

    public TipoPago getTipoPagoSeleccionado() {
        return (TipoPago) comboTipoPago.getSelectedItem();
    }

    public String getNumeroTarjeta() { return campoNumeroTarjeta.getText().trim(); }
    public String getTitularTarjeta() { return campoTitular.getText().trim(); }
    public String getFechaExpiracionTarjeta() { return campoFechaExpiracion.getText().trim(); }

    public String getEmailPayPal() { return campoEmailPayPal.getText().trim(); }

    public String getEmailMercadoPago() { return campoEmailMercadoPago.getText().trim(); }
    public String getAccessToken() { return campoAccessToken.getText().trim(); }

    public String getCbu() { return campoCbu.getText().trim(); }
    public String getBanco() { return campoBanco.getText().trim(); }

    public void mostrarError(String mensaje) {
        lblMensaje.setForeground(java.awt.Color.RED);
        lblMensaje.setText(mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        lblMensaje.setForeground(new java.awt.Color(0, 128, 0));
        lblMensaje.setText(mensaje);
    }

    public void setConfirmarListener(ActionListener listener) {
        btnConfirmar.addActionListener(listener);
    }

    public void setCancelarListener(ActionListener listener) {
        btnCancelar.addActionListener(listener);
    }
}
