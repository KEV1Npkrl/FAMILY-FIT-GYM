package ui.reportes;

import dominio.SesionUsuario;
import servicios.ServicioReportes;
import utilidades.ValidadorUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel principal de reportes con acceso a diferentes tipos de reportes
 */
public class PanelReportes extends JPanel {
    
    private ServicioReportes servicioReportes;
    private JTabbedPane tabbedPane;
    
    public PanelReportes() {
        this.servicioReportes = new ServicioReportes();
        configurarPanel();
        inicializarComponentes();
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Módulo de Reportes"));
    }
    
    private void inicializarComponentes() {
        // Panel de encabezado
        JPanel panelHeader = crearPanelHeader();
        add(panelHeader, BorderLayout.NORTH);
        
        // Panel principal con pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Pestaña de Reportes de Socios
        ReporteSociosPanel reporteSocios = new ReporteSociosPanel();
        tabbedPane.addTab("📊 Reportes de Socios", reporteSocios);
        
        // Pestaña de Reportes de Ingresos
        ReporteIngresosPanel reporteIngresos = new ReporteIngresosPanel();
        tabbedPane.addTab("💰 Reportes de Ingresos", reporteIngresos);
        
        // Pestaña de Reportes de Asistencias
        ReporteAsistenciasPanel reporteAsistencias = new ReporteAsistenciasPanel();
        tabbedPane.addTab("📈 Reportes de Asistencias", reporteAsistencias);
        
        // Pestaña de Reportes de Membresías
        ReporteMembresiasPanel reporteMembresias = new ReporteMembresiasPanel();
        tabbedPane.addTab("🎫 Reportes de Membresías", reporteMembresias);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel de pie
        JPanel panelFooter = crearPanelFooter();
        add(panelFooter, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("Sistema de Reportes - Family Fit Gym");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo, BorderLayout.CENTER);
        
        JLabel lblUsuario = new JLabel("Usuario: " + SesionUsuario.getInstance().getNombreUsuario());
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblUsuario, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelFooter() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnActualizar = new JButton("🔄 Actualizar Datos");
        btnActualizar.addActionListener(e -> actualizarTodosLosReportes());
        panel.add(btnActualizar);
        
        JButton btnCerrar = new JButton("❌ Cerrar");
        btnCerrar.addActionListener(e -> {
            Window ventana = SwingUtilities.getWindowAncestor(this);
            if (ventana != null) {
                ventana.dispose();
            }
        });
        panel.add(btnCerrar);
        
        return panel;
    }
    
    private void actualizarTodosLosReportes() {
        try {
            // Actualizar cada panel de reporte
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component componente = tabbedPane.getComponentAt(i);
                if (componente instanceof ReportePanel) {
                    ((ReportePanel) componente).actualizarDatos();
                }
            }
            
            ValidadorUI.mostrarExito(this, "Todos los reportes han sido actualizados exitosamente");
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al actualizar reportes: " + e.getMessage());
        }
    }
    
    /**
     * Interface común para todos los paneles de reportes
     */
    public interface ReportePanel {
        void actualizarDatos();
        void exportarReporte();
        void limpiarFiltros();
    }
}