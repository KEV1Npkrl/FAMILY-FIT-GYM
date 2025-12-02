package ui.mantenimiento;

import dominio.Evento;
import dominio.Empleado;
import dominio.Programacion;
import servicios.ServicioEvento;
import servicios.ServicioEmpleadoSimple;
import servicios.ServicioProgramacion;
import utilidades.ValidadorUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DialogoProgramacion extends JDialog {
    private ServicioProgramacion servicioProgramacion;
    private ServicioEvento servicioEvento;
    private ServicioEmpleadoSimple servicioEmpleado;
    private Programacion programacionEditando;
    private boolean esEdicion;
    
    // Componentes
    private JComboBox<Evento> cmbEvento;
    private JSpinner spnFecha;
    private JSpinner spnHora;
    private JSpinner spnMinutos;
    private JTextField txtLugar;
    private JComboBox<Empleado> cmbInstructor;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    public DialogoProgramacion(Window parent) {
        this(parent, null);
    }
    
    public DialogoProgramacion(Window parent, Programacion programacion) {
        super(parent, programacion == null ? "Nueva Programacion" : "Editar Programacion", Dialog.ModalityType.APPLICATION_MODAL);
        
        this.programacionEditando = programacion;
        this.esEdicion = (programacion != null);
        this.servicioProgramacion = new ServicioProgramacion();
        this.servicioEvento = new ServicioEvento();
        this.servicioEmpleado = new ServicioEmpleadoSimple();
        
        initComponents();
        cargarEventos();
        cargarInstructores();
        
        if (esEdicion) {
            cargarDatosProgramacion();
        }
        
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(500, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int fila = 0;
        
        // Evento
        gbc.gridx = 0; gbc.gridy = fila;
        panelPrincipal.add(new JLabel("Evento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbEvento = new JComboBox<>();
        panelPrincipal.add(cmbEvento, gbc);
        fila++;
        
        // Fecha
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        spnFecha = new JSpinner(new SpinnerDateModel());
        spnFecha.setEditor(new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy"));
        panelPrincipal.add(spnFecha, gbc);
        fila++;
        
        // Hora
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Hora:"), gbc);
        
        JPanel panelHora = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        spnHora = new JSpinner(new SpinnerNumberModel(8, 0, 23, 1));
        spnHora.setPreferredSize(new Dimension(60, 25));
        panelHora.add(spnHora);
        panelHora.add(new JLabel(":"));
        spnMinutos = new JSpinner(new SpinnerNumberModel(0, 0, 59, 15));
        spnMinutos.setPreferredSize(new Dimension(60, 25));
        panelHora.add(spnMinutos);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelPrincipal.add(panelHora, gbc);
        fila++;
        
        // Lugar
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Lugar:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtLugar = new JTextField(30);
        panelPrincipal.add(txtLugar, gbc);
        fila++;
        
        // Instructor (ComboBox)
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelPrincipal.add(new JLabel("Instructor:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbInstructor = new JComboBox<>();
        panelPrincipal.add(cmbInstructor, gbc);
        
        add(panelPrincipal, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarProgramacion());
        panelBotones.add(btnGuardar);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarEventos() {
        try {
            cmbEvento.removeAllItems();
            List<Evento> eventos = servicioEvento.obtenerTodos();
            for (Evento evento : eventos) {
                cmbEvento.addItem(evento);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar eventos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarInstructores() {
        try {
            cmbInstructor.removeAllItems();
            List<Empleado> empleados = servicioEmpleado.obtenerTodos();
            for (Empleado empleado : empleados) {
                cmbInstructor.addItem(empleado);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar instructores: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDatosProgramacion() {
        if (programacionEditando != null) {
            // Seleccionar evento
            for (int i = 0; i < cmbEvento.getItemCount(); i++) {
                Evento evento = cmbEvento.getItemAt(i);
                if (evento.getIdEvento() == programacionEditando.getIdEvento()) {
                    cmbEvento.setSelectedIndex(i);
                    break;
                }
            }
            
            txtLugar.setText(programacionEditando.getLugarEvento());
            
            // Seleccionar instructor
            String numDocumentoInstructor = programacionEditando.getNumDocumento();
            if (numDocumentoInstructor != null) {
                for (int i = 0; i < cmbInstructor.getItemCount(); i++) {
                    Empleado empleado = cmbInstructor.getItemAt(i);
                    if (empleado.getNumDocumento().equals(numDocumentoInstructor)) {
                        cmbInstructor.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Configurar fecha y hora
            LocalDateTime fechaHora = programacionEditando.getFechaHoraEvento();
            spnFecha.setValue(java.sql.Date.valueOf(fechaHora.toLocalDate()));
            spnHora.setValue(fechaHora.getHour());
            spnMinutos.setValue(fechaHora.getMinute());
        }
    }
    
    private LocalDateTime construirFechaHora() {
        java.util.Date fechaSeleccionada = (java.util.Date) spnFecha.getValue();
        LocalDate fecha = new java.sql.Date(fechaSeleccionada.getTime()).toLocalDate();
        int hora = (Integer) spnHora.getValue();
        int minutos = (Integer) spnMinutos.getValue();
        
        return LocalDateTime.of(fecha, LocalTime.of(hora, minutos));
    }
    
    private void guardarProgramacion() {
        try {
            // Validaciones
            if (cmbEvento.getSelectedItem() == null) {
                ValidadorUI.mostrarAdvertencia(this, "Debe seleccionar un evento");
                return;
            }
            
            if (txtLugar.getText().trim().isEmpty()) {
                ValidadorUI.mostrarAdvertencia(this, "Debe especificar un lugar");
                return;
            }
            
            if (cmbInstructor.getSelectedItem() == null) {
                ValidadorUI.mostrarAdvertencia(this, "Debe seleccionar un instructor");
                return;
            }
            
            LocalDateTime fechaHora = construirFechaHora();
            Evento eventoSeleccionado = (Evento) cmbEvento.getSelectedItem();
            Empleado instructorSeleccionado = (Empleado) cmbInstructor.getSelectedItem();
            
            Programacion programacion = new Programacion();
            if (esEdicion) {
                programacion.setIdProgramacion(programacionEditando.getIdProgramacion());
            }
            programacion.setIdEvento(eventoSeleccionado.getIdEvento());
            programacion.setFechaHoraEvento(fechaHora);
            programacion.setLugarEvento(txtLugar.getText().trim());
            programacion.setNumDocumento(instructorSeleccionado.getNumDocumento());
            
            if (esEdicion) {
                servicioProgramacion.actualizar(programacion);
                JOptionPane.showMessageDialog(this, "Programacion actualizada correctamente");
            } else {
                servicioProgramacion.crear(programacion);
                JOptionPane.showMessageDialog(this, "Programacion creada correctamente");
            }
            
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar programacion: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}