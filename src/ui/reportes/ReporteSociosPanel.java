package ui.reportes;

import servicios.ServicioReportes;
import utilidades.ValidadorUI;
import ui.reportes.PanelReportes.ReportePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Panel para reportes de socios
 */
public class ReporteSociosPanel extends JPanel implements ReportePanel {
    
    private ServicioReportes servicioReportes;
    private JLabel lblTotalSocios;
    private JLabel lblSociosActivos;
    private JLabel lblSociosInactivos;
    private JTable tablaRegistrosMes;
    private DefaultTableModel modeloTablaRegistros;
    
    public ReporteSociosPanel() {
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
        
        // Panel central con gráficos y tablas
        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel de acciones
        JPanel panelAcciones = crearPanelAcciones();
        add(panelAcciones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Estadísticas Generales de Socios"));
        
        // Total de socios
        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setBorder(BorderFactory.createEtchedBorder());
        panelTotal.setBackground(new Color(240, 248, 255));
        
        JLabel lblTituloTotal = new JLabel("Total de Socios", SwingConstants.CENTER);
        lblTituloTotal.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalSocios = new JLabel("0", SwingConstants.CENTER);
        lblTotalSocios.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalSocios.setForeground(new Color(70, 130, 180));
        
        panelTotal.add(lblTituloTotal, BorderLayout.NORTH);
        panelTotal.add(lblTotalSocios, BorderLayout.CENTER);
        panel.add(panelTotal);
        
        // Socios activos
        JPanel panelActivos = new JPanel(new BorderLayout());
        panelActivos.setBorder(BorderFactory.createEtchedBorder());
        panelActivos.setBackground(new Color(240, 255, 240));
        
        JLabel lblTituloActivos = new JLabel("Socios Activos", SwingConstants.CENTER);
        lblTituloActivos.setFont(new Font("Arial", Font.BOLD, 12));
        lblSociosActivos = new JLabel("0", SwingConstants.CENTER);
        lblSociosActivos.setFont(new Font("Arial", Font.BOLD, 24));
        lblSociosActivos.setForeground(new Color(34, 139, 34));
        
        panelActivos.add(lblTituloActivos, BorderLayout.NORTH);
        panelActivos.add(lblSociosActivos, BorderLayout.CENTER);
        panel.add(panelActivos);
        
        // Socios inactivos
        JPanel panelInactivos = new JPanel(new BorderLayout());
        panelInactivos.setBorder(BorderFactory.createEtchedBorder());
        panelInactivos.setBackground(new Color(255, 240, 240));
        
        JLabel lblTituloInactivos = new JLabel("Socios Inactivos", SwingConstants.CENTER);
        lblTituloInactivos.setFont(new Font("Arial", Font.BOLD, 12));
        lblSociosInactivos = new JLabel("0", SwingConstants.CENTER);
        lblSociosInactivos.setFont(new Font("Arial", Font.BOLD, 24));
        lblSociosInactivos.setForeground(new Color(220, 20, 60));
        
        panelInactivos.add(lblTituloInactivos, BorderLayout.NORTH);
        panelInactivos.add(lblSociosInactivos, BorderLayout.CENTER);
        panel.add(panelInactivos);
        
        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registros de Socios por Mes (Último Año)"));
        
        // Crear tabla para mostrar registros por mes
        String[] columnas = {"Año", "Mes", "Cantidad de Registros"};
        modeloTablaRegistros = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaRegistrosMes = new JTable(modeloTablaRegistros);
        tablaRegistrosMes.setRowSorter(new javax.swing.table.TableRowSorter<>(modeloTablaRegistros));
        
        // Configurar anchos de columnas
        tablaRegistrosMes.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaRegistrosMes.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaRegistrosMes.getColumnModel().getColumn(2).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(tablaRegistrosMes);
        scrollPane.setPreferredSize(new Dimension(400, 300));
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
        
        return panel;
    }
    
    @Override
    public void actualizarDatos() {
        try {
            // Actualizar estadísticas generales
            Map<String, Object> estadisticas = servicioReportes.obtenerEstadisticasSocios();
            
            lblTotalSocios.setText(String.valueOf(estadisticas.getOrDefault("totalSocios", 0)));
            lblSociosActivos.setText(String.valueOf(estadisticas.getOrDefault("sociosActivos", 0)));
            lblSociosInactivos.setText(String.valueOf(estadisticas.getOrDefault("sociosInactivos", 0)));
            
            // Actualizar tabla de registros por mes
            List<Map<String, Object>> registrosPorMes = servicioReportes.obtenerSociosPorMes();
            modeloTablaRegistros.setRowCount(0);
            
            String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                             "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            
            for (Map<String, Object> registro : registrosPorMes) {
                Object[] fila = {
                    registro.get("año"),
                    meses[(Integer) registro.get("mes")],
                    registro.get("cantidad")
                };
                modeloTablaRegistros.addRow(fila);
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al actualizar datos: " + e.getMessage());
        }
    }
    
    @Override
    public void exportarReporte() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Exportar Reporte de Socios");
            chooser.setSelectedFile(new File("reporte_socios_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt"));
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                
                try (PrintWriter writer = new PrintWriter(archivo, "UTF-8")) {
                    // Encabezado del reporte
                    writer.println("REPORTE DE SOCIOS - FAMILY FIT GYM");
                    writer.println("Fecha de generación: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    writer.println("=".repeat(60));
                    writer.println();
                    
                    // Estadísticas generales
                    writer.println("ESTADÍSTICAS GENERALES:");
                    writer.println("- Total de socios: " + lblTotalSocios.getText());
                    writer.println("- Socios activos: " + lblSociosActivos.getText());
                    writer.println("- Socios inactivos: " + lblSociosInactivos.getText());
                    writer.println();
                    
                    // Registros por mes
                    writer.println("REGISTROS POR MES (ÚLTIMO AÑO):");
                    writer.println("-".repeat(40));
                    writer.printf("%-6s %-12s %-10s%n", "AÑO", "MES", "CANTIDAD");
                    writer.println("-".repeat(40));
                    
                    for (int i = 0; i < modeloTablaRegistros.getRowCount(); i++) {
                        writer.printf("%-6s %-12s %-10s%n",
                            modeloTablaRegistros.getValueAt(i, 0),
                            modeloTablaRegistros.getValueAt(i, 1),
                            modeloTablaRegistros.getValueAt(i, 2));
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
        // Para socios no hay filtros específicos, solo actualiza los datos
        actualizarDatos();
        ValidadorUI.mostrarExito(this, "Datos actualizados");
    }
}