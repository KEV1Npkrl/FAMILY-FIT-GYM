package ui.operaciones;

import dominio.MetodoPago;
import servicios.ServicioCatalogo;
import utilidades.ValidadorUI;
import utilidades.FiltrosEntrada;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DialogoPago extends JDialog {
    private JTextField txtDocumentoEmpleado, txtMonto;
    private JComboBox<MetodoPago> cboMetodo;
    private boolean aceptado=false;

    public DialogoPago(Window owner) {
        super(owner, "Registrar pago", ModalityType.APPLICATION_MODAL);
        construirUI();
    }

    private void construirUI() {
        setSize(420, 220);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        txtDocumentoEmpleado = FiltrosEntrada.crearCampoDocumento();
        txtMonto = FiltrosEntrada.crearCampoDecimal(10);
        cboMetodo = new JComboBox<>();
        cargarMetodos();

        form.add(new JLabel("Documento Empleado:")); form.add(txtDocumentoEmpleado);
        form.add(new JLabel("Método de Pago:")); form.add(cboMetodo);
        form.add(new JLabel("Monto:")); form.add(txtMonto);
        add(form, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Registrar");
        JButton btnCancel = new JButton("Cancelar");
        acciones.add(btnOk); acciones.add(btnCancel);
        add(acciones, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> { aceptado=false; dispose(); });
        btnOk.addActionListener(e -> {
            // Validar todos los campos con mensajes específicos
            if (!ValidadorUI.validarDocumento(this, txtDocumentoEmpleado.getText(), "Documento del Empleado")) return;
            if (!ValidadorUI.validarComboBox(this, cboMetodo.getSelectedItem(), "Método de Pago")) return;
            if (!ValidadorUI.validarDecimal(this, txtMonto.getText(), "Monto")) return;
            
            aceptado=true; 
            dispose();
        });
    }

    private void cargarMetodos() {
        ServicioCatalogo cat = new ServicioCatalogo();
        List<MetodoPago> metodos = cat.listarMetodosPago();
        DefaultComboBoxModel<MetodoPago> model = new DefaultComboBoxModel<>();
        for (MetodoPago m: metodos) model.addElement(m);
        cboMetodo.setModel(model);
        cboMetodo.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MetodoPago) setText(((MetodoPago) value).getNombreMetodo());
                return c;
            }
        });
    }

    public boolean isAceptado() { return aceptado; }
    public String getDocumentoEmpleado() { return txtDocumentoEmpleado.getText().trim(); }
    public String getIdMetodoPago() { return ((MetodoPago)cboMetodo.getSelectedItem()).getIdMetodoPago(); }
    public BigDecimal getMonto() { return new BigDecimal(txtMonto.getText().trim()); }
}
