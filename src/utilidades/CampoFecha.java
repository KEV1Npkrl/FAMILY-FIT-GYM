package utilidades;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Componente de fecha que usa un calendario popup visual
 */
public class CampoFecha extends JPanel {
    private final JTextField txtFecha;
    private final JButton btnCalendario;
    private LocalDate fechaSeleccionada;
    private JPopupMenu popupCalendario;
    
    public CampoFecha() {
        this(LocalDate.now());
    }
    
    public CampoFecha(LocalDate fechaInicial) {
        this.fechaSeleccionada = fechaInicial;
        setLayout(new BorderLayout());
        
        // Campo de texto para mostrar la fecha
        txtFecha = new JTextField(fechaInicial.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtFecha.setEditable(false);
        txtFecha.setPreferredSize(new Dimension(100, 25));
        
        // Botón para abrir calendario
        btnCalendario = new JButton("📅");
        btnCalendario.setPreferredSize(new Dimension(30, 25));
        btnCalendario.setToolTipText("Seleccionar fecha");
        btnCalendario.addActionListener(this::mostrarCalendario);
        
        // Agregar componentes
        add(txtFecha, BorderLayout.CENTER);
        add(btnCalendario, BorderLayout.EAST);
        
        // Crear popup del calendario
        crearPopupCalendario();
    }
    
    private void crearPopupCalendario() {
        popupCalendario = new JPopupMenu();
        popupCalendario.setLayout(new BorderLayout());
        
        // Panel principal del calendario
        JPanel panelCalendario = new JPanel(new BorderLayout());
        panelCalendario.setBorder(new LineBorder(Color.GRAY));
        panelCalendario.setPreferredSize(new Dimension(250, 200));
        
        // Header con mes/año y botones de navegación
        JPanel header = crearHeader();
        panelCalendario.add(header, BorderLayout.NORTH);
        
        // Grid del calendario
        JTable tablaCalendario = crearTablaCalendario();
        JScrollPane scroll = new JScrollPane(tablaCalendario);
        scroll.setPreferredSize(new Dimension(240, 150));
        panelCalendario.add(scroll, BorderLayout.CENTER);
        
        popupCalendario.add(panelCalendario);
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        
        // Botones de navegación
        JButton btnAnterior = new JButton("◀");
        JButton btnSiguiente = new JButton("▶");
        
        btnAnterior.addActionListener(e -> {
            LocalDate nuevaFecha = fechaSeleccionada.minusMonths(1);
            // Permitir navegar a meses pasados sin restricción
            fechaSeleccionada = nuevaFecha;
            actualizarCalendario();
        });
        
        btnSiguiente.addActionListener(e -> {
            LocalDate nuevaFecha = fechaSeleccionada.plusMonths(1);
            // Verificar que no se navegue más allá del mes actual
            if (nuevaFecha.getYear() > LocalDate.now().getYear() || 
                (nuevaFecha.getYear() == LocalDate.now().getYear() && 
                 nuevaFecha.getMonthValue() > LocalDate.now().getMonthValue())) {
                // Mostrar mensaje y no permitir navegación
                JOptionPane.showMessageDialog((Component)e.getSource(), 
                    "No se puede navegar a fechas futuras.", 
                    "Navegación restringida", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            fechaSeleccionada = nuevaFecha;
            actualizarCalendario();
        });
        
        // Label del mes/año
        JLabel lblMesAno = new JLabel();
        lblMesAno.setHorizontalAlignment(SwingConstants.CENTER);
        lblMesAno.setFont(lblMesAno.getFont().deriveFont(Font.BOLD));
        
        header.add(btnAnterior, BorderLayout.WEST);
        header.add(lblMesAno, BorderLayout.CENTER);
        header.add(btnSiguiente, BorderLayout.EAST);
        
        return header;
    }
    
    private JTable crearTablaCalendario() {
        // Nombres de los días de la semana
        String[] columnNames = {"Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 6) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(model);
        tabla.setRowHeight(25);
        tabla.setShowGrid(true);
        tabla.setGridColor(Color.LIGHT_GRAY);
        
        // Deshabilitar selección de filas/columnas para evitar pintar toda la fila
        tabla.setRowSelectionAllowed(false);
        tabla.setColumnSelectionAllowed(false);
        tabla.setCellSelectionEnabled(false);
        
        // Renderer personalizado para resaltar días
        tabla.setDefaultRenderer(Object.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                
                if (value != null) {
                    label.setText(value.toString());
                    
                    try {
                        int dia = Integer.parseInt(value.toString());
                        LocalDate fechaDia = fechaSeleccionada.withDayOfMonth(dia);
                        
                        // Verificar si es el día seleccionado específicamente
                        boolean esDiaSeleccionado = dia == fechaSeleccionada.getDayOfMonth();
                        
                        // Deshabilitar visualmente días futuros
                        if (fechaDia.isAfter(LocalDate.now())) {
                            label.setBackground(Color.LIGHT_GRAY);
                            label.setForeground(Color.GRAY);
                        }
                        // Resaltar SOLO el día específico seleccionado
                        else if (esDiaSeleccionado) {
                            label.setBackground(Color.BLUE);
                            label.setForeground(Color.WHITE);
                        } else {
                            label.setBackground(Color.WHITE);
                            label.setForeground(Color.BLACK);
                        }
                    } catch (NumberFormatException ex) {
                        label.setBackground(Color.WHITE);
                        label.setForeground(Color.BLACK);
                    }
                } else {
                    label.setBackground(Color.LIGHT_GRAY);
                }
                
                return label;
            }
        });
        
        // Click para seleccionar fecha
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabla.rowAtPoint(e.getPoint());
                int col = tabla.columnAtPoint(e.getPoint());
                Object value = tabla.getValueAt(row, col);
                
                if (value != null && !value.toString().isEmpty()) {
                    try {
                        int dia = Integer.parseInt(value.toString());
                        LocalDate fechaSeleccionadaTemp = fechaSeleccionada.withDayOfMonth(dia);
                        
                        // Verificar que la fecha no sea posterior a hoy
                        if (fechaSeleccionadaTemp.isAfter(LocalDate.now())) {
                            // Mostrar mensaje de error y no permitir selección
                            JOptionPane.showMessageDialog(CampoFecha.this, 
                                "No se puede seleccionar una fecha posterior a la actual.", 
                                "Fecha inválida", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        fechaSeleccionada = fechaSeleccionadaTemp;
                        txtFecha.setText(fechaSeleccionada.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        
                        // Forzar repintado de la tabla para actualizar el resaltado
                        tabla.repaint();
                        
                        popupCalendario.setVisible(false);
                    } catch (NumberFormatException ex) {
                        // Ignorar clicks en celdas vacías
                    }
                }
            }
        });
        
        return tabla;
    }
    
    private void actualizarCalendario() {
        // Actualizar el header del popup si está visible
        if (popupCalendario.isVisible()) {
            Component[] components = ((JPanel)popupCalendario.getComponent(0)).getComponents();
            JPanel header = (JPanel) components[0];
            JLabel lblMesAno = (JLabel) ((JPanel) header).getComponent(1);
            
            String mesAno = fechaSeleccionada.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES")));
            lblMesAno.setText(mesAno.substring(0, 1).toUpperCase() + mesAno.substring(1));
            
            // Actualizar la tabla del calendario
            JScrollPane scroll = (JScrollPane) components[1];
            JTable tabla = (JTable) scroll.getViewport().getView();
            llenarCalendario(tabla);
        }
    }
    
    private void llenarCalendario(JTable tabla) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();
        
        // Limpiar tabla
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                model.setValueAt("", i, j);
            }
        }
        
