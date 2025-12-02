package ui.consultas;

import dominio.Socio;
import dominio.TipoDocumento;
import servicios.ServicioSocio;
import utilidades.FiltrosEntrada;
import utilidades.CampoFecha;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel para consultas avanzadas de socios con filtros
 */
public class PanelConsultaSocios extends JPanel {
    
    private JTextField txtDocumento;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JComboBox<TipoDocumento> comboTipoDoc;
    private CampoFecha campoFechaDesde;
    private CampoFecha campoFechaHasta;
    private JTable tablaSocios;
    private DefaultTableModel modeloTabla;
    private ServicioSocio servicioSocio;
    
    public PanelConsultaSocios() {
        this.servicioSocio = new ServicioSocio();
        configurarPanel();
        inicializarComponentes();
        configurarEventos();
        cargarDatosIniciales();
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Consulta de Socios"));
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
        
        // Fila 1: Documento y Tipo
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Documento:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtDocumento = new JTextField(15);
        txtDocumento.setDocument(new PlainDocument() {
            { setDocumentFilter(new FiltrosEntrada.SoloNumerosFilter(15)); }
        });
        panel.add(txtDocumento, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        comboTipoDoc = new JComboBox<>(TipoDocumento.values());
        comboTipoDoc.insertItemAt(null, 0);
        comboTipoDoc.setSelectedIndex(0);
        panel.add(comboTipoDoc, gbc);
        
        // Fila 2: Nombres y Apellidos
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Nombres:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombres = new JTextField(15);
        txtNombres.setDocument(new PlainDocument() {
            { setDocumentFilter(new FiltrosEntrada.TextoFilter(100)); }
        });
        panel.add(txtNombres, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Apellidos:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtApellidos = new JTextField(15);
        txtApellidos.setDocument(new PlainDocument() {
            { setDocumentFilter(new FiltrosEntrada.TextoFilter(100)); }
        });
        panel.add(txtApellidos, gbc);
        
        // Fila 3: Rango de fechas de registro
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Registro desde:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoFechaDesde = new CampoFecha();
        panel.add(campoFechaDesde, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
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
        String[] columnas = {"Documento", "Tipo", "Nombres", "Apellidos", 
                           "Teléfono", "Email", "Fecha Registro", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaSocios = new JTable(modeloTabla);
        tablaSocios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSocios.setRowSorter(new TableRowSorter<>(modeloTabla));
        
        // Configurar anchos de columnas
        tablaSocios.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaSocios.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaSocios.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaSocios.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaSocios.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaSocios.getColumnModel().getColumn(5).setPreferredWidth(150);
        tablaSocios.getColumnModel().getColumn(6).setPreferredWidth(100);
        tablaSocios.getColumnModel().getColumn(7).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tablaSocios);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setIcon(new ImageIcon("recursos/buscar.png"));
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
        txtDocumento.addActionListener(e -> realizarBusqueda());
        txtNombres.addActionListener(e -> realizarBusqueda());
        txtApellidos.addActionListener(e -> realizarBusqueda());
    }
    
    private void cargarDatosIniciales() {
        try {
            List<Socio> socios = servicioSocio.obtenerTodos();
            actualizarTabla(socios);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos iniciales: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void realizarBusqueda() {
        try {
            // Obtener criterios de búsqueda
            String documento = txtDocumento.getText().trim();
            String nombres = txtNombres.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            TipoDocumento tipoDoc = (TipoDocumento) comboTipoDoc.getSelectedItem();
            LocalDate fechaDesde = campoFechaDesde.getFecha();
            LocalDate fechaHasta = campoFechaHasta.getFecha();
            
            // Validar rango de fechas
            if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
                JOptionPane.showMessageDialog(this,
                    "La fecha 'desde' no puede ser mayor que la fecha 'hasta'",
                    "Error en fechas", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Realizar búsqueda
            List<Socio> resultados = servicioSocio.buscarPorCriterios(
                documento.isEmpty() ? null : documento,
                nombres.isEmpty() ? null : nombres,
                apellidos.isEmpty() ? null : apellidos,
                tipoDoc,
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
    
    private void actualizarTabla(List<Socio> socios) {
        modeloTabla.setRowCount(0);
        for (Socio socio : socios) {
            Object[] fila = {
                socio.getNumDocumento(),
                "DNI",
                socio.getNombres(),
                socio.getApellidos(),
                socio.getTelefono(),
                socio.getEmail(),
                socio.getFechaRegistro(),
                socio.isActivo() ? "Activo" : "Inactivo"
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void limpiarFiltros() {
        txtDocumento.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        comboTipoDoc.setSelectedIndex(0);
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
        chooser.setDialogTitle("Exportar Consulta de Socios");
        chooser.setSelectedFile(new java.io.File("consulta_socios_" + 
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
            writer.println("CONSULTA DE SOCIOS - " + java.time.LocalDateTime.now());
            writer.println("=" .repeat(80));
            writer.println();
            
            // Escribir filtros aplicados
            writer.println("FILTROS APLICADOS:");
            if (!txtDocumento.getText().trim().isEmpty()) {
                writer.println("- Documento: " + txtDocumento.getText());
            }
            if (comboTipoDoc.getSelectedItem() != null) {
                writer.println("- Tipo Documento: " + comboTipoDoc.getSelectedItem());
            }
            if (!txtNombres.getText().trim().isEmpty()) {
                writer.println("- Nombres: " + txtNombres.getText());
            }
            if (!txtApellidos.getText().trim().isEmpty()) {
                writer.println("- Apellidos: " + txtApellidos.getText());
            }
            if (campoFechaDesde.getFecha() != null) {
                writer.println("- Desde: " + campoFechaDesde.getFecha());
            }
            if (campoFechaHasta.getFecha() != null) {
                writer.println("- Hasta: " + campoFechaHasta.getFecha());
            }
            writer.println();
            
            // Escribir datos
            writer.printf("%-15s %-8s %-20s %-20s %-12s %-25s %-12s %-8s%n",
                "DOCUMENTO", "TIPO", "NOMBRES", "APELLIDOS", "TELÉFONO", "EMAIL", "REGISTRO", "ESTADO");
            writer.println("-".repeat(120));
            
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                writer.printf("%-15s %-8s %-20s %-20s %-12s %-25s %-12s %-8s%n",
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