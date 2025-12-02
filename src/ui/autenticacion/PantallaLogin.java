package ui.autenticacion;

import servicios.ServicioAutenticacion;
import dominio.SesionUsuario;
import utilidades.FiltrosEntrada;
import ui.VentanaPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Pantalla de login principal del sistema
 */
public class PantallaLogin extends JFrame {
    private JTextField txtDocumento;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnAlternarTipo;
    private JLabel lblTipoLogin;
    private JLabel lblImagen;
    
    private boolean esLoginSocio = true; // true = Socio, false = Empleado
    private ServicioAutenticacion servicioAuth;
    
    public PantallaLogin() {
        this.servicioAuth = new ServicioAutenticacion();
        construirUI();
    }
    
    private void construirUI() {
        setTitle("Family Fit Gym - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Layout principal
        setLayout(new BorderLayout());
        
        // Panel izquierdo para imagen
        JPanel panelImagen = crearPanelImagen();
        add(panelImagen, BorderLayout.CENTER);
        
        // Panel derecho para login
        JPanel panelLogin = crearPanelLogin();
        add(panelLogin, BorderLayout.EAST);
        
        // Aplicar tema visual
        aplicarTema();
    }
    
    private JPanel crearPanelImagen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 500));
        panel.setBackground(new Color(45, 45, 45));
        
        // Intentar cargar imagen del gimnasio
        lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setVerticalAlignment(SwingConstants.CENTER);
        
        try {
            // Buscar imagen en carpeta recursos
            File archivoImagen = new File("recursos/gym-logo.jpg");
            if (archivoImagen.exists()) {
                ImageIcon icon = new ImageIcon(archivoImagen.getAbsolutePath());
                // Redimensionar imagen
                Image img = icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                lblImagen.setIcon(new ImageIcon(img));
            } else {
                // Placeholder si no hay imagen
                lblImagen.setText("<html><div style='text-align: center;'>" +
                                "<h1 style='color: white; font-size: 48px;'>💪</h1>" +
                                "<h2 style='color: white;'>FAMILY FIT GYM</h2>" +
                                "<p style='color: #ccc;'>Tu gimnasio de confianza</p>" +
                                "</div></html>");
            }
        } catch (Exception e) {
            lblImagen.setText("<html><div style='text-align: center;'>" +
                            "<h1 style='color: white; font-size: 48px;'>💪</h1>" +
                            "<h2 style='color: white;'>FAMILY FIT GYM</h2>" +
                            "</div></html>");
        }
        
        panel.add(lblImagen, BorderLayout.CENTER);
        
        // Footer con información
        JLabel lblFooter = new JLabel("<html><div style='text-align: center; color: #888;'>" +
                                    "<small>Sistema de Gestión de Gimnasio v1.0</small></div></html>");
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblFooter, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300, 500));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));
        
        // Header del login
        lblTipoLogin = new JLabel("ACCESO SOCIOS");
        lblTipoLogin.setFont(new Font("Arial", Font.BOLD, 20));
        lblTipoLogin.setForeground(new Color(51, 51, 51));
        lblTipoLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTipoLogin);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Campo documento
        JLabel lblDocumento = new JLabel("Documento:");
        lblDocumento.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDocumento.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblDocumento);
        
        txtDocumento = FiltrosEntrada.crearCampoDocumento();
        txtDocumento.setMaximumSize(new Dimension(240, 30));
        txtDocumento.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtDocumento);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Campo contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblPassword);
        
        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(240, 30));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtPassword);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Botón de login
        btnLogin = new JButton("INGRESAR");
        btnLogin.setMaximumSize(new Dimension(240, 40));
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(76, 175, 80));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(this::procesarLogin);
        panel.add(btnLogin);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Botón alternar tipo
        btnAlternarTipo = new JButton("Acceso Empleados");
        btnAlternarTipo.setMaximumSize(new Dimension(240, 30));
        btnAlternarTipo.setFont(new Font("Arial", Font.PLAIN, 12));
        btnAlternarTipo.setBackground(new Color(158, 158, 158));
        btnAlternarTipo.setForeground(Color.WHITE);
        btnAlternarTipo.setFocusPainted(false);
        btnAlternarTipo.setBorderPainted(false);
        btnAlternarTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAlternarTipo.addActionListener(this::alternarTipoLogin);
        panel.add(btnAlternarTipo);
        
        // Agregar enter key listener para login
        txtPassword.addActionListener(this::procesarLogin);
        
        return panel;
    }
    
    private void aplicarTema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, usar look and feel por defecto
        }
    }
    
    private void alternarTipoLogin(ActionEvent e) {
        esLoginSocio = !esLoginSocio;
        
        if (esLoginSocio) {
            lblTipoLogin.setText("ACCESO SOCIOS");
            btnAlternarTipo.setText("Acceso Empleados");
            btnLogin.setBackground(new Color(76, 175, 80)); // Verde
        } else {
            lblTipoLogin.setText("ACCESO EMPLEADOS");
            btnAlternarTipo.setText("Acceso Socios");
            btnLogin.setBackground(new Color(33, 150, 243)); // Azul
        }
        
        // Limpiar campos
        txtDocumento.setText("");
        txtPassword.setText("");
        txtDocumento.requestFocus();
    }
    
    private void procesarLogin(ActionEvent e) {
        String documento = txtDocumento.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        
        // Validaciones básicas
        if (documento.isEmpty()) {
            mostrarError("Ingrese su número de documento");
            txtDocumento.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            mostrarError("Ingrese su contraseña");
            txtPassword.requestFocus();
            return;
        }
        
        // Mostrar indicador de carga
        btnLogin.setText("Verificando...");
        btnLogin.setEnabled(false);
        
        // Realizar autenticación en hilo separado
        SwingUtilities.invokeLater(() -> {
            try {
                boolean autenticado = false;
                
                if (esLoginSocio) {
                    autenticado = servicioAuth.autenticarSocio(documento, password);
                } else {
                    autenticado = servicioAuth.autenticarEmpleado(documento, password);
                }
                
                if (autenticado) {
                    // Login exitoso
                    SwingUtilities.invokeLater(() -> {
                        mostrarExito("¡Bienvenido!");
                        abrirVentanaPrincipal();
                    });
                } else {
                    // Login fallido
                    SwingUtilities.invokeLater(() -> {
                        mostrarError("Documento o contraseña incorrectos");
                        restaurarBotonLogin();
                        limpiarCampos();
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    mostrarError("Error de conexión. Intente nuevamente.");
                    restaurarBotonLogin();
                });
            }
        });
    }
    
    private void restaurarBotonLogin() {
        btnLogin.setText("INGRESAR");
        btnLogin.setEnabled(true);
    }
    
    private void limpiarCampos() {
        txtPassword.setText("");
        txtDocumento.requestFocus();
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void abrirVentanaPrincipal() {
        // Crear y mostrar ventana principal
        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
        ventanaPrincipal.setVisible(true);
        
        // Cerrar ventana de login
        this.dispose();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PantallaLogin().setVisible(true);
        });
    }
}