        // Obtener primer día del mes y días en el mes
        LocalDate primerDia = fechaSeleccionada.withDayOfMonth(1);
        int diasEnMes = fechaSeleccionada.lengthOfMonth();
        int diaSemanaInicio = primerDia.getDayOfWeek().getValue() % 7; // Domingo = 0
        
        // Llenar calendario
        int dia = 1;
        for (int semana = 0; semana < 6 && dia <= diasEnMes; semana++) {
            for (int diaSemana = 0; diaSemana < 7; diaSemana++) {
                if (semana == 0 && diaSemana < diaSemanaInicio) {
                    // Días del mes anterior (vacíos)
                    continue;
                } else if (dia <= diasEnMes) {
                    model.setValueAt(dia, semana, diaSemana);
                    dia++;
                }
            }
        }
    }
    
    private void mostrarCalendario(ActionEvent e) {
        // Actualizar el calendario antes de mostrarlo
        Component[] components = ((JPanel)popupCalendario.getComponent(0)).getComponents();
        
        // Actualizar header
        JPanel header = (JPanel) components[0];
        JLabel lblMesAno = (JLabel) ((JPanel) header).getComponent(1);
        String mesAno = fechaSeleccionada.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES")));
        lblMesAno.setText(mesAno.substring(0, 1).toUpperCase() + mesAno.substring(1));
        
        // Actualizar tabla
        JScrollPane scroll = (JScrollPane) components[1];
        JTable tabla = (JTable) scroll.getViewport().getView();
        llenarCalendario(tabla);
        
        // Mostrar popup
        popupCalendario.show(btnCalendario, 0, btnCalendario.getHeight());
    }
    
    /**
     * Obtener fecha seleccionada como LocalDate
     */
    public LocalDate getFecha() {
        return fechaSeleccionada;
    }
    
    /**
     * Establecer fecha (solo permite fechas hasta hoy)
     */
    public void setFecha(LocalDate fecha) {
        // Verificar que la fecha no sea posterior a hoy
        if (fecha.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, 
                "No se puede establecer una fecha posterior a la actual.", 
                "Fecha inválida", 
                JOptionPane.WARNING_MESSAGE);
            // Establecer la fecha actual como alternativa
            fecha = LocalDate.now();
        }
        
        this.fechaSeleccionada = fecha;
        txtFecha.setText(fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    
    /**
     * Obtener fecha como String en formato YYYY-MM-DD
     */
    public String getFechaString() {
        return fechaSeleccionada.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    /**
     * Establecer si el componente está habilitado
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtFecha.setEnabled(enabled);
        btnCalendario.setEnabled(enabled);
    }
    
    /**
     * Obtener referencia al campo de texto (para configuraciones avanzadas)
     */
    public JTextField getTextField() {
        return txtFecha;
    }
}