package ui.seguridad;

import dominio.SesionUsuario;
import servicios.ServicioAutenticacion;
import utilidades.ValidadorUI;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para cambio de contraseña del usuario actual
 */
public class PanelCambioPassword extends JPanel {
    private JPasswordField txtPasswordActual;
    private JPasswordField txtPasswordNueva;
    private JPasswordField txtConfirmarPassword;
    private JButton btnCambiar;
    private JLabel lblUsuarioActual;
    
    private ServicioAutenticacion servicioAuth;
    
    public PanelCambioPassword() {
        this.servicioAuth = new ServicioAutenticacion();
        construirUI();
    }
    
    private void construirUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        SesionUsuario sesion = SesionUsuario.getInstance();
        lblUsuarioActual = new JLabel("Cambiar contraseña - Usuario: " + sesion.getNombresCompletos());
        lblUsuarioActual.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(lblUsuarioActual);
        add(header, BorderLayout.NORTH);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Contraseña actual
        gbc.gridx = 0; gbc.gridy = 0;
        panelPrincipal.add(new JLabel("Contraseña actual:"), gbc);
        gbc.gridx = 1;
        txtPasswordActual = new JPasswordField(20);
        panelPrincipal.add(txtPasswordActual, gbc);
        
        // Nueva contraseña
        gbc.gridx = 0; gbc.gridy = 1;
        panelPrincipal.add(new JLabel("Nueva contraseña:"), gbc);
        gbc.gridx = 1;
        txtPasswordNueva = new JPasswordField(20);
        panelPrincipal.add(txtPasswordNueva, gbc);
        
        // Confirmar contraseña
        gbc.gridx = 0; gbc.gridy = 2;
        panelPrincipal.add(new JLabel("Confirmar contraseña:"), gbc);
        gbc.gridx = 1;
        txtConfirmarPassword = new JPasswordField(20);
        panelPrincipal.add(txtConfirmarPassword, gbc);
        
        // Botón cambiar
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnCambiar = new JButton("Cambiar Contraseña");
        btnCambiar.addActionListener(e -> procesarCambioPassword());
        
        // Listener para verificar coincidencia en tiempo real
        txtConfirmarPassword.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { verificarCoincidenciaPassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { verificarCoincidenciaPassword(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { verificarCoincidenciaPassword(); }
        });
        panelPrincipal.add(btnCambiar, gbc);
        
        // Información adicional
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblInfo = new JLabel("<html><div style='margin-top: 20px;'>" +
            "<strong>Requisitos para la nueva contraseña:</strong><br>" +
            "• Mínimo 6 caracteres<br>" +
            "• Se recomienda usar letras y números<br>" +
            "• Evite usar información personal<br>" +
            "</div></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        panelPrincipal.add(lblInfo, gbc);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private void procesarCambioPassword() {
        String passwordActual = new String(txtPasswordActual.getPassword()).trim();
        String passwordNueva = new String(txtPasswordNueva.getPassword()).trim();
        String confirmarPassword = new String(txtConfirmarPassword.getPassword()).trim();
        
        // Validaciones
        if (!ValidadorUI.validarTexto(this, passwordActual, "Contraseña actual", 60, true)) return;
        if (!ValidadorUI.validarTexto(this, passwordNueva, "Nueva contraseña", 60, true)) return;
        if (!ValidadorUI.validarTexto(this, confirmarPassword, "Confirmación de contraseña", 60, true)) return;
        
        // Validar longitud mínima
        if (passwordNueva.length() < 6) {
            JOptionPane.showMessageDialog(this, 
                "La nueva contraseña debe tener al menos 6 caracteres", 
                "Error", JOptionPane.ERROR_MESSAGE);
            txtPasswordNueva.requestFocus();
            return;
        }
        
        // Validar que las contraseñas coincidan
        if (confirmarPassword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Debe confirmar la nueva contraseña", 
                "Error de Validación", JOptionPane.ERROR_MESSAGE);
            txtConfirmarPassword.requestFocus();
            return;
        }
        
        if (!passwordNueva.equals(confirmarPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Las contraseñas nuevas no coinciden.\n\n" +
                "Verifique que ambas contraseñas sean exactamente iguales:\n" +
                "• No debe haber espacios adicionales\n" +
                "• Mayúsculas y minúsculas deben coincidir\n" +
                "• Todos los caracteres especiales deben ser idénticos\n\n" +
                "Vuelva a escribir la confirmación de contraseña.", 
                "Error de Confirmación", JOptionPane.ERROR_MESSAGE);
            txtConfirmarPassword.selectAll();
            txtConfirmarPassword.requestFocus();
            return;
        }
        
        // Simular cambio de contraseña (aquí se implementaría la lógica real)
        JOptionPane.showMessageDialog(this, 
            "Función de cambio de contraseña en desarrollo.\\n" +
            "Próximamente se implementará la actualización en base de datos.", 
            "En desarrollo", JOptionPane.INFORMATION_MESSAGE);
        
        // Limpiar campos
        limpiarCampos();
    }
    
    private void limpiarCampos() {
        txtPasswordActual.setText("");
        txtPasswordNueva.setText("");
        txtConfirmarPassword.setText("");
        txtPasswordActual.requestFocus();
    }
    
    private void verificarCoincidenciaPassword() {
        char[] nueva = txtPasswordNueva.getPassword();
        char[] confirmar = txtConfirmarPassword.getPassword();
        
        if (confirmar.length > 0) {
            boolean coinciden = java.util.Arrays.equals(nueva, confirmar);
            
            // Cambiar color de borde según coincidencia
            if (coinciden) {
                txtConfirmarPassword.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                txtConfirmarPassword.setToolTipText("✓ Las contraseñas coinciden");
            } else {
                txtConfirmarPassword.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                txtConfirmarPassword.setToolTipText("✗ Las contraseñas no coinciden");
            }
        } else {
            // Sin contenido - borde normal
            txtConfirmarPassword.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            txtConfirmarPassword.setToolTipText("Confirme la nueva contraseña");
        }
        
        // Limpiar arrays por seguridad
        java.util.Arrays.fill(nueva, ' ');
        java.util.Arrays.fill(confirmar, ' ');
    }
}