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
 * Panel para reportes de asistencias
 */
public class ReporteAsistenciasPanel extends JPanel implements ReportePanel {
    
    private ServicioReportes servicioReportes;
    private CampoFecha campoFechaInicio;
    private CampoFecha campoFechaFin;
    private JLabel lblTotalAsistencias;
    private JLabel lblPromedioDiario;
    private JLabel lblHoraPico;
    private JTable tablaDiasSemana;
    private DefaultTableModel modeloTablaDias;
    private JTable tablaHorarios;
    private DefaultTableModel modeloTablaHorarios;
    
    public ReporteAsistenciasPanel() {
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
        
        // Panel central con estadísticas y tablas
        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);
        
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
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de estadísticas en la parte superior
        JPanel panelEstadisticas = crearPanelEstadisticas();
        panel.add(panelEstadisticas, BorderLayout.NORTH);
        
        // Panel con las dos tablas
        JPanel panelTablas = crearPanelTablas();
        panel.add(panelTablas, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Estadísticas de Asistencias"));
        panel.setPreferredSize(new Dimension(0, 100));
        
        // Total de asistencias
        JPanel panelTotalAsistencias = new JPanel(new BorderLayout());
        panelTotalAsistencias.setBorder(BorderFactory.createEtchedBorder());
        panelTotalAsistencias.setBackground(new Color(240, 248, 255));
        
        JLabel lblTituloTotalAsistencias = new JLabel("Total Asistencias", SwingConstants.CENTER);
        lblTituloTotalAsistencias.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalAsistencias = new JLabel("0", SwingConstants.CENTER);
        lblTotalAsistencias.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalAsistencias.setForeground(new Color(70, 130, 180));
        
        panelTotalAsistencias.add(lblTituloTotalAsistencias, BorderLayout.NORTH);
        panelTotalAsistencias.add(lblTotalAsistencias, BorderLayout.CENTER);
        panel.add(panelTotalAsistencias);
        
        // Promedio diario
        JPanel panelPromedioDiario = new JPanel(new BorderLayout());
        panelPromedioDiario.setBorder(BorderFactory.createEtchedBorder());
        panelPromedioDiario.setBackground(new Color(240, 255, 240));
        
        JLabel lblTituloPromedioDiario = new JLabel("Promedio Diario", SwingConstants.CENTER);
        lblTituloPromedioDiario.setFont(new Font("Arial", Font.BOLD, 12));
        lblPromedioDiario = new JLabel("0.0", SwingConstants.CENTER);
        lblPromedioDiario.setFont(new Font("Arial", Font.BOLD, 24));
        lblPromedioDiario.setForeground(new Color(34, 139, 34));
        
        panelPromedioDiario.add(lblTituloPromedioDiario, BorderLayout.NORTH);
        panelPromedioDiario.add(lblPromedioDiario, BorderLayout.CENTER);
        panel.add(panelPromedioDiario);
        
        // Hora pico
        JPanel panelHoraPico = new JPanel(new BorderLayout());
        panelHoraPico.setBorder(BorderFactory.createEtchedBorder());
        panelHoraPico.setBackground(new Color(255, 248, 220));
        
        JLabel lblTituloHoraPico = new JLabel("Hora Pico", SwingConstants.CENTER);
        lblTituloHoraPico.setFont(new Font("Arial", Font.BOLD, 12));
        lblHoraPico = new JLabel("--:--", SwingConstants.CENTER);
        lblHoraPico.setFont(new Font("Arial", Font.BOLD, 24));
        lblHoraPico.setForeground(new Color(255, 140, 0));
        
        panelHoraPico.add(lblTituloHoraPico, BorderLayout.NORTH);
        panelHoraPico.add(lblHoraPico, BorderLayout.CENTER);
        panel.add(panelHoraPico);
        
        return panel;
    }
    
    private JPanel crearPanelTablas() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Tabla de días de la semana
        JPanel panelDiasSemana = crearTablaDiasSemana();
        panel.add(panelDiasSemana);
        
        // Tabla de horarios pico
        JPanel panelHorarios = crearTablaHorarios();
        panel.add(panelHorarios);
        
