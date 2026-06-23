package emarket.ui.view;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * Vista pasiva que muestra la lista de notificaciones del cliente actual.
 */
public class NotificacionesView extends JPanel {

    private final DefaultListModel<String> modeloLista = new DefaultListModel<>();
    private final JList<String> listaNotificaciones = new JList<>(modeloLista);
    private final JLabel lblVacio = new JLabel("No tenés notificaciones nuevas.", SwingConstants.CENTER);
    private final JButton btnCerrar = new JButton("Cerrar");

    public NotificacionesView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Notificaciones"), BorderLayout.NORTH);
        add(new JScrollPane(listaNotificaciones), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.add(btnCerrar);
        add(panelInferior, BorderLayout.SOUTH);

        setPreferredSize(new java.awt.Dimension(420, 320));
    }

    public void mostrarNotificaciones(List<String> notificaciones) {
        modeloLista.clear();
        if (notificaciones == null || notificaciones.isEmpty()) {
            modeloLista.addElement(lblVacio.getText());
            return;
        }
        notificaciones.forEach(modeloLista::addElement);
    }

    public void setCerrarListener(ActionListener listener) {
        btnCerrar.addActionListener(listener);
    }
}
