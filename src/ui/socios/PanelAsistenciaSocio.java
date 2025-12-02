package ui.socios;

import dominio.SesionUsuario;
import dominio.Membresia;
import dominio.Plan;
import servicios.ServicioMembresia;
import servicios.ServicioPlan;
import seguridad.ControladorPermisos;
import utilidades.ValidadorUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Panel para que los socios marquen su asistencia
 * Incluye validaciones de horarios y restricciones según el plan
 */
public class PanelAsistenciaSocio extends JPanel {
    
    private SesionUsuario sesion;
    private ServicioMembresia servicioMembresia;
    private ServicioPlan servicioPlan;
    private JLabel lblInfoSocio;
    private JLabel lblFechaActual;
    private JLabel lblHoraActual;
    private JLabel lblEstadoMembresia;
    private JLabel lblPlanActual;
    private JLabel lblRestriccionesHorario;
    private JButton btnMarcarAsistencia;
    private JTextArea txtHistorialAsistencias;
    private Timer reloj;
    
    public PanelAsistenciaSocio() {
        this.sesion = SesionUsuario.getInstance();
        this.servicioMembresia = new ServicioMembresia();
        this.servicioPlan = new ServicioPlan();
        
        if (!ControladorPermisos.esSocio()) {
            mostrarErrorAcceso();
            return;
        }
        
        configurarPanel();
        inicializarComponentes();
        configurarEventos();
        inicializarReloj();
        cargarDatosSocio();
    }
    
    private void mostrarErrorAcceso() {
        setLayout(new BorderLayout());
        JLabel lblError = new JLabel("Esta funcionalidad es solo para socios logueados.", 
                                    SwingConstants.CENTER);
        lblError.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblError.setForeground(Color.RED);
        add(lblError, BorderLayout.CENTER);
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Registro de Asistencia - Socio"));
    }
    