        return panel;
    }
    
    private JPanel crearTablaDiasSemana() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Asistencias por Día de la Semana"));
        
        String[] columnas = {"Día", "Asistencias", "Promedio"};
        modeloTablaDias = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaDiasSemana = new JTable(modeloTablaDias);
        tablaDiasSemana.setRowSorter(new javax.swing.table.TableRowSorter<>(modeloTablaDias));
        
        // Configurar anchos de columnas
        tablaDiasSemana.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaDiasSemana.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaDiasSemana.getColumnModel().getColumn(2).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tablaDiasSemana);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearTablaHorarios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Horarios de Mayor Afluencia"));
        
        String[] columnas = {"Hora", "Asistencias", "Porcentaje"};
        modeloTablaHorarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaHorarios = new JTable(modeloTablaHorarios);
        tablaHorarios.setRowSorter(new javax.swing.table.TableRowSorter<>(modeloTablaHorarios));
        
        // Configurar anchos de columnas
        tablaHorarios.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaHorarios.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaHorarios.getColumnModel().getColumn(2).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tablaHorarios);
        scrollPane.setPreferredSize(new Dimension(0, 200));
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
            
            // Actualizar estadísticas generales
            Map<String, Object> estadisticas = servicioReportes.obtenerEstadisticasAsistencias(fechaInicio, fechaFin);
            
            lblTotalAsistencias.setText(String.valueOf(estadisticas.getOrDefault("totalAsistencias", 0)));
            
            double promedioDiario = (Double) estadisticas.getOrDefault("promedioDiario", 0.0);
            lblPromedioDiario.setText(String.format("%.1f", promedioDiario));
            
            String horaPico = (String) estadisticas.getOrDefault("horaPico", "--:--");
            lblHoraPico.setText(horaPico);
            
            // Actualizar tabla de días de la semana
            List<Map<String, Object>> diasSemana = servicioReportes.obtenerAsistenciasPorDiaSemana(fechaInicio, fechaFin);
            modeloTablaDias.setRowCount(0);
            
            for (Map<String, Object> dia : diasSemana) {
                Object[] fila = {
                    dia.get("diaSemana"),
                    dia.get("totalAsistencias"),
                    String.format("%.1f", (Double) dia.get("promedio"))
                };
                modeloTablaDias.addRow(fila);
            }
            
            // Actualizar tabla de horarios pico
            List<Map<String, Object>> horariosPico = servicioReportes.obtenerHorariosPico(fechaInicio, fechaFin);
            modeloTablaHorarios.setRowCount(0);
            
            for (Map<String, Object> horario : horariosPico) {
                Object[] fila = {
                    horario.get("hora"),
                    horario.get("asistencias"),
                    String.format("%.1f%%", (Double) horario.get("porcentaje"))
                };
                modeloTablaHorarios.addRow(fila);
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al actualizar datos: " + e.getMessage());
        }
    }
    
    @Override
    public void exportarReporte() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Exportar Reporte de Asistencias");
            chooser.setSelectedFile(new File("reporte_asistencias_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt"));
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                
                try (PrintWriter writer = new PrintWriter(archivo, "UTF-8")) {
                    // Encabezado del reporte
                    writer.println("REPORTE DE ASISTENCIAS - FAMILY FIT GYM");
                    writer.println("Fecha de generación: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    writer.println("Período: " + 
                        (campoFechaInicio.getFecha() != null ? campoFechaInicio.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A") +
                        " al " +
                        (campoFechaFin.getFecha() != null ? campoFechaFin.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
                    writer.println("=".repeat(60));
                    writer.println();
                    
                    // Estadísticas generales
                    writer.println("ESTADÍSTICAS GENERALES:");
                    writer.println("- Total de asistencias: " + lblTotalAsistencias.getText());
                    writer.println("- Promedio diario: " + lblPromedioDiario.getText());
                    writer.println("- Hora pico: " + lblHoraPico.getText());
                    writer.println();
                    
                    // Asistencias por día de la semana
                    writer.println("ASISTENCIAS POR DÍA DE LA SEMANA:");
                    writer.println("-".repeat(40));
                    writer.printf("%-15s %-12s %-10s%n", "DÍA", "ASISTENCIAS", "PROMEDIO");
                    writer.println("-".repeat(40));
                    
                    for (int i = 0; i < modeloTablaDias.getRowCount(); i++) {
                        writer.printf("%-15s %-12s %-10s%n",
                            modeloTablaDias.getValueAt(i, 0),
                            modeloTablaDias.getValueAt(i, 1),
                            modeloTablaDias.getValueAt(i, 2));
                    }
                    
                    writer.println();
                    
                    // Horarios de mayor afluencia
                    writer.println("HORARIOS DE MAYOR AFLUENCIA:");
                    writer.println("-".repeat(40));
                    writer.printf("%-10s %-12s %-12s%n", "HORA", "ASISTENCIAS", "PORCENTAJE");
                    writer.println("-".repeat(40));
                    
                    for (int i = 0; i < modeloTablaHorarios.getRowCount(); i++) {
                        writer.printf("%-10s %-12s %-12s%n",
                            modeloTablaHorarios.getValueAt(i, 0),
                            modeloTablaHorarios.getValueAt(i, 1),
                            modeloTablaHorarios.getValueAt(i, 2));
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