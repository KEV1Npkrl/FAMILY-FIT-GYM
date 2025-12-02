package ui.mantenimiento;

import dominio.Socio;
import utilidades.ValidadorUI;
import utilidades.FiltrosEntrada;
import utilidades.CampoFecha;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class DialogoSocio extends JDialog {
    private JTextField txtDocumento, txtNombres, txtApellidos, txtPassword, txtCelular, txtCorreo;
    private CampoFecha campoFecha;
    private boolean aceptado=false;

    public DialogoSocio(Window owner, String titulo) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        construirUI();
    }

    private void construirUI() {
        setSize(460, 320);
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

        form.add(new JLabel("Documento:")); form.add(txtDocumento);
        form.add(new JLabel("Nombres:")); form.add(txtNombres);
        form.add(new JLabel("Apellidos:")); form.add(txtApellidos);
        form.add(new JLabel("PasswordHass:")); form.add(txtPassword);
        form.add(new JLabel("Celular:")); form.add(txtCelular);
        form.add(new JLabel("Correo:")); form.add(txtCorreo);
        form.add(new JLabel("Fecha registro:")); form.add(campoFecha);

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
            
            aceptado = true; 
            dispose();
        });
    }

    private void mensaje(String m) { JOptionPane.showMessageDialog(this, m); }

    public void setSocio(Socio s) {
        txtDocumento.setText(s.getNumDocumento());
        txtNombres.setText(s.getNombres());
        txtApellidos.setText(s.getApellidos());
        txtPassword.setText(s.getPasswordHass());
        txtCelular.setText(s.getCelular());
        txtCorreo.setText(s.getCorreo());
        campoFecha.setFecha(s.getFechaRegistro() != null ? s.getFechaRegistro() : LocalDate.now());
        txtDocumento.setEditable(false);
    }

    public Socio getSocio() {
        Socio s = new Socio();
        s.setNumDocumento(txtDocumento.getText().trim());
        s.setNombres(txtNombres.getText().trim());
        s.setApellidos(txtApellidos.getText().trim());
        s.setPasswordHass(txtPassword.getText().trim());
        s.setCelular(txtCelular.getText().trim());
        s.setCorreo(txtCorreo.getText().trim());
        s.setFechaRegistro(campoFecha.getFecha());
        return s;
    }

    public boolean isAceptado() { return aceptado; }
}
