package ui.autenticacion;

import servicios.ServicioAutenticacion;
import utilidades.FiltrosEntrada;
import ui.VentanaPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
    private boolean esFullscreenMaximizado = false; // Estado del fullscreen
    private ServicioAutenticacion servicioAuth;
    
    public PantallaLogin() {
        this.servicioAuth = new ServicioAutenticacion();
        construirUI();
    }
    
    private void construirUI() {
        setTitle("Family Fit Gym - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Configurar ventana fullscreen windowed por defecto
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        
        // Obtener dimensiones de pantalla pero mantener decoraciones
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setLocationRelativeTo(null);
        
        // Layout principal que ocupe toda la pantalla
        setLayout(new BorderLayout());
        
        // Panel principal que se escale a toda la ventana
        JPanel panelPrincipal = crearPanelPrincipal();
        add(panelPrincipal, BorderLayout.CENTER);
        
        // Aplicar tema visual
        aplicarTema();
        
        // Agregar soporte para teclas
        agregarManejadorTeclado();
    }
    
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        // Sin tamaño fijo para que ocupe toda la ventana
        panel.setBackground(Color.WHITE);
        
        // Panel izquierdo para imagen
        JPanel panelImagen = crearPanelImagen();
        panel.add(panelImagen, BorderLayout.CENTER);
        
        // Panel derecho para login
        JPanel panelLogin = crearPanelLogin();
        panel.add(panelLogin, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelImagen() {
        JPanel panel = new JPanel(new BorderLayout());
        // Sin tamaño fijo para escalarse según la ventana
        panel.setBackground(new Color(45, 45, 45));
        
        // Etiqueta para la imagen
        lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setVerticalAlignment(SwingConstants.CENTER);
        
        panel.add(lblImagen, BorderLayout.CENTER);
        
        // Cargar la imagen inicial (Socio por defecto)
        actualizarImagenLateral();
        
        // Footer con información y modo
        JLabel lblFooter = new JLabel("<html><div style='text-align: center; color: #888;'>" +
                                    "<small style='font-size: 14px;'>Sistema de Gestión de Gimnasio v1.0</small><br>" +
                                    "<small style='font-size: 12px;'>Presiona F11 para fullscreen | ESC para salir</small>" +
                                    "</div></html>");
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(lblFooter, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Actualiza la imagen mostrada en el panel lateral según el tipo de usuario.
     */
    private void actualizarImagenLateral() {
        String nombreImagen = esLoginSocio ? "login_usuarios.jpeg" : "login_empleados.jpg";
        
        try {
            // Buscar imagen en carpeta recursos
            File archivoImagen = new File("recursos/" + nombreImagen);
            
            if (archivoImagen.exists()) {
                ImageIcon icon = new ImageIcon(archivoImagen.getAbsolutePath());
                // Redimensionar imagen proporcionalmente según tamaño disponible (aprox 600x400 base)
                // Nota: Para un ajuste perfecto dinámico se requeriría un listener de redimensionamiento,
                // pero esto mantiene la lógica original simple.
                Image img = icon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
                lblImagen.setIcon(new ImageIcon(img));
                lblImagen.setText(""); // Limpiar texto placeholder si existe
            } else {
                // Placeholder si no encuentra la imagen específica
                lblImagen.setIcon(null);
                lblImagen.setText("<html><div style='text-align: center;'>" +
                                "<h1 style='color: white; font-size: 5vw; margin: 0;'>💪</h1>" +
                                "<h2 style='color: white; font-size: 3vw; margin: 10px 0;'>FAMILY FIT GYM</h2>" +
                                "<p style='color: #ccc; font-size: 1.2vw; margin: 5px 0;'>" + nombreImagen + " no encontrada</p>" +
                                "</div></html>");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }
    }
    
    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // Ancho fijo pero altura escalable
        panel.setPreferredSize(new Dimension(450, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));
        
        // Agregar espacio flexible arriba para centrado vertical
        panel.add(Box.createVerticalGlue());
        
        // Header del login
        lblTipoLogin = new JLabel("ACCESO SOCIOS");
        lblTipoLogin.setFont(new Font("Arial", Font.BOLD, 32));
        lblTipoLogin.setForeground(new Color(51, 51, 51));
        lblTipoLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTipoLogin);
        
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Campo documento
        JLabel lblDocumento = new JLabel("Documento:");
        lblDocumento.setFont(new Font("Arial", Font.PLAIN, 18));
        lblDocumento.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblDocumento);
        
        txtDocumento = FiltrosEntrada.crearCampoDocumento();
        txtDocumento.setMaximumSize(new Dimension(350, 45));
        txtDocumento.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(txtDocumento);
        
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Campo contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 18));
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblPassword);
        
        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(350, 45));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(txtPassword);
        
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Botón de login
        btnLogin = new JButton("INGRESAR");
        btnLogin.setMaximumSize(new Dimension(350, 55));
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setBackground(new Color(76, 175, 80));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(this::procesarLogin);
        panel.add(btnLogin);
        
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Botón alternar tipo
        btnAlternarTipo = new JButton("Acceso Empleados");
        btnAlternarTipo.setMaximumSize(new Dimension(350, 45));
        btnAlternarTipo.setFont(new Font("Arial", Font.PLAIN, 15));
        btnAlternarTipo.setBackground(new Color(158, 158, 158));
        btnAlternarTipo.setForeground(Color.WHITE);
        btnAlternarTipo.setFocusPainted(false);
        btnAlternarTipo.setBorderPainted(false);
        btnAlternarTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAlternarTipo.addActionListener(this::alternarTipoLogin);
        panel.add(btnAlternarTipo);
        
        // Botón para salir
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        JButton btnSalir = new JButton("Salir");
        btnSalir.setMaximumSize(new Dimension(120, 35));
        btnSalir.setFont(new Font("Arial", Font.PLAIN, 14));
        btnSalir.setBackground(new Color(244, 67, 54));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.addActionListener(e -> confirmarSalida());
        panel.add(btnSalir);
        
        // Agregar espacio flexible abajo para centrado vertical
        panel.add(Box.createVerticalGlue());
        
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
    
    private void agregarManejadorTeclado() {
        // Manejar tecla ESC para salir
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarSalida();
            }
        });
        
        // Manejar tecla F11 para alternar fullscreen
        KeyStroke f11KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0, false);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f11KeyStroke, "F11");
        getRootPane().getActionMap().put("F11", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alternarModoFullscreen();
            }
        });
    }
    
    private void alternarModoFullscreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        if (!esFullscreenMaximizado) {
            // Cambiar a fullscreen maximizado (sin decoraciones)
            if (gd.isFullScreenSupported()) {
                dispose(); // Cerrar ventana actual
                setUndecorated(true); // Quitar decoraciones
                setVisible(true); // Mostrar nuevamente
                gd.setFullScreenWindow(this); // Activar fullscreen real
                esFullscreenMaximizado = true;
            }
        } else {
            // Volver a modo windowed
            gd.setFullScreenWindow(null); // Salir de fullscreen
            dispose(); // Cerrar ventana actual
            setUndecorated(false); // Restaurar decoraciones
            setVisible(true); // Mostrar nuevamente
            setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizar en modo ventana
            esFullscreenMaximizado = false;
        }
    }
    
    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Desea cerrar la aplicación?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void alternarTipoLogin(ActionEvent e) {
        esLoginSocio = !esLoginSocio;
        
        // Actualizar la imagen según el nuevo tipo de login
        actualizarImagenLateral();
        
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