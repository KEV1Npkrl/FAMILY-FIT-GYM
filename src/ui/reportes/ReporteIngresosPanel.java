package ui.reportes;

import servicios.ServicioReportes;
import utilidades.CampoFecha;
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
 * Panel para reportes de ingresos
 */
public class ReporteIngresosPanel extends JPanel implements ReportePanel {
    
    private ServicioReportes servicioReportes;
    private CampoFecha campoFechaInicio;
    private CampoFecha campoFechaFin;
    private JLabel lblTotalPagos;
    private JLabel lblMontoTotal;
    private JLabel lblPromedioPago;
    private JTable tablaMetodosPago;
    private DefaultTableModel modeloTablaMetodos;
    
    public ReporteIngresosPanel() {
        this.servicioReportes = new ServicioReportes();
        configurarPanel();
        inicializarComponentes();
        configurarFechasDefault();
        actualizarDatos();
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel de filtros
        JPanel panelFiltros = crearPanelFiltros();
        add(panelFiltros, BorderLayout.NORTH);
        
        // Panel de estadísticas
        JPanel panelEstadisticas = crearPanelEstadisticas();
        add(panelEstadisticas, BorderLayout.CENTER);
        
        // Panel de métodos de pago
        JPanel panelMetodos = crearPanelMetodosPago();
        add(panelMetodos, BorderLayout.EAST);
        
        // Panel de acciones
        JPanel panelAcciones = crearPanelAcciones();
        add(panelAcciones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Filtros de Fecha"));
        
        panel.add(new JLabel("Desde:"));
        campoFechaInicio = new CampoFecha();
        panel.add(campoFechaInicio);
        
        panel.add(new JLabel("Hasta:"));
        campoFechaFin = new CampoFecha();
        panel.add(campoFechaFin);
        
        JButton btnFiltrar = new JButton("🔍 Filtrar");
        btnFiltrar.addActionListener(e -> actualizarDatos());
        panel.add(btnFiltrar);
        
        return panel;
    }
    
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Estadísticas de Ingresos"));
        
        // Total de pagos
        JPanel panelTotalPagos = new JPanel(new BorderLayout());
        panelTotalPagos.setBorder(BorderFactory.createEtchedBorder());
        panelTotalPagos.setBackground(new Color(240, 248, 255));
        
        JLabel lblTituloTotalPagos = new JLabel("Total de Pagos", SwingConstants.CENTER);
        lblTituloTotalPagos.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalPagos = new JLabel("0", SwingConstants.CENTER);
        lblTotalPagos.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalPagos.setForeground(new Color(70, 130, 180));
        
        panelTotalPagos.add(lblTituloTotalPagos, BorderLayout.NORTH);
        panelTotalPagos.add(lblTotalPagos, BorderLayout.CENTER);
        panel.add(panelTotalPagos);
        
        // Monto total
        JPanel panelMontoTotal = new JPanel(new BorderLayout());
        panelMontoTotal.setBorder(BorderFactory.createEtchedBorder());
        panelMontoTotal.setBackground(new Color(240, 255, 240));
        
        JLabel lblTituloMontoTotal = new JLabel("Monto Total", SwingConstants.CENTER);
        lblTituloMontoTotal.setFont(new Font("Arial", Font.BOLD, 12));
        lblMontoTotal = new JLabel("S/ 0.00", SwingConstants.CENTER);
        lblMontoTotal.setFont(new Font("Arial", Font.BOLD, 24));
        lblMontoTotal.setForeground(new Color(34, 139, 34));
        
        panelMontoTotal.add(lblTituloMontoTotal, BorderLayout.NORTH);
        panelMontoTotal.add(lblMontoTotal, BorderLayout.CENTER);
        panel.add(panelMontoTotal);
        
        // Promedio por pago
        JPanel panelPromedio = new JPanel(new BorderLayout());
        panelPromedio.setBorder(BorderFactory.createEtchedBorder());
        panelPromedio.setBackground(new Color(255, 248, 220));
        
        JLabel lblTituloPromedio = new JLabel("Promedio por Pago", SwingConstants.CENTER);
        lblTituloPromedio.setFont(new Font("Arial", Font.BOLD, 12));
        lblPromedioPago = new JLabel("S/ 0.00", SwingConstants.CENTER);
        lblPromedioPago.setFont(new Font("Arial", Font.BOLD, 24));
        lblPromedioPago.setForeground(new Color(255, 140, 0));
        
        panelPromedio.add(lblTituloPromedio, BorderLayout.NORTH);
        panelPromedio.add(lblPromedioPago, BorderLayout.CENTER);
        panel.add(panelPromedio);
        
        return panel;
    }
    
