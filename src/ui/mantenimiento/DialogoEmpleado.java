package ui.mantenimiento;

import dominio.Empleado;
import utilidades.ValidadorUI;
import utilidades.FiltrosEntrada;
import utilidades.CampoFecha;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class DialogoEmpleado extends JDialog {
    private JTextField txtDocumento, txtNombres, txtApellidos, txtPassword, txtCelular, txtCorreo, txtTipo;
    private CampoFecha campoFecha;
    private boolean aceptado=false;

    public DialogoEmpleado(Window owner, String titulo) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        construirUI();
    }

    private void construirUI() {
        setSize(480, 340);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        txtDocumento = FiltrosEntrada.crearCampoDocumento();
        txtNombres = FiltrosEntrada.crearCampoTexto(50);
        txtApellidos = FiltrosEntrada.crearCampoTexto(50);
        txtPassword = new JTextField();
        txtCelular = FiltrosEntrada.crearCampoCelular();
        txtCorreo = FiltrosEntrada.crearCampoTexto(100);
        campoFecha = new CampoFecha();
        campoFecha.setFecha(LocalDate.now());
        txtTipo = FiltrosEntrada.crearCampoTexto(20);

        form.add(new JLabel("Documento:")); form.add(txtDocumento);
        form.add(new JLabel("Nombres:")); form.add(txtNombres);
        form.add(new JLabel("Apellidos:")); form.add(txtApellidos);
        form.add(new JLabel("PasswordHass:")); form.add(txtPassword);
        form.add(new JLabel("Celular:")); form.add(txtCelular);
        form.add(new JLabel("Correo:")); form.add(txtCorreo);
        form.add(new JLabel("Fecha registro:")); form.add(campoFecha);
        form.add(new JLabel("Tipo Empleado:")); form.add(txtTipo);
        add(form, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Aceptar");
        JButton btnCancel = new JButton("Cancelar");
        acciones.add(btnOk); acciones.add(btnCancel);
        add(acciones, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> { aceptado=false; dispose(); });
        btnOk.addActionListener(e -> {
            // Validar todos los campos con mensajes específicos
            if (!ValidadorUI.validarDocumento(this, txtDocumento.getText(), "Documento")) return;
            if (!ValidadorUI.validarTexto(this, txtNombres.getText(), "Nombres", 100, true)) return;
            if (!ValidadorUI.validarTexto(this, txtApellidos.getText(), "Apellidos", 100, true)) return;
            if (!ValidadorUI.validarTexto(this, txtPassword.getText(), "Password", 60, true)) return;
            if (!ValidadorUI.validarCelular(this, txtCelular.getText(), "Celular", false)) return;
            if (!ValidadorUI.validarEmail(this, txtCorreo.getText(), "Correo", false)) return;
            
            // Validación de fecha ya no es necesaria (CampoFecha siempre contiene una fecha válida)
            if (!ValidadorUI.validarTexto(this, txtTipo.getText(), "Tipo de Empleado", 20, true)) return;
            
            aceptado = true; 
            dispose();
        });
    }

    private void mensaje(String m) { JOptionPane.showMessageDialog(this, m); }

    public void setEmpleado(Empleado e) {
        txtDocumento.setText(e.getNumDocumento());
        txtNombres.setText(e.getNombres());
        txtApellidos.setText(e.getApellidos());
        txtPassword.setText(e.getPasswordHass());
        txtCelular.setText(e.getCelular());
        txtCorreo.setText(e.getCorreo());
        campoFecha.setFecha(e.getFechaRegistro() != null ? e.getFechaRegistro() : LocalDate.now());
        txtTipo.setText(e.getTipoEmpleado());
        txtDocumento.setEditable(false);
    }

    public Empleado getEmpleado() {
        Empleado e = new Empleado();
        e.setNumDocumento(txtDocumento.getText().trim());
        e.setNombres(txtNombres.getText().trim());
        e.setApellidos(txtApellidos.getText().trim());
        e.setPasswordHass(txtPassword.getText().trim());
        e.setCelular(txtCelular.getText().trim());
        e.setCorreo(txtCorreo.getText().trim());
        e.setFechaRegistro(campoFecha.getFecha());
        e.setTipoEmpleado(txtTipo.getText().trim());
        return e;
    }

    public boolean isAceptado() { return aceptado; }
}
