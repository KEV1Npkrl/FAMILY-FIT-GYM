package ui.socios;

import dominio.*;
import servicios.*;
import seguridad.ControladorPermisos;
import utilidades.ValidadorUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Panel para que los socios se registren en eventos programados
 * Valida restricciones según el plan de membresía
 */
public class PanelEventosSocio extends JPanel {
    
    private SesionUsuario sesion;
    private ServicioEvento servicioEvento;
    private ServicioMembresia servicioMembresia;
    private ServicioPlan servicioPlan;
    private ServicioAsistenciaEvento servicioAsistenciaEvento;
    
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    private JTextField txtFiltroNombre;
    private JComboBox<String> cmbFiltroDia;
    private JButton btnRegistrarse;
    private JButton btnCancelarRegistro;
    private JLabel lblInfoMembresia;
    private JLabel lblEventosRegistrados;
    
    public PanelEventosSocio() {
        this.sesion = SesionUsuario.getInstance();
        this.servicioEvento = new ServicioEvento();
        this.servicioMembresia = new ServicioMembresia();
        this.servicioPlan = new ServicioPlan();
        this.servicioAsistenciaEvento = new ServicioAsistenciaEvento();
        
        if (!ControladorPermisos.esSocio()) {
            mostrarErrorAcceso();
            return;
        }
        
        configurarPanel();
        inicializarComponentes();
        configurarEventos();
        cargarEventos();
        actualizarInfoMembresia();
    }
    
    private void mostrarErrorAcceso() {
        setLayout(new BorderLayout());
        JLabel lblError = new JLabel("Esta funcionalidad es solo para socios.", 
                                    SwingConstants.CENTER);
        lblError.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblError.setForeground(Color.RED);
        add(lblError, BorderLayout.CENTER);
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Registro en Eventos Programados"));
    }
    