    private JPanel crearPanelMetodosPago() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Ingresos por Método de Pago"));
        panel.setPreferredSize(new Dimension(300, 0));
        
        // Crear tabla para métodos de pago
        String[] columnas = {"Método de Pago", "Cantidad", "Monto Total"};
        modeloTablaMetodos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMetodosPago = new JTable(modeloTablaMetodos);
        tablaMetodosPago.setRowSorter(new javax.swing.table.TableRowSorter<>(modeloTablaMetodos));
        
        // Configurar anchos de columnas
        tablaMetodosPago.getColumnModel().getColumn(0).setPreferredWidth(120);
        tablaMetodosPago.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaMetodosPago.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(tablaMetodosPago);
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
    
    private void configurarFechasDefault() {
        // Configurar fechas por defecto (último mes)
        LocalDate hoy = LocalDate.now();
        campoFechaFin.setFecha(hoy);
        campoFechaInicio.setFecha(hoy.minusMonths(1));
    }
    
    @Override
    public void actualizarDatos() {
        try {
            LocalDate fechaInicio = campoFechaInicio.getFecha();
            LocalDate fechaFin = campoFechaFin.getFecha();
            
            if (fechaInicio == null || fechaFin == null) {
                ValidadorUI.mostrarError(this, "Por favor seleccione ambas fechas");
                return;
            }
            
            if (fechaInicio.isAfter(fechaFin)) {
                ValidadorUI.mostrarError(this, "La fecha de inicio no puede ser mayor que la fecha fin");
                return;
            }
            
            // Actualizar estadísticas de ingresos
            Map<String, Object> ingresos = servicioReportes.obtenerIngresosPorPeriodo(fechaInicio, fechaFin);
            
            lblTotalPagos.setText(String.valueOf(ingresos.getOrDefault("totalPagos", 0)));
            
            double montoTotal = (Double) ingresos.getOrDefault("montoTotal", 0.0);
            lblMontoTotal.setText(String.format("S/ %.2f", montoTotal));
            
            double promedioPago = (Double) ingresos.getOrDefault("promedioPago", 0.0);
            lblPromedioPago.setText(String.format("S/ %.2f", promedioPago));
            
            // Actualizar tabla de métodos de pago
            List<Map<String, Object>> metodosPago = servicioReportes.obtenerIngresosPorMetodoPago(fechaInicio, fechaFin);
            modeloTablaMetodos.setRowCount(0);
            
            for (Map<String, Object> metodo : metodosPago) {
                Object[] fila = {
                    metodo.get("metodo"),
                    metodo.get("cantidadPagos"),
                    String.format("S/ %.2f", (Double) metodo.get("montoTotal"))
                };
                modeloTablaMetodos.addRow(fila);
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al actualizar datos: " + e.getMessage());
        }
    }
    
    @Override
    public void exportarReporte() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Exportar Reporte de Ingresos");
            chooser.setSelectedFile(new File("reporte_ingresos_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt"));
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                
                try (PrintWriter writer = new PrintWriter(archivo, "UTF-8")) {
                    // Encabezado del reporte
                    writer.println("REPORTE DE INGRESOS - FAMILY FIT GYM");
                    writer.println("Fecha de generación: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    writer.println("Período: " + 
                        (campoFechaInicio.getFecha() != null ? campoFechaInicio.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A") +
                        " al " +
                        (campoFechaFin.getFecha() != null ? campoFechaFin.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
                    writer.println("=".repeat(60));
                    writer.println();
                    
                    // Estadísticas generales
                    writer.println("ESTADÍSTICAS GENERALES:");
                    writer.println("- Total de pagos: " + lblTotalPagos.getText());
                    writer.println("- Monto total: " + lblMontoTotal.getText());
                    writer.println("- Promedio por pago: " + lblPromedioPago.getText());
                    writer.println();
                    
                    // Métodos de pago
                    writer.println("INGRESOS POR MÉTODO DE PAGO:");
                    writer.println("-".repeat(50));
                    writer.printf("%-20s %-10s %-15s%n", "MÉTODO", "CANTIDAD", "MONTO TOTAL");
                    writer.println("-".repeat(50));
                    
                    for (int i = 0; i < modeloTablaMetodos.getRowCount(); i++) {
                        writer.printf("%-20s %-10s %-15s%n",
                            modeloTablaMetodos.getValueAt(i, 0),
                            modeloTablaMetodos.getValueAt(i, 1),
                            modeloTablaMetodos.getValueAt(i, 2));
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
        configurarFechasDefault();
        actualizarDatos();
        ValidadorUI.mostrarExito(this, "Filtros restablecidos al último mes");
    }
}