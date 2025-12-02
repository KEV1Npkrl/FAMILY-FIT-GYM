package ui.socios;

import dominio.SesionUsuario;
import dominio.Socio;
import dominio.Membresia;
import dominio.Plan;
import servicios.ServicioSocio;
import servicios.ServicioMembresia;
import servicios.ServicioPlan;
import seguridad.ControladorPermisos;
import utilidades.ValidadorUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.List;

/**
 * Panel para que los socios visualicen sus datos personales, membresía y plan
 * Solo lectura - sin capacidad de modificación
 */
public class PanelDatosSocio extends JPanel {
    
    private SesionUsuario sesion;
    private ServicioSocio servicioSocio;
    private ServicioMembresia servicioMembresia;
    private ServicioPlan servicioPlan;
    
    // Campos de datos personales
    private JTextField txtDocumento;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextField txtFechaRegistro;
    private JTextField txtFechaIngreso;
    
    // Campos de membresía
    private JTextField txtEstadoMembresia;
    private JTextField txtPlanActual;
    private JTextField txtFechaInicioMembresia;
    private JTextField txtFechaFinMembresia;
    private JTextField txtPrecioMembresia;
    private JTextArea txtDetallesPlan;
    
    public PanelDatosSocio() {
        this.sesion = SesionUsuario.getInstance();
        this.servicioSocio = new ServicioSocio();
        this.servicioMembresia = new ServicioMembresia();
        this.servicioPlan = new ServicioPlan();
        
        if (!ControladorPermisos.esSocio()) {
            mostrarErrorAcceso();
            return;
        }
        
        configurarPanel();
        inicializarComponentes();
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
        setBorder(BorderFactory.createTitledBorder("Mis Datos Personales"));
    }
    
    private void inicializarComponentes() {
        // Panel principal con scroll
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Panel de datos personales
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        panelPrincipal.add(crearPanelDatosPersonales(), gbc);
        
        // Panel de membresía
        gbc.gridy = 1;
        panelPrincipal.add(crearPanelMembresia(), gbc);
        
        // Panel de información del plan
        gbc.gridy = 2; gbc.weighty = 1.0;
        panelPrincipal.add(crearPanelPlan(), gbc);
        
        // Mensaje informativo
        JPanel panelInfo = new JPanel(new FlowLayout());
        JLabel lblInfo = new JLabel("<html><b>Nota:</b> Esta información es solo de consulta. " +
                                   "Para modificar sus datos, contacte con recepción.</html>");
        lblInfo.setForeground(new Color(102, 102, 102));
        panelInfo.add(lblInfo);
        
        add(panelInfo, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelDatosPersonales() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180)), 
            "Datos Personales", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            new Color(70, 130, 180)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Documento
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Documento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtDocumento = crearCampoSoloLectura();
        panel.add(txtDocumento, gbc);
        
        // Nombres
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Nombres:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombres = crearCampoSoloLectura();
        panel.add(txtNombres, gbc);
        