    private void inicializarComponentes() {
        // Panel superior - Filtros e información
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - Tabla de eventos
        JPanel panelCentral = crearPanelTabla();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior - Botones
        JPanel panelInferior = crearPanelBotones();
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de información de membresía
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(34, 139, 34)), 
            "Mi Membresía", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            new Color(34, 139, 34)));
        
        lblInfoMembresia = new JLabel("Cargando información de membresía...", SwingConstants.CENTER);
        lblInfoMembresia.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panelInfo.add(lblInfoMembresia, BorderLayout.CENTER);
        
        lblEventosRegistrados = new JLabel("Eventos registrados: 0", SwingConstants.CENTER);
        lblEventosRegistrados.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEventosRegistrados.setForeground(new Color(70, 130, 180));
        panelInfo.add(lblEventosRegistrados, BorderLayout.SOUTH);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        
        panelFiltros.add(new JLabel("Evento:"));
        txtFiltroNombre = new JTextField(15);
        panelFiltros.add(txtFiltroNombre);
        
        panelFiltros.add(new JLabel("Día:"));
        cmbFiltroDia = new JComboBox<>(new String[]{
            "Todos", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        });
        panelFiltros.add(cmbFiltroDia);
        
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        panelFiltros.add(btnFiltrar);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panelFiltros.add(btnLimpiar);
        
        panel.add(panelInfo, BorderLayout.NORTH);
        panel.add(panelFiltros, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Eventos Disponibles"));
        
        // Crear modelo de tabla
        String[] columnas = {"ID", "Evento", "Día", "Hora", "Instructor", "Cupo Máx", "Inscritos", "Estado", "Registrado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 8) return Boolean.class; // Columna "Registrado"
                return String.class;
            }
        };
        
        // Crear tabla
        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.setRowHeight(25);
        tablaEventos.getTableHeader().setReorderingAllowed(false);
        
        // Configurar columnas
        tablaEventos.getColumnModel().getColumn(0).setMaxWidth(50); // ID
        tablaEventos.getColumnModel().getColumn(3).setMaxWidth(80); // Hora
        tablaEventos.getColumnModel().getColumn(5).setMaxWidth(70); // Cupo Máx
        tablaEventos.getColumnModel().getColumn(6).setMaxWidth(70); // Inscritos
        tablaEventos.getColumnModel().getColumn(7).setMaxWidth(80); // Estado
        tablaEventos.getColumnModel().getColumn(8).setMaxWidth(80); // Registrado
        
        // Ordenamiento
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaEventos.setRowSorter(sorter);
        
        // Scroll
        JScrollPane scrollPane = new JScrollPane(tablaEventos);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());
        
        btnRegistrarse = new JButton("Registrarse en Evento");
        btnRegistrarse.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRegistrarse.setBackground(new Color(34, 139, 34));
        btnRegistrarse.setForeground(Color.WHITE);
        btnRegistrarse.setPreferredSize(new Dimension(180, 35));
        btnRegistrarse.setEnabled(false);
        
        btnCancelarRegistro = new JButton("Cancelar Registro");
        btnCancelarRegistro.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCancelarRegistro.setBackground(new Color(220, 20, 60));
        btnCancelarRegistro.setForeground(Color.WHITE);
        btnCancelarRegistro.setPreferredSize(new Dimension(150, 35));
        btnCancelarRegistro.setEnabled(false);
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setPreferredSize(new Dimension(100, 35));
        btnActualizar.addActionListener(e -> {
            cargarEventos();
            actualizarInfoMembresia();
        });
        
        panel.add(btnRegistrarse);
        panel.add(btnCancelarRegistro);
        panel.add(btnActualizar);
        
        return panel;
    }
    
    private void configurarEventos() {
        // Selección en tabla
        tablaEventos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotones();
            }
        });
        
        // Botón registrarse
        btnRegistrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarseEnEvento();
            }
        });
        
        // Botón cancelar registro
        btnCancelarRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarRegistro();
            }
        });
    }
    
    private void cargarEventos() {
        try {
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            // Cargar eventos activos
            List<Evento> eventos = servicioEvento.listarActivos();
            String documentoSocio = sesion.getDocumentoUsuario();
            
            for (Evento evento : eventos) {
                // Verificar si el socio está registrado en este evento
                boolean yaRegistrado = servicioAsistenciaEvento.estaRegistrado(documentoSocio, evento.getId());
                
                // Contar inscritos
                int inscritos = servicioAsistenciaEvento.contarInscritos(evento.getId());
                
                // Determinar estado
                String estado;
                if (inscritos >= evento.getCupoMaximo()) {
                    estado = "COMPLETO";
                } else if (evento.getFechaHora().isBefore(LocalDateTime.now())) {
                    estado = "PASADO";
                } else {
                    estado = "DISPONIBLE";
                }
                
                // Agregar fila
                Object[] fila = {
                    evento.getId(),
                    evento.getNombre(),
                    evento.getFechaHora().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")),
                    evento.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")),
                    evento.getInstructor() != null ? evento.getInstructor() : "Sin asignar",
                    evento.getCupoMaximo(),
                    inscritos,
                    estado,
                    yaRegistrado
                };
                
                modeloTabla.addRow(fila);
            }
            
            // Actualizar contador de eventos registrados
            actualizarContadorEventos();
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar eventos: " + e.getMessage());
        }
    }
    
    private void actualizarInfoMembresia() {
        try {
            String documento = sesion.getDocumentoUsuario();
            Optional<Membresia> membresiaOpt = servicioMembresia.obtenerActiva(documento);
            
            if (!membresiaOpt.isPresent()) {
                lblInfoMembresia.setText("⚠️ Sin membresía activa - No puede registrarse en eventos");
                lblInfoMembresia.setForeground(Color.RED);
                return;
            }
            
            Membresia membresia = membresiaOpt.get();
            Optional<Plan> planOpt = servicioPlan.obtener(membresia.getIdPlan());
            
            if (!planOpt.isPresent()) {
                lblInfoMembresia.setText("⚠️ Plan no encontrado");
                lblInfoMembresia.setForeground(Color.RED);
                return;
            }
            
            Plan plan = planOpt.get();
            String estado = membresia.getFechaFin().isBefore(LocalDate.now()) ? "VENCIDA" : "ACTIVA";
            
            String info = String.format("Plan: %s | Estado: %s | Vence: %s", 
                plan.getNombre(), estado, 
                membresia.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            lblInfoMembresia.setText(info);
            lblInfoMembresia.setForeground(estado.equals("ACTIVA") ? new Color(34, 139, 34) : Color.RED);
            
        } catch (Exception e) {
            lblInfoMembresia.setText("Error al cargar información de membresía");
            lblInfoMembresia.setForeground(Color.RED);
        }
    }
    
    private void actualizarContadorEventos() {
        try {
            String documento = sesion.getDocumentoUsuario();
            int registrados = servicioAsistenciaEvento.contarEventosRegistrados(documento);
            lblEventosRegistrados.setText("Eventos registrados: " + registrados);
        } catch (Exception e) {
            lblEventosRegistrados.setText("Error al contar eventos");
        }
    }
    
    private void actualizarEstadoBotones() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            btnRegistrarse.setEnabled(false);
            btnCancelarRegistro.setEnabled(false);
            return;
        }
        
        // Obtener datos de la fila seleccionada
        boolean yaRegistrado = (Boolean) tablaEventos.getValueAt(filaSeleccionada, 8);
        String estado = (String) tablaEventos.getValueAt(filaSeleccionada, 7);
        
        // Habilitar botones según estado
        btnRegistrarse.setEnabled(!yaRegistrado && estado.equals("DISPONIBLE"));
        btnCancelarRegistro.setEnabled(yaRegistrado);
    }
    
    private void aplicarFiltros() {
        // TODO: Implementar filtros específicos
        cargarEventos();
    }
    
    private void limpiarFiltros() {
        txtFiltroNombre.setText("");
        cmbFiltroDia.setSelectedIndex(0);
        cargarEventos();
    }
    
    /**
     * Registrar al socio en el evento seleccionado
     */
    private void registrarseEnEvento() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidadorUI.mostrarError(this, "Debe seleccionar un evento");
            return;
        }
        
        try {
            // Obtener datos del evento
            int idEvento = (Integer) tablaEventos.getValueAt(filaSeleccionada, 0);
            String nombreEvento = (String) tablaEventos.getValueAt(filaSeleccionada, 1);
            String documentoSocio = sesion.getDocumentoUsuario();
            
            // Verificar membresía activa
            Optional<Membresia> membresiaOpt = servicioMembresia.obtenerActiva(documentoSocio);
            if (!membresiaOpt.isPresent()) {
                ValidadorUI.mostrarError(this, "No tiene una membresía activa para registrarse en eventos");
                return;
            }
            
            Membresia membresia = membresiaOpt.get();
            Optional<Plan> planOpt = servicioPlan.obtener(membresia.getIdPlan());
            if (!planOpt.isPresent()) {
                ValidadorUI.mostrarError(this, "No se pudo verificar su plan");
                return;
            }
            
            Plan plan = planOpt.get();
            
            // Verificar si puede registrarse en más eventos según su plan
            if (!servicioAsistenciaEvento.puedeRegistrarseEnMasEventos(documentoSocio, plan.getNombre())) {
                String mensaje = String.format(
                    "Ha alcanzado el límite de eventos para su plan '%s'.\n\n" +
                    "Límites por plan:\n" +
                    "• Básico: 2 eventos por mes\n" +
                    "• Estudiante: 4 eventos por mes\n" +
                    "• Premium: Sin límite\n\n" +
                    "Para registrarse en más eventos, considere actualizar su plan.",
                    plan.getNombre()
                );
                ValidadorUI.mostrarError(this, mensaje);
                return;
            }
            
            // Confirmar registro
            String mensaje = String.format(
                "¿Desea registrarse en el evento '%s'?\n\n" +
                "Plan: %s\n" +
                "Socio: %s\n\n" +
                "Una vez registrado, debe asistir al evento en el horario programado.",
                nombreEvento, plan.getNombre(), sesion.getNombreUsuario()
            );
            
            int opcion = JOptionPane.showConfirmDialog(
                this,
                mensaje,
                "Confirmar Registro en Evento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Realizar registro
            if (servicioAsistenciaEvento.registrarEnEvento(documentoSocio, idEvento)) {
                ValidadorUI.mostrarExito(this, "Se registró exitosamente en el evento '" + nombreEvento + "'");
                cargarEventos(); // Refrescar tabla
                actualizarInfoMembresia(); // Actualizar contador
            } else {
                ValidadorUI.mostrarError(this, "Error al registrarse en el evento");
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error inesperado: " + e.getMessage());
        }
    }
    
    /**
     * Cancelar el registro del socio en el evento seleccionado
     */
    private void cancelarRegistro() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidadorUI.mostrarError(this, "Debe seleccionar un evento");
            return;
        }
        
        try {
            // Obtener datos del evento
            int idEvento = (Integer) tablaEventos.getValueAt(filaSeleccionada, 0);
            String nombreEvento = (String) tablaEventos.getValueAt(filaSeleccionada, 1);
            String documentoSocio = sesion.getDocumentoUsuario();
            
            // Confirmar cancelación
            String mensaje = String.format(
                "¿Está seguro de cancelar su registro en el evento '%s'?\n\n" +
                "Esta acción no se puede deshacer.\n" +
                "Si el evento ya comenzó, podría aplicar penalizaciones.",
                nombreEvento
            );
            
            int opcion = JOptionPane.showConfirmDialog(
                this,
                mensaje,
                "Confirmar Cancelación de Registro",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Realizar cancelación
            if (servicioAsistenciaEvento.cancelarRegistro(documentoSocio, idEvento)) {
                ValidadorUI.mostrarExito(this, "Se canceló su registro en el evento '" + nombreEvento + "'");
                cargarEventos(); // Refrescar tabla
                actualizarInfoMembresia(); // Actualizar contador
            } else {
                ValidadorUI.mostrarError(this, "Error al cancelar el registro");
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error inesperado: " + e.getMessage());
        }
    }
}