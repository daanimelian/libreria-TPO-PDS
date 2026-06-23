package emarket.ui.view;

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
 * Vista pasiva para el alta de un Administrador.
 */
public class RegistroAdminView extends JPanel {

    private final JTextField campoUsername = new JTextField(18);
    private final JPasswordField campoPassword = new JPasswordField(18);
    private final JPasswordField campoConfirmarPassword = new JPasswordField(18);
    private final JPasswordField campoClaveAdmin = new JPasswordField(18);
    private final JButton btnRegistrar = new JButton("Registrarme");
    private final JButton btnCancelar = new JButton("Cancelar");
    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    public RegistroAdminView() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        agregarCampo(gbc, fila++, "Usuario:", campoUsername);
        agregarCampo(gbc, fila++, "Contraseña:", campoPassword);
        agregarCampo(gbc, fila++, "Confirmar contraseña:", campoConfirmarPassword);
        agregarCampo(gbc, fila++, "Clave de administrador:", campoClaveAdmin);

        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        lblMensaje.setForeground(java.awt.Color.RED);
        add(lblMensaje, gbc);
        fila++;

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        add(panelBotones, gbc);
    }

    private void agregarCampo(GridBagConstraints gbc, int fila, String etiqueta, JTextField campo) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = fila;
        add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        add(campo, gbc);
    }

    public String getUsername() { return campoUsername.getText().trim(); }
    public String getPassword() { return new String(campoPassword.getPassword()); }
    public String getConfirmarPassword() { return new String(campoConfirmarPassword.getPassword()); }
    public String getClaveAdmin() { return new String(campoClaveAdmin.getPassword()); }

    public void limpiarCampos() {
        campoUsername.setText("");
        campoPassword.setText("");
        campoConfirmarPassword.setText("");
        campoClaveAdmin.setText("");
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

    public void setRegistrarListener(ActionListener listener) {
        btnRegistrar.addActionListener(listener);
    }

    public void setCancelarListener(ActionListener listener) {
        btnCancelar.addActionListener(listener);
    }
}
