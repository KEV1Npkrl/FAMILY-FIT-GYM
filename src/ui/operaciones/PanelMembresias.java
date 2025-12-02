package ui.operaciones;

import dominio.Membresia;
import servicios.ServicioMembresia;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelMembresias extends JPanel {
    private final ServicioMembresia servicio;
    private final MembresiaTableModel modelo;
    private final JTable tabla;
    private final JTextField txtDocumento;

    public PanelMembresias() {
        this.servicio = new ServicioMembresia();
        this.modelo = new MembresiaTableModel();
        this.tabla = new JTable(modelo);
        this.txtDocumento = new JTextField(15);
        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout(8,8));

        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(new JLabel("Documento Socio:"));
        tb.add(txtDocumento);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnNueva = new JButton("Nueva");
        tb.add(btnBuscar); tb.add(btnNueva);
        add(tb, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> buscar());
        btnNueva.addActionListener(e -> nueva());
    }

    private void buscar() {
        String doc = txtDocumento.getText().trim();
        if (doc.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese documento"); return; }
        List<Membresia> lista = servicio.listarPorSocio(doc);
        modelo.setDatos(lista);
    }

    private void nueva() {
        DialogoMembresia dlg = new DialogoMembresia(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            boolean ok = servicio.crearNueva(dlg.getDocumento(), dlg.getIdPlan(), dlg.getFechaInicio(), dlg.getEstado());
            if (!ok) JOptionPane.showMessageDialog(this, "No se pudo crear la membresía", "Error", JOptionPane.ERROR_MESSAGE);
            else {
                txtDocumento.setText(dlg.getDocumento());
                buscar();
            }
        }
    }
}
