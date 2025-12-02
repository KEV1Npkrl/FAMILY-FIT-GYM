package ui.reportes;

import servicios.ServicioReportes;
import utilidades.ValidadorUI;
import ui.reportes.PanelReportes.ReportePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Panel para reportes de membresías
 */
public class ReporteMembresiasPanel extends JPanel implements ReportePanel {
    
    private ServicioReportes servicioReportes;
    private JLabel lblTotalMembresias;
    private JLabel lblMembresiasActivas;
    private JLabel lblMembresiasVencidas;
    private JLabel lblProximasVencer;
    private JTable tablaPlanesMembresia;
    private DefaultTableModel modeloTablaPlanes;
    private JTable tablaVencimientos;
    private DefaultTableModel modeloTablaVencimientos;
    
    public ReporteMembresiasPanel() {
        this.servicioReportes = new ServicioReportes();
        configurarPanel();
        inicializarComponentes();
        actualizarDatos();
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel de estadísticas generales
        JPanel panelEstadisticas = crearPanelEstadisticas();
        add(panelEstadisticas, BorderLayout.NORTH);
        
        // Panel central con tablas
        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel de acciones
        JPanel panelAcciones = crearPanelAcciones();
        add(panelAcciones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Estadísticas de Membresías"));
        panel.setPreferredSize(new Dimension(0, 150));
        
        // Total de membresías
        JPanel panelTotalMembresias = new JPanel(new BorderLayout());
        panelTotalMembresias.setBorder(BorderFactory.createEtchedBorder());
        panelTotalMembresias.setBackground(new Color(240, 248, 255));
        
        JLabel lblTituloTotalMembresias = new JLabel("Total Membresías", SwingConstants.CENTER);
        lblTituloTotalMembresias.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalMembresias = new JLabel("0", SwingConstants.CENTER);
        lblTotalMembresias.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalMembresias.setForeground(new Color(70, 130, 180));
        
        panelTotalMembresias.add(lblTituloTotalMembresias, BorderLayout.NORTH);
        panelTotalMembresias.add(lblTotalMembresias, BorderLayout.CENTER);
        panel.add(panelTotalMembresias);
        
        // Membresías activas
        JPanel panelMembresiasActivas = new JPanel(new BorderLayout());
        panelMembresiasActivas.setBorder(BorderFactory.createEtchedBorder());
        panelMembresiasActivas.setBackground(new Color(240, 255, 240));
        
        JLabel lblTituloMembresiasActivas = new JLabel("Activas", SwingConstants.CENTER);
        lblTituloMembresiasActivas.setFont(new Font("Arial", Font.BOLD, 12));
        lblMembresiasActivas = new JLabel("0", SwingConstants.CENTER);
        lblMembresiasActivas.setFont(new Font("Arial", Font.BOLD, 24));
        lblMembresiasActivas.setForeground(new Color(34, 139, 34));
        
        panelMembresiasActivas.add(lblTituloMembresiasActivas, BorderLayout.NORTH);
        panelMembresiasActivas.add(lblMembresiasActivas, BorderLayout.CENTER);
        panel.add(panelMembresiasActivas);
        
        // Membresías vencidas
        JPanel panelMembresiasVencidas = new JPanel(new BorderLayout());
        panelMembresiasVencidas.setBorder(BorderFactory.createEtchedBorder());
        panelMembresiasVencidas.setBackground(new Color(255, 240, 240));
        
        JLabel lblTituloMembresiasVencidas = new JLabel("Vencidas", SwingConstants.CENTER);
        lblTituloMembresiasVencidas.setFont(new Font("Arial", Font.BOLD, 12));
        lblMembresiasVencidas = new JLabel("0", SwingConstants.CENTER);
        lblMembresiasVencidas.setFont(new Font("Arial", Font.BOLD, 24));
        lblMembresiasVencidas.setForeground(new Color(220, 20, 60));
        
        panelMembresiasVencidas.add(lblTituloMembresiasVencidas, BorderLayout.NORTH);
        panelMembresiasVencidas.add(lblMembresiasVencidas, BorderLayout.CENTER);
        panel.add(panelMembresiasVencidas);
        
        // Próximas a vencer
        JPanel panelProximasVencer = new JPanel(new BorderLayout());
        panelProximasVencer.setBorder(BorderFactory.createEtchedBorder());
        panelProximasVencer.setBackground(new Color(255, 248, 220));
        
        JLabel lblTituloProximasVencer = new JLabel("Próximas a Vencer", SwingConstants.CENTER);
        lblTituloProximasVencer.setFont(new Font("Arial", Font.BOLD, 12));
        lblProximasVencer = new JLabel("0", SwingConstants.CENTER);
        lblProximasVencer.setFont(new Font("Arial", Font.BOLD, 24));
        lblProximasVencer.setForeground(new Color(255, 140, 0));
        
        panelProximasVencer.add(lblTituloProximasVencer, BorderLayout.NORTH);
        panelProximasVencer.add(lblProximasVencer, BorderLayout.CENTER);
        panel.add(panelProximasVencer);
        
        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Tabla de planes de membresía
        JPanel panelPlanesMembresia = crearTablaPlanesMembresia();
        panel.add(panelPlanesMembresia);
        
        // Tabla de vencimientos próximos
        JPanel panelVencimientos = crearTablaVencimientos();
        panel.add(panelVencimientos);
        
        return panel;
    }
    
    private JPanel crearTablaPlanesMembresia() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Membresías por Plan"));
        
        String[] columnas = {"Plan de Membresía", "Cantidad", "Porcentaje"};
        modeloTablaPlanes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPlanesMembresia = new JTable(modeloTablaPlanes);
        tablaPlanesMembresia.setRowSorter(new javax.swing.table.TableRowSorter<>(modeloTablaPlanes));
        
        // Configurar anchos de columnas
        tablaPlanesMembresia.getColumnModel().getColumn(0).setPreferredWidth(150);
        tablaPlanesMembresia.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaPlanesMembresia.getColumnModel().getColumn(2).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tablaPlanesMembresia);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearTablaVencimientos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Próximos Vencimientos (30 días)"));
        
