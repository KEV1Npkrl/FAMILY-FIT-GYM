package ui.consultas;

import dominio.Membresia;
import dominio.TipoDocumento;
import servicios.ServicioMembresia;
import utilidades.FiltrosEntrada;
import utilidades.CampoFecha;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel para consultas avanzadas de membresías con filtros
 */
public class PanelConsultaMembresias extends JPanel {
    
    private JTextField txtDocumentoSocio;
    private JTextField txtNombreSocio;
    private JComboBox<String> comboEstado;
    private CampoFecha campoFechaDesde;
    private CampoFecha campoFechaHasta;
    private JTable tablaMembresias;
    private DefaultTableModel modeloTabla;
    private ServicioMembresia servicioMembresia;
    
    public PanelConsultaMembresias() {
        this.servicioMembresia = new ServicioMembresia();
        configurarPanel();
        inicializarComponentes();
        configurarEventos();
        cargarDatosIniciales();
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Consulta de Membresías"));
    }
    
    private void inicializarComponentes() {
        // Panel de filtros
        JPanel panelFiltros = crearPanelFiltros();
        add(panelFiltros, BorderLayout.NORTH);
        
        // Panel de resultados
        JPanel panelResultados = crearPanelResultados();
        add(panelResultados, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Fila 1: Documento y Nombre del Socio
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Doc. Socio:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtDocumentoSocio = new JTextField(15);
        txtDocumentoSocio.setDocument(new PlainDocument() {
            { setDocumentFilter(new FiltrosEntrada.SoloNumerosFilter(15)); }
        });
        panel.add(txtDocumentoSocio, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Nombre Socio:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombreSocio = new JTextField(15);
        txtNombreSocio.setDocument(new PlainDocument() {
            { setDocumentFilter(new FiltrosEntrada.TextoFilter(100)); }
        });
        panel.add(txtNombreSocio, gbc);
        
        // Fila 2: Estado y rango de fechas
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        comboEstado = new JComboBox<>(new String[]{"Todos", "Activa", "Vencida", "Suspendida"});
        comboEstado.setSelectedIndex(0);
        panel.add(comboEstado, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Vigencia desde:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoFechaDesde = new CampoFecha();
        panel.add(campoFechaDesde, gbc);
        
        // Fila 3: Hasta
        gbc.gridx = 2; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("hasta:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoFechaHasta = new CampoFecha();
        panel.add(campoFechaHasta, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resultados"));
        
        // Crear tabla
        String[] columnas = {"ID", "Doc. Socio", "Nombre Socio", "Plan", 
                           "Fecha Inicio", "Fecha Fin", "Precio", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMembresias = new JTable(modeloTabla);
        tablaMembresias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMembresias.setRowSorter(new TableRowSorter<>(modeloTabla));
        
        // Configurar anchos de columnas
        tablaMembresias.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaMembresias.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaMembresias.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaMembresias.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaMembresias.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaMembresias.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaMembresias.getColumnModel().getColumn(6).setPreferredWidth(80);
        tablaMembresias.getColumnModel().getColumn(7).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tablaMembresias);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> realizarBusqueda());
        panel.add(btnBuscar);
        
        JButton btnLimpiar = new JButton("Limpiar Filtros");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiar);
        
        JButton btnExportar = new JButton("Exportar");
        btnExportar.addActionListener(e -> exportarResultados());
        panel.add(btnExportar);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> {
            Window ventana = SwingUtilities.getWindowAncestor(this);
            if (ventana != null) {
                ventana.dispose();
            }
        });
        panel.add(btnCerrar);
        
        return panel;
    }
    
    private void configurarEventos() {
        // Enter en campos de texto para buscar
        txtDocumentoSocio.addActionListener(e -> realizarBusqueda());
        txtNombreSocio.addActionListener(e -> realizarBusqueda());
    }
    
    private void cargarDatosIniciales() {
        try {
            List<Membresia> membresias = servicioMembresia.obtenerTodas();
            actualizarTabla(membresias);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos iniciales: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void realizarBusqueda() {
        try {
            // Obtener criterios de búsqueda
            String docSocio = txtDocumentoSocio.getText().trim();
            String nombreSocio = txtNombreSocio.getText().trim();
            String estado = (String) comboEstado.getSelectedItem();
            LocalDate fechaDesde = campoFechaDesde.getFecha();
            LocalDate fechaHasta = campoFechaHasta.getFecha();
            
            // Validar rango de fechas
            if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
                JOptionPane.showMessageDialog(this,
                    "La fecha 'desde' no puede ser mayor que la fecha 'hasta'",
                    "Error en fechas", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Convertir estado a booleano si es necesario
            Boolean estadoActivo = null;
            if (!"Todos".equals(estado)) {
                estadoActivo = "Activa".equals(estado);
            }
            
            // Realizar búsqueda
            List<Membresia> resultados = servicioMembresia.buscarPorCriterios(
                docSocio.isEmpty() ? null : docSocio,
                nombreSocio.isEmpty() ? null : nombreSocio,
                estadoActivo,
                fechaDesde,
                fechaHasta
            );
            
            actualizarTabla(resultados);
            
            // Mostrar mensaje de resultados
            JOptionPane.showMessageDialog(this,
                String.format("Búsqueda completada. %d registro(s) encontrado(s).", 
                    resultados.size()),
                "Resultados", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error en la búsqueda: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTabla(List<Membresia> membresias) {
        modeloTabla.setRowCount(0);
        for (Membresia membresia : membresias) {
            String estado = determinarEstado(membresia);
            Object[] fila = {
                membresia.getId(),
                membresia.getNumDocumentoSocio(),
                obtenerNombreSocio(membresia.getNumDocumentoSocio()),
                obtenerNombrePlan(membresia.getIdPlan()),
                membresia.getFechaInicio(),
                membresia.getFechaFin(),
                String.format("S/ %.2f", membresia.getPrecio()),
                estado
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private String determinarEstado(Membresia membresia) {
        LocalDate hoy = LocalDate.now();
        if (membresia.getFechaFin().isBefore(hoy)) {
            return "Vencida";
        } else if (membresia.isActiva()) {
            return "Activa";
        } else {
            return "Suspendida";
        }
    }
    
    private String obtenerNombreSocio(String documento) {
        // Implementar consulta para obtener nombre del socio
        return "Socio " + documento; // Placeholder
    }
    
    private String obtenerNombrePlan(int idPlan) {
        // Implementar consulta para obtener nombre del plan
        return "Plan " + idPlan; // Placeholder
    }
    
    private void limpiarFiltros() {
        txtDocumentoSocio.setText("");
        txtNombreSocio.setText("");
        comboEstado.setSelectedIndex(0);
        campoFechaDesde.setFecha(null);
        campoFechaHasta.setFecha(null);
        cargarDatosIniciales();
    }
    
    private void exportarResultados() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay datos para exportar",
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar Consulta de Membresías");
        chooser.setSelectedFile(new java.io.File("consulta_membresias_" + 
            java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                exportarATXT(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this,
                    "Datos exportados exitosamente",
                    "Exportación", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportarATXT(java.io.File archivo) throws Exception {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(archivo, "UTF-8")) {
            // Escribir encabezados
            writer.println("CONSULTA DE MEMBRESÍAS - " + java.time.LocalDateTime.now());
            writer.println("=" .repeat(80));
            writer.println();
            
            // Escribir filtros aplicados
            writer.println("FILTROS APLICADOS:");
            if (!txtDocumentoSocio.getText().trim().isEmpty()) {
                writer.println("- Documento Socio: " + txtDocumentoSocio.getText());
            }
            if (!txtNombreSocio.getText().trim().isEmpty()) {
                writer.println("- Nombre Socio: " + txtNombreSocio.getText());
            }
            if (comboEstado.getSelectedIndex() > 0) {
                writer.println("- Estado: " + comboEstado.getSelectedItem());
            }
            if (campoFechaDesde.getFecha() != null) {
                writer.println("- Desde: " + campoFechaDesde.getFecha());
            }
            if (campoFechaHasta.getFecha() != null) {
                writer.println("- Hasta: " + campoFechaHasta.getFecha());
            }
            writer.println();
            
            // Escribir datos
            writer.printf("%-8s %-12s %-20s %-15s %-12s %-12s %-12s %-10s%n",
                "ID", "DOC_SOCIO", "NOMBRE_SOCIO", "PLAN", "INICIO", "FIN", "PRECIO", "ESTADO");
            writer.println("-".repeat(100));
            
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                writer.printf("%-8s %-12s %-20s %-15s %-12s %-12s %-12s %-10s%n",
                    modeloTabla.getValueAt(i, 0),
                    modeloTabla.getValueAt(i, 1),
                    modeloTabla.getValueAt(i, 2),
                    modeloTabla.getValueAt(i, 3),
                    modeloTabla.getValueAt(i, 4),
                    modeloTabla.getValueAt(i, 5),
                    modeloTabla.getValueAt(i, 6),
                    modeloTabla.getValueAt(i, 7));
            }
            
            writer.println();
            writer.println("Total de registros: " + modeloTabla.getRowCount());
        }
    }
}