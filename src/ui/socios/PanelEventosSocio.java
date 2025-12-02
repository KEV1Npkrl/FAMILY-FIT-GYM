package ui.socios;

import dominio.*;
import servicios.*;
import utilidades.ValidadorUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class PanelEventosSocio extends JPanel {
    
    private SesionUsuario sesion;
    private ServicioProgramacion servicioProgramacion;
    private ServicioReserva servicioReserva;
    
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrarse;
    private JButton btnMisReservas;
    
    public PanelEventosSocio() {
        this.sesion = SesionUsuario.getInstance();
        this.servicioProgramacion = new ServicioProgramacion();
        this.servicioReserva = new ServicioReserva();
        
        inicializarComponentes();
        cargarEventosDisponibles();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.add(new JLabel("Eventos Disponibles - " + sesion.getNombreCompleto()));
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con tabla
        String[] columnas = {
            "ID", "Evento", "Dia", "Hora", "Instructor", "Registrados", "Estado", "Ya Registrado"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) return Boolean.class; // Ya Registrado
                return String.class;
            }
        };
        
        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    registrarseEnEvento();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaEventos);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnRegistrarse = new JButton("Registrarse en Evento");
        btnRegistrarse.addActionListener(e -> registrarseEnEvento());
        panelBotones.add(btnRegistrarse);
        
        btnMisReservas = new JButton("Mis Reservas");
        btnMisReservas.addActionListener(e -> mostrarMisReservas());
        panelBotones.add(btnMisReservas);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarEventosDisponibles() {
        try {
            modeloTabla.setRowCount(0);
            
            List<Programacion> programaciones = servicioProgramacion.obtenerTodas();
            String documentoSocio = sesion.getNumDocumento();
            
            for (Programacion programacion : programaciones) {
                // Solo mostrar eventos futuros
                if (programacion.getFechaHoraEvento().isAfter(LocalDateTime.now())) {
                    // Verificar si ya está registrado
                    boolean yaRegistrado = servicioReserva.yaEstaReservado(programacion.getIdProgramacion(), documentoSocio);
                    
                    // Contar reservas
                    int reservas = servicioReserva.contarReservasPorEvento(programacion.getIdProgramacion());
                    
                    // Determinar estado - sin límite de cupo máximo
                    String estado = "DISPONIBLE";
                    
                    // Agregar fila
                    Object[] fila = {
                        programacion.getIdProgramacion(),
                        programacion.getNombreEvento(),
                        programacion.getFechaHoraEvento().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")),
                        programacion.getFechaHoraEvento().format(DateTimeFormatter.ofPattern("HH:mm")),
                        programacion.getNumDocumento() != null ? programacion.getNumDocumento() : "Sin asignar",
                        reservas,
                        estado,
                        yaRegistrado
                    };
                    modeloTabla.addRow(fila);
                }
            }
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar eventos: " + e.getMessage());
        }
    }
    
    private void registrarseEnEvento() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidadorUI.mostrarAdvertencia(this, "Debe seleccionar un evento");
            return;
        }
        
        try {
            int idProgramacion = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            String estado = (String) modeloTabla.getValueAt(filaSeleccionada, 6);
            Boolean yaRegistrado = (Boolean) modeloTabla.getValueAt(filaSeleccionada, 7);
            
            if (yaRegistrado) {
                ValidadorUI.mostrarAdvertencia(this, "Ya está registrado en este evento");
                return;
            }
            
            if ("COMPLETO".equals(estado)) {
                ValidadorUI.mostrarAdvertencia(this, "Este evento ya está completo");
                return;
            }
            
            String documentoSocio = sesion.getNumDocumento();
            
            if (servicioReserva.crearReserva(idProgramacion, documentoSocio)) {
                JOptionPane.showMessageDialog(this, "Se ha registrado exitosamente en el evento");
                cargarEventosDisponibles(); // Actualizar tabla
            } else {
                ValidadorUI.mostrarError(this, "No se pudo completar el registro");
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al registrarse: " + e.getMessage());
        }
    }
    
    private void mostrarMisReservas() {
        try {
            DialogoMisReservas dialogo = new DialogoMisReservas(
                (Window) SwingUtilities.getWindowAncestor(this));
            dialogo.setVisible(true);
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al mostrar reservas: " + e.getMessage());
        }
    }
}