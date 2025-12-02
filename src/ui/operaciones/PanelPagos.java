package ui.operaciones;

import dominio.Membresia;
import dominio.Pago;
import servicios.ServicioMembresia;
import servicios.ServicioPago;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelPagos extends JPanel {
    private final ServicioMembresia servicioMembresia;
    private final ServicioPago servicioPago;
    private final MembresiaTableModel modeloMembresias;
    private final PagoTableModel modeloPagos;
    private final JTable tablaMembresias, tablaPagos;
    private final JTextField txtDocumentoSocio;

    public PanelPagos() {
        this.servicioMembresia = new ServicioMembresia();
        this.servicioPago = new ServicioPago();
        this.modeloMembresias = new MembresiaTableModel();
        this.modeloPagos = new PagoTableModel();
        this.tablaMembresias = new JTable(modeloMembresias);
        this.tablaPagos = new JTable(modeloPagos);
        this.txtDocumentoSocio = new JTextField(15);
        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout(8,8));

        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(new JLabel("Documento Socio:"));
        tb.add(txtDocumentoSocio);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnPagar = new JButton("Registrar Pago");
        tb.add(btnBuscar); tb.add(btnPagar);
        add(tb, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaMembresias), new JScrollPane(tablaPagos));
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> buscar());
        btnPagar.addActionListener(e -> pagar());

        tablaMembresias.getSelectionModel().addListSelectionListener(e -> cargarPagosSeleccion());
    }

    private void buscar() {
        String doc = txtDocumentoSocio.getText().trim();
        if (doc.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese documento"); return; }
        List<Membresia> lista = servicioMembresia.listarPorSocio(doc);
        modeloMembresias.setDatos(lista);
        modeloPagos.setDatos(java.util.Collections.emptyList());
    }

    private void cargarPagosSeleccion() {
        int fila = tablaMembresias.getSelectedRow();
        if (fila<0) return;
        Membresia m = modeloMembresias.getAt(fila);
        List<Pago> pagos = servicioPago.listarPorMembresia(m.getIdMembresia());
        modeloPagos.setDatos(pagos);
    }

    private void pagar() {
        int fila = tablaMembresias.getSelectedRow();
        if (fila<0) { JOptionPane.showMessageDialog(this, "Seleccione una membresía"); return; }
        Membresia m = modeloMembresias.getAt(fila);
        DialogoPago dlg = new DialogoPago(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            boolean ok = servicioPago.registrarPago(m.getIdMembresia(), dlg.getIdMetodoPago(), dlg.getDocumentoEmpleado(), dlg.getMonto());
            if (!ok) JOptionPane.showMessageDialog(this, "No se pudo registrar el pago", "Error", JOptionPane.ERROR_MESSAGE);
            cargarPagosSeleccion();
        }
    }
}
