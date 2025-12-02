package ui.socios;

import dominio.SesionUsuario;
import dominio.Socio;
import servicios.ServicioSocio;
import seguridad.ControladorPermisos;
import utilidades.ValidadorUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.Arrays;

/**
 * Panel para que los socios cambien su contraseña
 * Requiere confirmar la contraseña actual y escribir la nueva dos veces
 */
public class PanelCambioPassword extends JPanel {
    
    private SesionUsuario sesion;
    private ServicioSocio servicioSocio;
    
    private JPasswordField txtPasswordActual;
    private JPasswordField txtNuevaPassword;
    private JPasswordField txtConfirmarPassword;
    private JButton btnCambiar;
    private JButton btnLimpiar;
    private JLabel lblFortaleza;
    private JProgressBar progressFortaleza;
    
    public PanelCambioPassword() {
        this.sesion = SesionUsuario.getInstance();
        this.servicioSocio = new ServicioSocio();
        
        if (!ControladorPermisos.puedeCambiarPassword()) {
            mostrarErrorAcceso();
            return;
        }
        
        configurarPanel();
        inicializarComponentes();
        configurarEventos();
    }
    
    private void mostrarErrorAcceso() {
        setLayout(new BorderLayout());
        JLabel lblError = new JLabel("No tiene permisos para cambiar contraseña.", 
                                    SwingConstants.CENTER);
        lblError.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblError.setForeground(Color.RED);
        add(lblError, BorderLayout.CENTER);
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Cambiar Contraseña"));
    }
    
