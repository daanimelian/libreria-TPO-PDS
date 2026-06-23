package emarket.ui.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Vista pasiva de login. No conoce ni invoca al backend: solo expone los datos
 * ingresados por el usuario y permite registrar listeners para sus botones.
 */
public class LoginView extends JPanel {

    private final JTextField campoUsername = new JTextField(18);
    private final JPasswordField campoPassword = new JPasswordField(18);
    private final JButton btnIniciarSesion = new JButton("Iniciar sesión");
    private final JButton btnRegistrarCliente = new JButton("Registrarme como cliente");
    private final JButton btnRegistrarAdmin = new JButton("Registrarme como administrador");
    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    public LoginView() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("EMarket — Iniciar sesión", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        formulario.add(campoUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        formulario.add(campoPassword, gbc);

        add(formulario, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new javax.swing.BoxLayout(panelInferior, javax.swing.BoxLayout.Y_AXIS));
        lblMensaje.setForeground(java.awt.Color.RED);
        panelInferior.add(lblMensaje);

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnIniciarSesion);
        panelInferior.add(panelBotones);

        JPanel panelRegistro = new JPanel();
        panelRegistro.add(btnRegistrarCliente);
        panelRegistro.add(btnRegistrarAdmin);
        panelInferior.add(panelRegistro);

        add(panelInferior, BorderLayout.SOUTH);
    }

    public String getUsername() {
        return campoUsername.getText().trim();
    }

    public String getPassword() {
        return new String(campoPassword.getPassword());
    }

    public void limpiarCampos() {
        campoUsername.setText("");
        campoPassword.setText("");
        lblMensaje.setText(" ");
    }

    public void mostrarError(String mensaje) {
        lblMensaje.setForeground(java.awt.Color.RED);
        lblMensaje.setText(mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        lblMensaje.setForeground(new java.awt.Color(0, 128, 0));
        lblMensaje.setText(mensaje);
    }

    public void setIniciarSesionListener(ActionListener listener) {
        btnIniciarSesion.addActionListener(listener);
    }

    public void setRegistrarClienteListener(ActionListener listener) {
        btnRegistrarCliente.addActionListener(listener);
    }

    public void setRegistrarAdminListener(ActionListener listener) {
        btnRegistrarAdmin.addActionListener(listener);
    }
}
