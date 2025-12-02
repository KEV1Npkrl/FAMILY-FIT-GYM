package ui.mantenimiento;

import dominio.MetodoPago;
import utilidades.ValidadorUI;
import utilidades.FiltrosEntrada;

import javax.swing.*;
import java.awt.*;

public class DialogoMetodoPago extends JDialog {
    private JTextField txtId;
    private JTextField txtNombre;
    private JCheckBox chkActivo;
    private boolean aceptado=false;

    public DialogoMetodoPago(Window owner, String titulo) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        construirUI();
    }

    private void construirUI() {
        setSize(360, 220);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        txtId = FiltrosEntrada.crearCampoTexto(20);
        txtNombre = FiltrosEntrada.crearCampoTexto(50);
        chkActivo = new JCheckBox();
        form.add(new JLabel("Id:")); form.add(txtId);
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Activo:")); form.add(chkActivo);
        add(form, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        acciones.add(btnOk); acciones.add(btnCancelar);
        add(acciones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> { aceptado=false; dispose(); });
        btnOk.addActionListener(e -> {
            // Validar todos los campos con mensajes específicos
            if (!ValidadorUI.validarTexto(this, txtId.getText(), "ID del Método", 20, true)) return;
            if (!ValidadorUI.validarTexto(this, txtNombre.getText(), "Nombre del Método", 50, true)) return;
            
            aceptado=true; 
            dispose();
        });
    }

    public void setMetodo(MetodoPago m) {
        txtId.setText(m.getIdMetodoPago());
        txtNombre.setText(m.getNombreMetodo());
        chkActivo.setSelected(m.isEstadoActivo());
        txtId.setEditable(false);
    }

    public MetodoPago getMetodo() {
        return new MetodoPago(txtId.getText().trim(), txtNombre.getText().trim(), chkActivo.isSelected());
    }

    public boolean isAceptado() { return aceptado; }
}