    private void inicializarComponentes() {
        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblTitulo = new JLabel("Cambio de Contraseña", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(70, 130, 180));
        panelPrincipal.add(lblTitulo, gbc);
        
        // Información del usuario
        gbc.gridy = 1; gbc.gridwidth = 2; 
        JLabel lblUsuario = new JLabel("Usuario: " + sesion.getNombreUsuario() + 
                                      " (" + sesion.getDocumentoUsuario() + ")", 
                                      SwingConstants.CENTER);
        lblUsuario.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblUsuario.setForeground(Color.GRAY);
        panelPrincipal.add(lblUsuario, gbc);
        
        // Contraseña actual
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panelPrincipal.add(new JLabel("Contraseña actual:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPasswordActual = new JPasswordField(20);
        txtPasswordActual.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panelPrincipal.add(txtPasswordActual, gbc);
        
        // Nueva contraseña
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Nueva contraseña:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNuevaPassword = new JPasswordField(20);
        txtNuevaPassword.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panelPrincipal.add(txtNuevaPassword, gbc);
        
        // Confirmar nueva contraseña
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Confirmar contraseña:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtConfirmarPassword = new JPasswordField(20);
        txtConfirmarPassword.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panelPrincipal.add(txtConfirmarPassword, gbc);
        
        // Indicador de fortaleza
        gbc.gridx = 0; gbc.gridy = 5;
        panelPrincipal.add(new JLabel("Fortaleza:"), gbc);
        gbc.gridx = 1;
        JPanel panelFortaleza = new JPanel(new BorderLayout(5, 0));
        progressFortaleza = new JProgressBar(0, 100);
        progressFortaleza.setStringPainted(true);
        progressFortaleza.setPreferredSize(new Dimension(200, 20));
        lblFortaleza = new JLabel("Débil");
        lblFortaleza.setFont(new Font("SansSerif", Font.BOLD, 10));
        panelFortaleza.add(progressFortaleza, BorderLayout.CENTER);
        panelFortaleza.add(lblFortaleza, BorderLayout.EAST);
        panelPrincipal.add(panelFortaleza, gbc);
        
        // Botones
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnCambiar = new JButton("Cambiar Contraseña");
        btnCambiar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCambiar.setBackground(new Color(34, 139, 34));
        btnCambiar.setForeground(Color.WHITE);
        btnCambiar.setPreferredSize(new Dimension(160, 35));
        btnCambiar.setEnabled(false);
        
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnLimpiar.setPreferredSize(new Dimension(100, 35));
        
        panelBotones.add(btnCambiar);
        panelBotones.add(btnLimpiar);
        panelPrincipal.add(panelBotones, gbc);
        
        // Instrucciones
        gbc.gridy = 7; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel panelInstrucciones = crearPanelInstrucciones();
        panelPrincipal.add(panelInstrucciones, gbc);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelInstrucciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0)), 
            "Instrucciones de Seguridad", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            new Color(255, 140, 0)));
        
        JTextArea txtInstrucciones = new JTextArea(6, 40);
        txtInstrucciones.setEditable(false);
        txtInstrucciones.setFont(new Font("SansSerif", Font.PLAIN, 11));
        txtInstrucciones.setBackground(new Color(255, 255, 240));
        txtInstrucciones.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtInstrucciones.setLineWrap(true);
        txtInstrucciones.setWrapStyleWord(true);
        
        String instrucciones = 
            "🔐 REQUISITOS PARA UNA CONTRASEÑA SEGURA:\n\n" +
            "• Mínimo 8 caracteres\n" +
            "• Al menos una letra mayúscula (A-Z)\n" +
            "• Al menos una letra minúscula (a-z)\n" +
            "• Al menos un número (0-9)\n" +
            "• Al menos un carácter especial (!@#$%^&*)\n\n" +
            "⚠️  La nueva contraseña debe ser diferente a la actual\n" +
            "✅ Ambas contraseñas nuevas deben coincidir exactamente";
        
        txtInstrucciones.setText(instrucciones);
        panel.add(txtInstrucciones, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void configurarEventos() {
        // Evento para verificar fortaleza en tiempo real
        txtNuevaPassword.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { verificarFortaleza(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { verificarFortaleza(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { verificarFortaleza(); }
        });
        
        // Evento para verificar coincidencia
        txtConfirmarPassword.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { verificarCoincidencia(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { verificarCoincidencia(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { verificarCoincidencia(); }
        });
        
        // Botón cambiar
        btnCambiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarPassword();
            }
        });
        
        // Botón limpiar
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
    }
    
    private void verificarFortaleza() {
        char[] password = txtNuevaPassword.getPassword();
        String pass = new String(password);
        Arrays.fill(password, ' '); // Limpiar array por seguridad
        
        int fortaleza = calcularFortaleza(pass);
        
        progressFortaleza.setValue(fortaleza);
        
        if (fortaleza < 30) {
            progressFortaleza.setForeground(Color.RED);
            lblFortaleza.setText("Muy Débil");
            lblFortaleza.setForeground(Color.RED);
        } else if (fortaleza < 50) {
            progressFortaleza.setForeground(Color.ORANGE);
            lblFortaleza.setText("Débil");
            lblFortaleza.setForeground(Color.ORANGE);
        } else if (fortaleza < 75) {
            progressFortaleza.setForeground(Color.YELLOW);
            lblFortaleza.setText("Media");
            lblFortaleza.setForeground(new Color(200, 200, 0));
        } else if (fortaleza < 90) {
            progressFortaleza.setForeground(Color.GREEN);
            lblFortaleza.setText("Fuerte");
            lblFortaleza.setForeground(Color.GREEN);
        } else {
            progressFortaleza.setForeground(new Color(0, 150, 0));
            lblFortaleza.setText("Muy Fuerte");
            lblFortaleza.setForeground(new Color(0, 150, 0));
        }
        
        verificarHabilitarBoton();
    }
    
    private int calcularFortaleza(String password) {
        int puntos = 0;
        
        if (password.length() >= 8) puntos += 25;
        if (password.length() >= 12) puntos += 10;
        
        if (password.matches(".*[A-Z].*")) puntos += 20;
        if (password.matches(".*[a-z].*")) puntos += 20;
        if (password.matches(".*[0-9].*")) puntos += 20;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?].*")) puntos += 15;
        
        return Math.min(100, puntos);
    }
    
    private void verificarCoincidencia() {
        char[] nueva = txtNuevaPassword.getPassword();
        char[] confirmar = txtConfirmarPassword.getPassword();
        
        if (confirmar.length > 0) {
            boolean coinciden = Arrays.equals(nueva, confirmar);
            
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
        Arrays.fill(nueva, ' ');
        Arrays.fill(confirmar, ' ');
        
        verificarHabilitarBoton();
    }
    
    private void verificarHabilitarBoton() {
        char[] actual = txtPasswordActual.getPassword();
        char[] nueva = txtNuevaPassword.getPassword();
        char[] confirmar = txtConfirmarPassword.getPassword();
        
        boolean habilitar = actual.length > 0 && 
                           nueva.length >= 8 && 
                           Arrays.equals(nueva, confirmar) &&
                           calcularFortaleza(new String(nueva)) >= 50;
        
        btnCambiar.setEnabled(habilitar);
        
        // Limpiar arrays por seguridad
        Arrays.fill(actual, ' ');
        Arrays.fill(nueva, ' ');
        Arrays.fill(confirmar, ' ');
    }
    
    private void cambiarPassword() {
        try {
            // Obtener contraseñas
            char[] actualArray = txtPasswordActual.getPassword();
            char[] nuevaArray = txtNuevaPassword.getPassword();
            char[] confirmarArray = txtConfirmarPassword.getPassword();
            
            String actual = new String(actualArray);
            String nueva = new String(nuevaArray);
            String confirmar = new String(confirmarArray);
            
            // Limpiar arrays inmediatamente por seguridad
            Arrays.fill(actualArray, ' ');
            Arrays.fill(nuevaArray, ' ');
            Arrays.fill(confirmarArray, ' ');
            
            // Validaciones
            if (!validarDatos(actual, nueva, confirmar)) {
                return;
            }
            
            // Confirmar cambio
            if (!confirmarCambio()) {
                return;
            }
            
            // Verificar contraseña actual
            if (!verificarPasswordActual(actual)) {
                ValidadorUI.mostrarError(this, "La contraseña actual es incorrecta");
                txtPasswordActual.requestFocus();
                return;
            }
            
            // Cambiar contraseña
            if (servicioSocio.cambiarPassword(sesion.getDocumentoUsuario(), nueva)) {
                ValidadorUI.mostrarExito(this, "Contraseña cambiada exitosamente");
                limpiarCampos();
            } else {
                ValidadorUI.mostrarError(this, "Error al cambiar la contraseña");
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error inesperado: " + e.getMessage());
        }
    }
    
    private boolean validarDatos(String actual, String nueva, String confirmar) {
        if (actual.trim().isEmpty()) {
            ValidadorUI.mostrarError(this, "Debe ingresar su contraseña actual");
            txtPasswordActual.requestFocus();
            return false;
        }
        
        if (nueva.length() < 8) {
            ValidadorUI.mostrarError(this, "La nueva contraseña debe tener al menos 8 caracteres");
            txtNuevaPassword.requestFocus();
            return false;
        }
        
        // Validación mejorada de confirmación de contraseña
        if (confirmar.trim().isEmpty()) {
            ValidadorUI.mostrarError(this, "Debe confirmar la nueva contraseña");
            txtConfirmarPassword.requestFocus();
            return false;
        }
        
        if (!nueva.equals(confirmar)) {
            ValidadorUI.mostrarError(this, 
                "Las contraseñas nuevas no coinciden.\n\n" +
                "Verifique que ambas contraseñas sean exactamente iguales:\n" +
                "• No debe haber espacios adicionales\n" +
                "• Mayúsculas y minúsculas deben coincidir\n" +
                "• Todos los caracteres especiales deben ser idénticos");
            txtConfirmarPassword.selectAll();
            txtConfirmarPassword.requestFocus();
            return false;
        }
        
        if (actual.equals(nueva)) {
            ValidadorUI.mostrarError(this, "La nueva contraseña debe ser diferente a la actual");
            txtNuevaPassword.requestFocus();
            return false;
        }
        
        if (calcularFortaleza(nueva) < 50) {
            ValidadorUI.mostrarError(this, "La contraseña debe ser al menos de fortaleza 'Media'");
            txtNuevaPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean confirmarCambio() {
        String mensaje = "¿Está seguro de cambiar su contraseña?\n\n" +
                        "Usuario: " + sesion.getNombreUsuario() + "\n" +
                        "Documento: " + sesion.getDocumentoUsuario() + "\n\n" +
                        "Esta acción no se puede deshacer.";
        
        int opcion = JOptionPane.showConfirmDialog(
            this,
            mensaje,
            "Confirmar Cambio de Contraseña",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        return opcion == JOptionPane.YES_OPTION;
    }
    
    private boolean verificarPasswordActual(String passwordActual) {
        try {
            String documento = sesion.getDocumentoUsuario();
            return servicioSocio.verificarPassword(documento, passwordActual);
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }
    
    private void limpiarCampos() {
        txtPasswordActual.setText("");
        txtNuevaPassword.setText("");
        txtConfirmarPassword.setText("");
        progressFortaleza.setValue(0);
        lblFortaleza.setText("Débil");
        lblFortaleza.setForeground(Color.RED);
        btnCambiar.setEnabled(false);
        txtPasswordActual.requestFocus();
    }
}