    private void inicializarComponentes() {
        // Panel superior con información del socio
        JPanel panelInfo = crearPanelInformacion();
        add(panelInfo, BorderLayout.NORTH);
        
        // Panel central con botón de marcar asistencia
        JPanel panelAsistencia = crearPanelAsistencia();
        add(panelAsistencia, BorderLayout.CENTER);
        
        // Panel inferior con historial
        JPanel panelHistorial = crearPanelHistorial();
        add(panelHistorial, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Información del Socio"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Información del socio
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Socio:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        lblInfoSocio = new JLabel(sesion.getNombresCompletos() + " (" + sesion.getDocumentoUsuario() + ")");
        lblInfoSocio.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(lblInfoSocio, gbc);
        
        // Fecha actual
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Fecha:"), gbc);
        
        gbc.gridx = 1;
        lblFechaActual = new JLabel();
        panel.add(lblFechaActual, gbc);
        
        // Hora actual
        gbc.gridx = 2;
        lblHoraActual = new JLabel();
        lblHoraActual.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblHoraActual.setForeground(new Color(0, 102, 204));
        panel.add(lblHoraActual, gbc);
        
        // Estado de membresía
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Membresía:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        lblEstadoMembresia = new JLabel();
        panel.add(lblEstadoMembresia, gbc);
        
        // Plan actual
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(new JLabel("Plan:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        lblPlanActual = new JLabel();
        panel.add(lblPlanActual, gbc);
        
        // Restricciones de horario
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Horarios:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        lblRestriccionesHorario = new JLabel();
        panel.add(lblRestriccionesHorario, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelAsistencia() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Marcar Asistencia"));
        
        btnMarcarAsistencia = new JButton("Marcar Mi Asistencia");
        btnMarcarAsistencia.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnMarcarAsistencia.setPreferredSize(new Dimension(200, 50));
        btnMarcarAsistencia.setBackground(new Color(46, 125, 50));
        btnMarcarAsistencia.setForeground(Color.WHITE);
        
        panel.add(btnMarcarAsistencia);
        
        return panel;
    }
    
    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Mis Últimas Asistencias"));
        
        txtHistorialAsistencias = new JTextArea(6, 40);
        txtHistorialAsistencias.setEditable(false);
        txtHistorialAsistencias.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(txtHistorialAsistencias);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void configurarEventos() {
        btnMarcarAsistencia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                marcarAsistencia();
            }
        });
    }
    
    private void inicializarReloj() {
        reloj = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarFechaHora();
            }
        });
        reloj.start();
        actualizarFechaHora();
    }
    
    private void actualizarFechaHora() {
        LocalDate fechaActual = LocalDate.now();
        LocalTime horaActual = LocalTime.now();
        
        lblFechaActual.setText(fechaActual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblHoraActual.setText(horaActual.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    private void cargarDatosSocio() {
        try {
            // Obtener membresía activa
            Optional<Membresia> membresiaOpt = servicioMembresia.obtenerActiva(sesion.getDocumentoUsuario());
            
            if (!membresiaOpt.isPresent()) {
                lblEstadoMembresia.setText("Sin membresía activa");
                lblEstadoMembresia.setForeground(Color.RED);
                lblPlanActual.setText("N/A");
                lblRestriccionesHorario.setText("No puede marcar asistencia");
                btnMarcarAsistencia.setEnabled(false);
                return;
            }
            
            Membresia membresia = membresiaOpt.get();
            
            // Verificar si está vencida
            if (membresia.getFechaFin().isBefore(LocalDate.now())) {
                lblEstadoMembresia.setText("Membresía vencida el " + 
                    membresia.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                lblEstadoMembresia.setForeground(Color.RED);
                btnMarcarAsistencia.setEnabled(false);
                return;
            }
            
            // Membresía activa
            lblEstadoMembresia.setText("Activa hasta " + 
                membresia.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            lblEstadoMembresia.setForeground(new Color(46, 125, 50));
            
            // Obtener información del plan
            Optional<Plan> planOpt = servicioPlan.obtener(membresia.getIdPlan());
            if (planOpt.isPresent()) {
                Plan plan = planOpt.get();
                lblPlanActual.setText(plan.getNombre() + " - $" + plan.getPrecio());
                
                // Mostrar restricciones de horario según el plan
                mostrarRestriccionesPlan(plan);
                
                // Validar si puede marcar asistencia en este momento
                validarHorarioAsistencia(plan);
            } else {
                lblPlanActual.setText("Plan no encontrado");
                btnMarcarAsistencia.setEnabled(false);
            }
            
            // Cargar historial de asistencias
            cargarHistorialAsistencias();
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar datos del socio: " + e.getMessage());
        }
    }
    
    private void mostrarRestriccionesPlan(Plan plan) {
        String restricciones = "Sin restricciones especiales";
        
        // Aquí puedes agregar lógica específica según el plan
        // Por ejemplo, diferentes horarios para diferentes planes
        switch (plan.getNombre().toLowerCase()) {
            case "básico":
                restricciones = "Lun-Vie: 6:00-22:00, Sáb-Dom: 8:00-20:00";
                break;
            case "premium":
                restricciones = "Todos los días: 24 horas";
                break;
            case "estudiante":
                restricciones = "Lun-Vie: 14:00-22:00, Sáb-Dom: 8:00-18:00";
                break;
            default:
                restricciones = "Consultar horarios en recepción";
        }
        
        lblRestriccionesHorario.setText(restricciones);
    }
    
    private void validarHorarioAsistencia(Plan plan) {
        LocalTime horaActual = LocalTime.now();
        LocalDate fechaActual = LocalDate.now();
        boolean puedeMarcar = true;
        String razon = "";
        
        // Validaciones básicas de horario según el plan
        switch (plan.getNombre().toLowerCase()) {
            case "básico":
                if (fechaActual.getDayOfWeek().getValue() <= 5) { // Lunes a Viernes
                    if (horaActual.isBefore(LocalTime.of(6, 0)) || 
                        horaActual.isAfter(LocalTime.of(22, 0))) {
                        puedeMarcar = false;
                        razon = "Horario permitido: 6:00-22:00";
                    }
                } else { // Sábado y Domingo
                    if (horaActual.isBefore(LocalTime.of(8, 0)) || 
                        horaActual.isAfter(LocalTime.of(20, 0))) {
                        puedeMarcar = false;
                        razon = "Horario de fin de semana: 8:00-20:00";
                    }
                }
                break;
                
            case "estudiante":
                if (fechaActual.getDayOfWeek().getValue() <= 5) { // Lunes a Viernes
                    if (horaActual.isBefore(LocalTime.of(14, 0)) || 
                        horaActual.isAfter(LocalTime.of(22, 0))) {
                        puedeMarcar = false;
                        razon = "Horario de estudiante: 14:00-22:00";
                    }
                } else { // Sábado y Domingo
                    if (horaActual.isBefore(LocalTime.of(8, 0)) || 
                        horaActual.isAfter(LocalTime.of(18, 0))) {
                        puedeMarcar = false;
                        razon = "Horario de fin de semana: 8:00-18:00";
                    }
                }
                break;
                
            case "premium":
                // Sin restricciones de horario
                break;
                
            default:
                // Por defecto, horario normal
                if (horaActual.isBefore(LocalTime.of(6, 0)) || 
                    horaActual.isAfter(LocalTime.of(23, 0))) {
                    puedeMarcar = false;
                    razon = "Horario general: 6:00-23:00";
                }
        }
        
        if (!puedeMarcar) {
            btnMarcarAsistencia.setEnabled(false);
            btnMarcarAsistencia.setText("Fuera de horario");
            btnMarcarAsistencia.setBackground(Color.GRAY);
            btnMarcarAsistencia.setToolTipText(razon);
        } else {
            btnMarcarAsistencia.setEnabled(true);
            btnMarcarAsistencia.setText("Marcar Mi Asistencia");
            btnMarcarAsistencia.setBackground(new Color(46, 125, 50));
            btnMarcarAsistencia.setToolTipText("Hacer clic para registrar asistencia");
        }
    }
    
    private void cargarHistorialAsistencias() {
        // Placeholder - aquí cargarías las últimas asistencias del socio
        txtHistorialAsistencias.setText(
            "HISTORIAL DE ASISTENCIAS\n" +
            "========================\n" +
            "01/12/2025 - 08:30 - Entrada registrada\n" +
            "30/11/2025 - 19:45 - Entrada registrada\n" +
            "29/11/2025 - 07:15 - Entrada registrada\n" +
            "28/11/2025 - 18:20 - Entrada registrada\n" +
            "27/11/2025 - 09:00 - Entrada registrada\n\n" +
            "Total asistencias este mes: 15"
        );
    }
    
    private void marcarAsistencia() {
        try {
            // Confirmar la acción
            int opcion = JOptionPane.showConfirmDialog(this,
                "¿Confirma que desea marcar su asistencia?\n" +
                "Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "Hora: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                "Confirmar Asistencia",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Aquí implementarías la lógica para guardar la asistencia en la BD
            // Por ahora, simulamos el registro exitoso
            
            JOptionPane.showMessageDialog(this,
                "¡Asistencia registrada exitosamente!\n" +
                "Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "Hora: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                "Asistencia Registrada",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Recargar historial
            cargarHistorialAsistencias();
            
            // Deshabilitar el botón por un tiempo para evitar registros múltiples
            btnMarcarAsistencia.setEnabled(false);
            btnMarcarAsistencia.setText("Asistencia Registrada");
            
            Timer timer = new Timer(30000, e -> { // 30 segundos
                btnMarcarAsistencia.setEnabled(true);
                btnMarcarAsistencia.setText("Marcar Mi Asistencia");
            });
            timer.setRepeats(false);
            timer.start();
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al registrar asistencia: " + e.getMessage());
        }
    }
    
    /**
     * Limpia recursos al cerrar el panel
     */
    public void cerrarPanel() {
        if (reloj != null) {
            reloj.stop();
        }
    }
}