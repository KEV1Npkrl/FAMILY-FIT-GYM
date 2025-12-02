package ui.seguridad;

import dominio.SesionUsuario;
import dominio.Empleado;
import dominio.Socio;
import servicios.ServicioEmpleado;
import servicios.ServicioSocio;
import seguridad.ControladorPermisos;
import utilidades.ValidadorUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel para que los Admins cambien contraseñas de otros usuarios
 */
public class PanelCambioPasswordAdmin extends JPanel {
    private JComboBox<String> cmbTipoUsuario;
    private JComboBox<Object> cmbUsuarios;
    private JPasswordField txtPasswordNueva;
    private JPasswordField txtConfirmarPassword;
    private JButton btnCambiar;
    private JButton btnCargarUsuarios;
    private JLabel lblUsuarioActual;
    
    private ServicioEmpleado servicioEmpleado;
    private ServicioSocio servicioSocio;
    
    public PanelCambioPasswordAdmin() {
        this.servicioEmpleado = new ServicioEmpleado();
        this.servicioSocio = new ServicioSocio();
        
        // Verificar permisos antes de construir UI
        if (!ControladorPermisos.esAdmin()) {
            construirUINoPermiso();
        } else {
            construirUI();
            cargarUsuarios();
        }
    }
    
    private void construirUINoPermiso() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        
        JLabel lblMensaje = new JLabel("<html><div style='text-align: center;'>" +
            "<h3>Acceso Denegado</h3>" +
            "Solo los administradores pueden cambiar contraseñas de otros usuarios.<br>" +
            "Puedes cambiar tu propia contraseña desde el menú Seguridad → Cambio de Contraseña." +
            "</div></html>");
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        
        panel.add(lblMensaje);
        add(panel, BorderLayout.CENTER);
    }
    
    private void construirUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        SesionUsuario sesion = SesionUsuario.getInstance();
        lblUsuarioActual = new JLabel("Cambiar contraseña de otros usuarios - Admin: " + sesion.getNombresCompletos());
        lblUsuarioActual.setFont(new Font("Arial", Font.BOLD, 14));
        lblUsuarioActual.setForeground(new Color(0, 100, 0));
        header.add(lblUsuarioActual);
        add(header, BorderLayout.NORTH);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Tipo de usuario
        gbc.gridx = 0; gbc.gridy = 0;
        panelPrincipal.add(new JLabel("Tipo de usuario:"), gbc);
        gbc.gridx = 1;
        cmbTipoUsuario = new JComboBox<>(new String[]{"Empleados", "Socios"});
        cmbTipoUsuario.addActionListener(e -> cargarUsuarios());
        panelPrincipal.add(cmbTipoUsuario, gbc);
        
        // Botón cargar usuarios
        gbc.gridx = 2;
        btnCargarUsuarios = new JButton("🔄 Actualizar");
        btnCargarUsuarios.addActionListener(e -> cargarUsuarios());
        panelPrincipal.add(btnCargarUsuarios, gbc);
        
        // Usuario seleccionado
        gbc.gridx = 0; gbc.gridy = 1;
        panelPrincipal.add(new JLabel("Seleccionar usuario:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbUsuarios = new JComboBox<>();
        cmbUsuarios.setPreferredSize(new Dimension(300, cmbUsuarios.getPreferredSize().height));
        panelPrincipal.add(cmbUsuarios, gbc);
        
        // Nueva contraseña
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panelPrincipal.add(new JLabel("Nueva contraseña:"), gbc);
        gbc.gridx = 1;
        txtPasswordNueva = new JPasswordField(20);
        panelPrincipal.add(txtPasswordNueva, gbc);
        
        // Confirmar contraseña
        gbc.gridx = 0; gbc.gridy = 3;
        panelPrincipal.add(new JLabel("Confirmar contraseña:"), gbc);
        gbc.gridx = 1;
        txtConfirmarPassword = new JPasswordField(20);
        panelPrincipal.add(txtConfirmarPassword, gbc);
        
        // Botón cambiar
        gbc.gridx = 1; gbc.gridy = 4;
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
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblInfo = new JLabel("<html><div style='margin-top: 20px;'>" +
            "<strong>⚠️ Función Administrativa:</strong><br>" +
            "Como administrador, puedes cambiar la contraseña de cualquier usuario.<br>" +
            "• Mínimo 6 caracteres para la nueva contraseña<br>" +
            "• Esta acción quedará registrada en los logs del sistema<br>" +
            "• Usa esta función responsablemente<br>" +
            "</div></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfo.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        panelPrincipal.add(lblInfo, gbc);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private void cargarUsuarios() {
        cmbUsuarios.removeAllItems();
        
        String tipoSeleccionado = (String) cmbTipoUsuario.getSelectedItem();
        
        try {
            if ("Empleados".equals(tipoSeleccionado)) {
                List<Empleado> empleados = servicioEmpleado.listar();
                String documentoActual = ControladorPermisos.getDocumentoUsuarioActual();
                
                for (Empleado empleado : empleados) {
                    // No permitir que el admin se cambie la contraseña a sí mismo desde aquí
                    if (!empleado.getNumDocumento().equals(documentoActual)) {
                        String item = empleado.getNumDocumento() + " - " + 
                                    empleado.getNombres() + " " + empleado.getApellidos() +
                                    " (" + empleado.getTipoEmpleado() + ")";
                        cmbUsuarios.addItem(item);
                    }
                }
            } else if ("Socios".equals(tipoSeleccionado)) {
                List<Socio> socios = servicioSocio.listar();
                for (Socio socio : socios) {
                    String item = socio.getNumDocumento() + " - " + 
                                socio.getNombres() + " " + socio.getApellidos();
                    cmbUsuarios.addItem(item);
                }
            }
            
            if (cmbUsuarios.getItemCount() == 0) {
                cmbUsuarios.addItem("No hay usuarios disponibles");
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar usuarios: " + e.getMessage());
        }
    }
    
    private void procesarCambioPassword() {
        String passwordNueva = new String(txtPasswordNueva.getPassword()).trim();
        String confirmarPassword = new String(txtConfirmarPassword.getPassword()).trim();
        Object usuarioSeleccionado = cmbUsuarios.getSelectedItem();
        
        // Validaciones
        if (usuarioSeleccionado == null || usuarioSeleccionado.toString().equals("No hay usuarios disponibles")) {
            ValidadorUI.mostrarError(this, "Debe seleccionar un usuario");
            return;
        }
        
        if (!ValidadorUI.validarTexto(this, passwordNueva, "Nueva contraseña", 60, true)) return;
        if (!ValidadorUI.validarTexto(this, confirmarPassword, "Confirmación de contraseña", 60, true)) return;
        
        // Validar longitud mínima
        if (passwordNueva.length() < 6) {
            ValidadorUI.mostrarError(this, "La nueva contraseña debe tener al menos 6 caracteres");
            txtPasswordNueva.requestFocus();
            return;
        }
        
        // Validar que las contraseñas coincidan
        if (!passwordNueva.equals(confirmarPassword)) {
            ValidadorUI.mostrarError(this, "Las contraseñas nuevas no coinciden");
            txtConfirmarPassword.selectAll();
            txtConfirmarPassword.requestFocus();
            return;
        }
        
        // Extraer documento del usuario seleccionado
        String textoUsuario = usuarioSeleccionado.toString();
        String documento = textoUsuario.substring(0, textoUsuario.indexOf(" - "));
        
        // Confirmar acción
        int resultado = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cambiar la contraseña del usuario:\\n" +
            textoUsuario + "?\\n\\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar Cambio de Contraseña",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (resultado != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Simular cambio de contraseña (aquí se implementaría la lógica real)
        JOptionPane.showMessageDialog(this, 
            "Función de cambio de contraseña por administrador en desarrollo.\\n" +
            "Usuario: " + textoUsuario + "\\n" +
            "Próximamente se implementará la actualización en base de datos.", 
            "En desarrollo", JOptionPane.INFORMATION_MESSAGE);
        
        // Limpiar campos
        limpiarCampos();
    }
    
    private void limpiarCampos() {
        txtPasswordNueva.setText("");
        txtConfirmarPassword.setText("");
        txtPasswordNueva.requestFocus();
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