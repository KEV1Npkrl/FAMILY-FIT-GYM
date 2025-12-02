package ui.splash;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Pantalla de bienvenida (Splash Screen) que se muestra al iniciar la aplicación
 * Implementa multithreading para cargar recursos en segundo plano
 */
public class SplashScreen extends JWindow {
    
    private JProgressBar progressBar;
    private JLabel lblEstado;
    private JLabel lblVersion;
    private JLabel lblTitulo;
    
    // Componentes para el diseño
    private static final int ANCHO = 500;
    private static final int ALTO = 350;
    private static final Color COLOR_PRINCIPAL = new Color(46, 125, 50);
    private static final Color COLOR_SECUNDARIO = new Color(81, 200, 87);
    
    public SplashScreen() {
        construirUI();
        centrarVentana();
        setVisible(true);
    }
    
    private void construirUI() {
        setSize(ANCHO, ALTO);
        setBackground(Color.WHITE);
        
        // Panel principal con diseño personalizado
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradiente de fondo
                GradientPaint gradient = new GradientPaint(
                    0, 0, COLOR_PRINCIPAL,
                    getWidth(), getHeight(), COLOR_SECUNDARIO
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Borde redondeado
                g2d.setColor(COLOR_PRINCIPAL.darker());
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
            }
        };
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setOpaque(false);
        
        // Panel superior con título y logo
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setOpaque(false);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Título principal
        lblTitulo = new JLabel("FAMILY FIT GYM");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);
        panelSuperior.add(lblTitulo, gbc);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Sistema de Gestión Integral");
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 16));
        lblSubtitulo.setForeground(new Color(240, 240, 240));
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        panelSuperior.add(lblSubtitulo, gbc);
        
        // Icono representativo (usando texto como placeholder)
        JLabel lblIcono = new JLabel("🏋️");
        lblIcono.setFont(new Font("Arial", Font.PLAIN, 48));
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        panelSuperior.add(lblIcono, gbc);
        
        panelPrincipal.add(panelSuperior, BorderLayout.CENTER);
        
        // Panel inferior con progreso y estado
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        
        // Barra de progreso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Iniciando sistema...");
        progressBar.setFont(new Font("Arial", Font.BOLD, 12));
        progressBar.setForeground(COLOR_PRINCIPAL);
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(400, 25));
        
        // Estado actual
        lblEstado = new JLabel("Cargando componentes...");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEstado.setForeground(Color.WHITE);
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Versión
        lblVersion = new JLabel("Versión 1.0 - Desarrollado con Java Swing");
        lblVersion.setFont(new Font("Arial", Font.PLAIN, 10));
        lblVersion.setForeground(new Color(220, 220, 220));
        lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelInferior.add(progressBar, BorderLayout.CENTER);
        panelInferior.add(lblEstado, BorderLayout.NORTH);
        panelInferior.add(lblVersion, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        add(panelPrincipal);
        
        // Hacer que la ventana sea transparente con bordes redondeados
        setShape(new RoundRectangle2D.Double(0, 0, ANCHO, ALTO, 20, 20));
    }
    
    private void centrarVentana() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - ANCHO) / 2;
        int y = (screenSize.height - ALTO) / 2;
        setLocation(x, y);
    }
    
    /**
     * Simula la carga de la aplicación con actualizaciones de progreso
     * Ejecuta en un hilo separado para no bloquear el EDT
     */
    public void iniciarCarga(Runnable onComplete) {
        // Crear un SwingWorker para ejecutar la carga en segundo plano
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulación de carga de componentes
                String[] pasos = {
                    "Inicializando base de datos...",
                    "Cargando configuración...", 
                    "Verificando conexión...",
                    "Cargando servicios...",
                    "Preparando interfaz...",
                    "Aplicando tema visual...",
                    "Configurando seguridad...",
                    "Finalizando carga..."
                };
                
                for (int i = 0; i < pasos.length; i++) {
                    // Publicar estado actual
                    publish(pasos[i]);
                    
                    // Simular tiempo de carga (entre 200-500ms por paso)
                    Thread.sleep(300 + (int)(Math.random() * 300));
                    
                    // Actualizar progreso
                    final int progreso = ((i + 1) * 100) / pasos.length;
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progreso);
                        progressBar.setString(progreso + "%");
                    });
                }
                
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                // Actualizar estado en el EDT
                for (String estado : chunks) {
                    lblEstado.setText(estado);
                }
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Verificar que no hubo excepciones
                    
                    // Mostrar "Completado" por un momento
                    lblEstado.setText("¡Sistema cargado exitosamente!");
                    progressBar.setValue(100);
                    progressBar.setString("100% - Completado");
                    
                    // Esperar un poco antes de cerrar
                    Timer timer = new Timer(800, e -> {
                        setVisible(false);
                        dispose();
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    lblEstado.setText("Error al cargar el sistema");
                    progressBar.setString("Error");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Método estático para mostrar el splash screen fácilmente
     */
    public static void mostrar(Runnable onComplete) {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.iniciarCarga(onComplete);
        });
    }
}