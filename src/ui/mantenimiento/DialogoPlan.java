package ui.mantenimiento;

import dominio.Plan;
import dominio.TipoPlan;
import dominio.*;
import utilidades.ValidadorUI;
import utilidades.FiltrosEntrada;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class DialogoPlan extends JDialog {
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDuracion;
    private JTextField txtCosto;
    private JTextField txtDescripcion;
    private JComboBox<TipoPlan> cboTipo;

    private boolean aceptado = false;

    public DialogoPlan(Window owner, String titulo) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        construirUI();
    }

    private void construirUI() {
        setSize(420, 360);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        txtId = FiltrosEntrada.crearCampoEntero(10);
        txtNombre = FiltrosEntrada.crearCampoTexto(100);
        txtDuracion = FiltrosEntrada.crearCampoEntero(5);
        txtCosto = FiltrosEntrada.crearCampoDecimal(10);
        txtDescripcion = FiltrosEntrada.crearCampoTexto(500);
        cboTipo = new JComboBox<>(TipoPlan.values());

        int row=0;
        c.gridx=0; c.gridy=row; form.add(new JLabel("IdPlan:"), c); c.gridx=1; form.add(txtId, c); row++;
        c.gridx=0; c.gridy=row; form.add(new JLabel("Nombre:"), c); c.gridx=1; form.add(txtNombre, c); row++;
        c.gridx=0; c.gridy=row; form.add(new JLabel("Duración (días):"), c); c.gridx=1; form.add(txtDuracion, c); row++;
        c.gridx=0; c.gridy=row; form.add(new JLabel("Costo:"), c); c.gridx=1; form.add(txtCosto, c); row++;
        c.gridx=0; c.gridy=row; form.add(new JLabel("Descripción:"), c); c.gridx=1; form.add(txtDescripcion, c); row++;
        c.gridx=0; c.gridy=row; form.add(new JLabel("Tipo:"), c); c.gridx=1; form.add(cboTipo, c); row++;

        add(form, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        acciones.add(btnAceptar); acciones.add(btnCancelar);

        btnCancelar.addActionListener(e -> { aceptado=false; dispose(); });
        btnAceptar.addActionListener(e -> {
            if (validar()) { aceptado = true; dispose(); }
        });

        add(acciones, BorderLayout.SOUTH);
    }

    private boolean validar() {
        // Validar usando ValidadorUI con mensajes específicos
        if (!ValidadorUI.validarEntero(this, txtId.getText(), "Id del Plan", 1)) return false;
        if (!ValidadorUI.validarTexto(this, txtNombre.getText(), "Nombre del Plan", 60, true)) return false;
        if (!ValidadorUI.validarEntero(this, txtDuracion.getText(), "Duración", 1)) return false;
        if (!ValidadorUI.validarDecimal(this, txtCosto.getText(), "Costo")) return false;
        if (!ValidadorUI.validarTexto(this, txtDescripcion.getText(), "Descripción", 200, false)) return false;
        if (!ValidadorUI.validarComboBox(this, cboTipo.getSelectedItem(), "Tipo de Plan")) return false;
        
        return true;
    }

    private void mensaje(String m) { JOptionPane.showMessageDialog(this, m, "Validación", JOptionPane.WARNING_MESSAGE); }

    public boolean isAceptado() { return aceptado; }

    public void setPlan(Plan p) {
        txtId.setText(String.valueOf(p.getIdPlan()));
        txtNombre.setText(p.getNombrePlan());
        txtDuracion.setText(String.valueOf(p.getDuracionDias()));
        txtCosto.setText(p.getCosto()!=null? p.getCosto().toPlainString(): "");
        txtDescripcion.setText(p.getDescripcion()!=null? p.getDescripcion(): "");
        cboTipo.setSelectedItem(p.getTipo());
        txtId.setEditable(false); // evitar cambiar PK en edición
    }

    public Plan getPlan() {
        int id = Integer.parseInt(txtId.getText());
        String nombre = txtNombre.getText().trim();
        int dur = Integer.parseInt(txtDuracion.getText());
        BigDecimal costo = new BigDecimal(txtCosto.getText());
        String desc = txtDescripcion.getText().trim();
        TipoPlan tipo = (TipoPlan) cboTipo.getSelectedItem();
        Plan instancia;
        switch (tipo) {
            case PROMO: instancia = new PlanPromo(); break;
            case FULL: instancia = new PlanFull(); break;
            case FITNESS: instancia = new PlanFitness(); break;
            case INTER: instancia = new PlanInter(); break;
            case PAREJA_2X: instancia = new PlanPareja2x(); break;
            case TRIO_3X: instancia = new PlanTrio3x(); break;
            case BAILE: instancia = new PlanBaile(); break;
            default: instancia = new PlanFull();
        }
        instancia.setIdPlan(id);
        instancia.setNombrePlan(nombre);
        instancia.setDuracionDias(dur);
        instancia.setCosto(costo);
        instancia.setDescripcion(desc);
        instancia.setTipo(tipo);
        return instancia;
    }
}