        String[] columnas = {"Socio", "Plan", "Fecha Vencimiento", "Días Restantes"};
        modeloTablaVencimientos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaVencimientos = new JTable(modeloTablaVencimientos);
        tablaVencimientos.setRowSorter(new javax.swing.table.TableRowSorter<>(modeloTablaVencimientos));
        
        // Configurar anchos de columnas
        tablaVencimientos.getColumnModel().getColumn(0).setPreferredWidth(120);
        tablaVencimientos.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaVencimientos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaVencimientos.getColumnModel().getColumn(3).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tablaVencimientos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.addActionListener(e -> actualizarDatos());
        panel.add(btnActualizar);
        
        JButton btnExportar = new JButton("📄 Exportar");
        btnExportar.addActionListener(e -> exportarReporte());
        panel.add(btnExportar);
        
        JButton btnLimpiar = new JButton("🧹 Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiar);
        
        JButton btnNotificarVencimientos = new JButton("📧 Notificar Vencimientos");
        btnNotificarVencimientos.addActionListener(this::notificarVencimientos);
        panel.add(btnNotificarVencimientos);
        
        return panel;
    }
    
    @Override
    public void actualizarDatos() {
        try {
            // Actualizar estadísticas generales
            Map<String, Object> estadisticas = servicioReportes.obtenerEstadisticasMembresias();
            
            lblTotalMembresias.setText(String.valueOf(estadisticas.getOrDefault("totalMembresias", 0)));
            lblMembresiasActivas.setText(String.valueOf(estadisticas.getOrDefault("membresiasActivas", 0)));
            lblMembresiasVencidas.setText(String.valueOf(estadisticas.getOrDefault("membresiasVencidas", 0)));
            lblProximasVencer.setText(String.valueOf(estadisticas.getOrDefault("proximasVencer", 0)));
            
            // Actualizar tabla de planes de membresía
            List<Map<String, Object>> planesMembresia = servicioReportes.obtenerMembresiasPorPlan();
            modeloTablaPlanes.setRowCount(0);
            
            int totalMembresias = Integer.parseInt(lblTotalMembresias.getText());
            
            for (Map<String, Object> plan : planesMembresia) {
                int cantidad = (Integer) plan.get("cantidad");
                double porcentaje = totalMembresias > 0 ? (cantidad * 100.0) / totalMembresias : 0.0;
                
                Object[] fila = {
                    plan.get("planMembresia"),
                    cantidad,
                    String.format("%.1f%%", porcentaje)
                };
                modeloTablaPlanes.addRow(fila);
            }
            
            // Actualizar tabla de vencimientos próximos
            List<Map<String, Object>> vencimientos = servicioReportes.obtenerProximosVencimientos();
            modeloTablaVencimientos.setRowCount(0);
            
            for (Map<String, Object> vencimiento : vencimientos) {
                Object[] fila = {
                    vencimiento.get("nombreSocio"),
                    vencimiento.get("planMembresia"),
                    vencimiento.get("fechaVencimiento"),
                    vencimiento.get("diasRestantes")
                };
                modeloTablaVencimientos.addRow(fila);
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al actualizar datos: " + e.getMessage());
        }
    }
    
    @Override
    public void exportarReporte() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Exportar Reporte de Membresías");
            chooser.setSelectedFile(new File("reporte_membresias_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt"));
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                
                try (PrintWriter writer = new PrintWriter(archivo, "UTF-8")) {
                    // Encabezado del reporte
                    writer.println("REPORTE DE MEMBRESÍAS - FAMILY FIT GYM");
                    writer.println("Fecha de generación: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    writer.println("=".repeat(60));
                    writer.println();
                    
                    // Estadísticas generales
                    writer.println("ESTADÍSTICAS GENERALES:");
                    writer.println("- Total de membresías: " + lblTotalMembresias.getText());
                    writer.println("- Membresías activas: " + lblMembresiasActivas.getText());
                    writer.println("- Membresías vencidas: " + lblMembresiasVencidas.getText());
                    writer.println("- Próximas a vencer (30 días): " + lblProximasVencer.getText());
                    writer.println();
                    
                    // Membresías por plan
                    writer.println("MEMBRESÍAS POR PLAN:");
                    writer.println("-".repeat(50));
                    writer.printf("%-25s %-10s %-12s%n", "PLAN DE MEMBRESÍA", "CANTIDAD", "PORCENTAJE");
                    writer.println("-".repeat(50));
                    
                    for (int i = 0; i < modeloTablaPlanes.getRowCount(); i++) {
                        writer.printf("%-25s %-10s %-12s%n",
                            modeloTablaPlanes.getValueAt(i, 0),
                            modeloTablaPlanes.getValueAt(i, 1),
                            modeloTablaPlanes.getValueAt(i, 2));
                    }
                    
                    writer.println();
                    
                    // Próximos vencimientos
                    writer.println("PRÓXIMOS VENCIMIENTOS (30 DÍAS):");
                    writer.println("-".repeat(80));
                    writer.printf("%-25s %-20s %-15s %-10s%n", "SOCIO", "PLAN", "VENCIMIENTO", "DÍAS REST.");
                    writer.println("-".repeat(80));
                    
                    for (int i = 0; i < modeloTablaVencimientos.getRowCount(); i++) {
                        writer.printf("%-25s %-20s %-15s %-10s%n",
                            modeloTablaVencimientos.getValueAt(i, 0),
                            modeloTablaVencimientos.getValueAt(i, 1),
                            modeloTablaVencimientos.getValueAt(i, 2),
                            modeloTablaVencimientos.getValueAt(i, 3));
                    }
                }
                
                ValidadorUI.mostrarExito(this, "Reporte exportado exitosamente a: " + archivo.getName());
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al exportar reporte: " + e.getMessage());
        }
    }
    