        // Apellidos
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtApellidos = crearCampoSoloLectura();
        panel.add(txtApellidos, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtTelefono = crearCampoSoloLectura();
        panel.add(txtTelefono, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmail = crearCampoSoloLectura();
        panel.add(txtEmail, gbc);
        
        // Fecha de Registro
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Registro:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtFechaRegistro = crearCampoSoloLectura();
        panel.add(txtFechaRegistro, gbc);
        
        // Fecha de Ingreso como Socio
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Ingreso Socio:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtFechaIngreso = crearCampoSoloLectura();
        panel.add(txtFechaIngreso, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelMembresia() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(34, 139, 34)), 
            "Mi Membresía Actual", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            new Color(34, 139, 34)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEstadoMembresia = crearCampoSoloLectura();
        panel.add(txtEstadoMembresia, gbc);
        
        // Plan
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Plan:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPlanActual = crearCampoSoloLectura();
        panel.add(txtPlanActual, gbc);
        
        // Fecha Inicio
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Inicio:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtFechaInicioMembresia = crearCampoSoloLectura();
        panel.add(txtFechaInicioMembresia, gbc);
        
        // Fecha Fin
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Vencimiento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtFechaFinMembresia = crearCampoSoloLectura();
        panel.add(txtFechaFinMembresia, gbc);
        
        // Precio
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Precio Pagado:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrecioMembresia = crearCampoSoloLectura();
        panel.add(txtPrecioMembresia, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelPlan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0)), 
            "Detalles del Plan", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            new Color(255, 140, 0)));
        
        txtDetallesPlan = new JTextArea(8, 40);
        txtDetallesPlan.setEditable(false);
        txtDetallesPlan.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDetallesPlan.setBackground(new Color(248, 248, 248));
        txtDetallesPlan.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtDetallesPlan.setLineWrap(true);
        txtDetallesPlan.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(txtDetallesPlan);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField crearCampoSoloLectura() {
        JTextField campo = new JTextField();
        campo.setEditable(false);
        campo.setBackground(new Color(248, 248, 248));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        return campo;
    }
    
    private void cargarDatosSocio() {
        try {
            String documento = sesion.getDocumentoUsuario();
            
            // Cargar datos del socio
            Optional<Socio> socioOpt = servicioSocio.obtener(documento);
            if (!socioOpt.isPresent()) {
                ValidadorUI.mostrarError(this, "No se encontraron los datos del socio");
                return;
            }
            
            Socio socio = socioOpt.get();
            
            // Llenar campos de datos personales
            txtDocumento.setText(socio.getNumDocumento());
            txtNombres.setText(socio.getNombres() != null ? socio.getNombres() : "");
            txtApellidos.setText(socio.getApellidos() != null ? socio.getApellidos() : "");
            txtTelefono.setText(socio.getTelefono() != null ? socio.getTelefono() : "");
            txtEmail.setText(socio.getEmail() != null ? socio.getEmail() : "");
            txtFechaRegistro.setText(socio.getFechaRegistro() != null ? 
                socio.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            txtFechaIngreso.setText(socio.getFechaIngreso() != null ? 
                socio.getFechaIngreso().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            
            // Cargar datos de membresía
            cargarDatosMembresia(documento);
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar datos: " + e.getMessage());
        }
    }
    
    private void cargarDatosMembresia(String documento) {
        try {
            // Obtener membresía activa
            Optional<Membresia> membresiaOpt = servicioMembresia.obtenerActiva(documento);
            
            if (!membresiaOpt.isPresent()) {
                txtEstadoMembresia.setText("Sin membresía activa");
                txtEstadoMembresia.setForeground(Color.RED);
                txtPlanActual.setText("N/A");
                txtFechaInicioMembresia.setText("N/A");
                txtFechaFinMembresia.setText("N/A");
                txtPrecioMembresia.setText("N/A");
                txtDetallesPlan.setText("No tiene membresía activa actualmente.\n\n" +
                                      "Para adquirir una nueva membresía, " +
                                      "contacte con recepción del gimnasio.");
                return;
            }
            
            Membresia membresia = membresiaOpt.get();
            
            // Verificar estado de la membresía
            if (membresia.getFechaFin().isBefore(java.time.LocalDate.now())) {
                txtEstadoMembresia.setText("VENCIDA");
                txtEstadoMembresia.setForeground(Color.RED);
            } else {
                txtEstadoMembresia.setText("ACTIVA");
                txtEstadoMembresia.setForeground(new Color(34, 139, 34));
            }
            
            // Llenar datos de membresía
            txtFechaInicioMembresia.setText(membresia.getFechaInicio()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            txtFechaFinMembresia.setText(membresia.getFechaFin()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            if (membresia.getPrecio() != null) {
                txtPrecioMembresia.setText("S/ " + String.format("%.2f", membresia.getPrecio()));
            } else {
                txtPrecioMembresia.setText("No especificado");
            }
            
            // Cargar datos del plan
            cargarDatosPlan(membresia.getIdPlan());
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar datos de membresía: " + e.getMessage());
        }
    }
    
    private void cargarDatosPlan(int idPlan) {
        try {
            Optional<Plan> planOpt = servicioPlan.obtener(idPlan);
            
            if (!planOpt.isPresent()) {
                txtPlanActual.setText("Plan no encontrado");
                txtDetallesPlan.setText("No se pudieron cargar los detalles del plan.");
                return;
            }
            
            Plan plan = planOpt.get();
            txtPlanActual.setText(plan.getNombre());
            
            // Construir descripción detallada del plan
            StringBuilder detalles = new StringBuilder();
            detalles.append("PLAN: ").append(plan.getNombre()).append("\n");
            detalles.append("═".repeat(40)).append("\n\n");
            
            detalles.append("💰 PRECIO: S/ ").append(String.format("%.2f", plan.getPrecio())).append("\n");
            detalles.append("📅 DURACIÓN: ").append(plan.getDuracionDias()).append(" días\n\n");
            
            if (plan.getDescripcion() != null && !plan.getDescripcion().trim().isEmpty()) {
                detalles.append("📋 DESCRIPCIÓN:\n");
                detalles.append(plan.getDescripcion()).append("\n\n");
            }
            
            // Agregar información específica según el plan
            agregarDetallesEspecificosPlan(detalles, plan.getNombre());
            
            detalles.append("\n📞 Para más información contacte con recepción.");
            
            txtDetallesPlan.setText(detalles.toString());
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar datos del plan: " + e.getMessage());
        }
    }
    
    private void agregarDetallesEspecificosPlan(StringBuilder detalles, String nombrePlan) {
        detalles.append("🏋️ BENEFICIOS INCLUIDOS:\n");
        
        switch (nombrePlan.toLowerCase()) {
            case "básico":
                detalles.append("• Acceso a área de cardio y pesas\n");
                detalles.append("• Horarios: Lun-Vie 6:00-22:00, Sáb-Dom 8:00-20:00\n");
                detalles.append("• Casillero incluido\n");
                detalles.append("• 1 clase grupal por semana\n");
                break;
                
            case "premium":
                detalles.append("• Acceso completo 24/7 los 7 días\n");
                detalles.append("• Todas las áreas y equipos\n");
                detalles.append("• Clases grupales ilimitadas\n");
                detalles.append("• Acceso a sauna y jacuzzi\n");
                detalles.append("• 2 sesiones de entrenamiento personal\n");
                detalles.append("• Casillero personal\n");
                detalles.append("• Invitados: 2 pases por mes\n");
                break;
                
            case "estudiante":
                detalles.append("• Acceso a área de cardio y pesas\n");
                detalles.append("• Horarios: Lun-Vie 14:00-22:00, Sáb-Dom 8:00-18:00\n");
                detalles.append("• Casillero incluido\n");
                detalles.append("• 2 clases grupales por semana\n");
                detalles.append("• Descuento especial para estudiantes\n");
                break;
                
            default:
                detalles.append("• Consultar beneficios específicos en recepción\n");
        }
        
        detalles.append("\n⚠️  RESTRICCIONES:\n");
        detalles.append("• Respetar horarios establecidos\n");
        detalles.append("• Uso obligatorio de toalla\n");
        detalles.append("• Registro de asistencia requerido\n");
        detalles.append("• Pago puntual para mantener beneficios\n");
    }
}