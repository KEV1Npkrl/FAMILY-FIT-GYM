package ui.operaciones;

import dominio.Plan;
import servicios.ServicioCatalogo;
import utilidades.ValidadorUI;
import utilidades.FiltrosEntrada;
import utilidades.CampoFecha;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DialogoMembresia extends JDialog {
    private JTextField txtDocumento, txtEstado;
    private CampoFecha campoFechaInicio;
    private JComboBox<Plan> cboPlan;
    private boolean aceptado=false;

    public DialogoMembresia(Window owner) {
        super(owner, "Nueva membresía", ModalityType.APPLICATION_MODAL);
        construirUI();
    }

    private void construirUI() {
        setSize(420, 260);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        txtDocumento = FiltrosEntrada.crearCampoDocumento();
        campoFechaInicio = new CampoFecha();
        campoFechaInicio.setFecha(LocalDate.now());
        txtEstado = FiltrosEntrada.crearCampoTexto(50);
        txtEstado.setText("ACTIVA");
        cboPlan = new JComboBox<>();

        cargarPlanes();

        form.add(new JLabel("Documento Socio:")); form.add(txtDocumento);
        form.add(new JLabel("Plan:")); form.add(cboPlan);
        form.add(new JLabel("Fecha inicio:")); form.add(campoFechaInicio);
        form.add(new JLabel("Estado:")); form.add(txtEstado);
        add(form, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Crear");
        JButton btnCancel = new JButton("Cancelar");
        acciones.add(btnOk); acciones.add(btnCancel);
        add(acciones, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> { aceptado=false; dispose(); });
        btnOk.addActionListener(e -> {
            // Validar todos los campos con mensajes específicos
            if (!ValidadorUI.validarDocumento(this, txtDocumento.getText(), "Documento del Socio")) return;
            if (!ValidadorUI.validarComboBox(this, cboPlan.getSelectedItem(), "Plan")) return;
            // Validación de fecha ya no es necesaria (CampoFecha siempre contiene una fecha válida)
            if (!ValidadorUI.validarTexto(this, txtEstado.getText(), "Estado", 50, true)) return;
            
            aceptado=true; 
            dispose();
        });
    }

    private void cargarPlanes() {
        ServicioCatalogo cat = new ServicioCatalogo();
        List<Plan> planes = cat.listarPlanes();
        DefaultComboBoxModel<Plan> model = new DefaultComboBoxModel<>();
        for (Plan p: planes) model.addElement(p);
        cboPlan.setModel(model);
        cboPlan.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Plan) setText(((Plan) value).getNombrePlan());
                return c;
            }
        });
    }

    public boolean isAceptado() { return aceptado; }
    public String getDocumento() { return txtDocumento.getText().trim(); }
    public int getIdPlan() { return ((Plan)cboPlan.getSelectedItem()).getIdPlan(); }
    public LocalDate getFechaInicio() { return campoFechaInicio.getFecha(); }
    public String getEstado() { return txtEstado.getText().trim(); }
}