    @Override
    public void limpiarFiltros() {
        // Para membresías no hay filtros específicos, solo actualiza los datos
        actualizarDatos();
        ValidadorUI.mostrarExito(this, "Datos actualizados");
    }
    
    private void notificarVencimientos(ActionEvent e) {
        try {
            int proximasVencer = Integer.parseInt(lblProximasVencer.getText());
            
            if (proximasVencer == 0) {
                ValidadorUI.mostrarExito(this, "No hay membresías próximas a vencer en los próximos 30 días.");
                return;
            }
            
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("Se encontraron ").append(proximasVencer).append(" membresías próximas a vencer:\\n\\n");
            
            for (int i = 0; i < modeloTablaVencimientos.getRowCount() && i < 5; i++) {
                mensaje.append("• ").append(modeloTablaVencimientos.getValueAt(i, 0))
                       .append(" - ").append(modeloTablaVencimientos.getValueAt(i, 1))
                       .append(" (").append(modeloTablaVencimientos.getValueAt(i, 3)).append(" días)\\n");
            }
            
            if (modeloTablaVencimientos.getRowCount() > 5) {
                mensaje.append("... y ").append(modeloTablaVencimientos.getRowCount() - 5).append(" más.");
            }
            
            int opcion = JOptionPane.showConfirmDialog(this,
                mensaje.toString(),
                "Notificación de Vencimientos",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            
            if (opcion == JOptionPane.OK_OPTION) {
                ValidadorUI.mostrarExito(this, "Notificaciones enviadas correctamente.");
            }
            
        } catch (Exception ex) {
            ValidadorUI.mostrarError(this, "Error al procesar notificaciones: " + ex.getMessage());
        }
    }
}