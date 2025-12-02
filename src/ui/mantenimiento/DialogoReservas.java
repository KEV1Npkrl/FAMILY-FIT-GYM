package ui.mantenimiento;

import servicios.ServicioReserva;
import servicios.ServicioAsistenciaEvento;
import utilidades.ValidadorUI;
import dominio.Programacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Diálogo para ver y gestionar las reservas de un evento programado
 */
public class DialogoReservas extends JDialog {
    private Connection conexion;
    private ServicioReserva servicioReserva;
    private ServicioAsistenciaEvento servicioAsistencia;
    private Programacion programacion;
    
    private JTable tablaReservas;
    private DefaultTableModel modeloTabla;
    private JButton btnMarcarAsistencia;
    private JButton btnCancelarReserva;
    private JButton btnCerrar;
    private JLabel lblInfo;

    public DialogoReservas(JFrame parent, Connection conexion, Programacion programacion) {
        super(parent, "Reservas del Evento", true);
        this.conexion = conexion;
        this.programacion = programacion;
        this.servicioReserva = new ServicioReserva();
        this.servicioAsistencia = new ServicioAsistenciaEvento();
        
        initComponents();
        cargarReservas();
        
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior con información del evento
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("Gestión de Reservas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel panelInfoEvento = new JPanel(new FlowLayout());
        panelInfoEvento.add(new JLabel("Evento: " + programacion.getNombreEvento()));
        panelInfoEvento.add(new JLabel(" | Fecha: " + formatearFecha(programacion.getFechaHoraEvento())));
        panelInfoEvento.add(new JLabel(" | Lugar: " + programacion.getLugarEvento()));
        panelSuperior.add(panelInfoEvento, BorderLayout.CENTER);
        
        lblInfo = new JLabel("Cargando reservas...");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelSuperior.add(lblInfo, BorderLayout.SOUTH);
        
        add(panelSuperior, BorderLayout.NORTH);

        // Tabla de reservas
        String[] columnas = {
            "Documento", "Socio", "Email", "Teléfono", 
            "Estado", "Asistió", "Hora Asistencia"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaReservas = new JTable(modeloTabla);
        tablaReservas.setRowHeight(30);
        tablaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaReservas.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tablaReservas);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnMarcarAsistencia = new JButton("✅ Marcar Asistencia");
        btnMarcarAsistencia.setBackground(new Color(76, 175, 80));
        btnMarcarAsistencia.setForeground(Color.WHITE);
        btnMarcarAsistencia.setFocusPainted(false);
        btnMarcarAsistencia.setEnabled(false);
        btnMarcarAsistencia.addActionListener(this::marcarAsistencia);
        panelBotones.add(btnMarcarAsistencia);
        
        btnCancelarReserva = new JButton("❌ Cancelar Reserva");
        btnCancelarReserva.setBackground(new Color(244, 67, 54));
        btnCancelarReserva.setForeground(Color.WHITE);
        btnCancelarReserva.setFocusPainted(false);
        btnCancelarReserva.setEnabled(false);
        btnCancelarReserva.addActionListener(this::cancelarReserva);
        panelBotones.add(btnCancelarReserva);
        
        panelBotones.add(Box.createHorizontalStrut(20));
        
        btnCerrar = new JButton("🚪 Cerrar");
        btnCerrar.setBackground(new Color(158, 158, 158));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);

        // Listener para selección de tabla
        tablaReservas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotones();
            }
        });
    }

    private void cargarReservas() {
        try {
            modeloTabla.setRowCount(0);
            
            String query = "SELECT S.NumDocumento, CONCAT(S.Nombres, ' ', S.Apellidos) AS NombreSocio, " +
                          "S.Email, S.Telefono, 'RESERVADO' AS Estado, " +
                          "CASE WHEN A.NumDocumento IS NOT NULL THEN 'SÍ' ELSE 'NO' END AS Asistio, " +
                          "A.FechaHoraAsis FROM RESERVA R " +
                          "INNER JOIN SOCIO S ON R.NumDocumento = S.NumDocumento " +
                          "LEFT JOIN ASISTENCIAEVENTO A ON R.IdProgramacion = A.IdProgramacion " +
                          "AND R.NumDocumento = A.NumDocumento " +
                          "WHERE R.IdProgramacion = ? ORDER BY S.Apellidos, S.Nombres";

            try (var stmt = conexion.prepareStatement(query)) {
                stmt.setInt(1, programacion.getIdProgramacion());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    int totalReservas = 0;
                    int totalAsistencias = 0;
                    
                    while (rs.next()) {
                        Object[] fila = {
                            rs.getString("NumDocumento"),
                            rs.getString("NombreSocio"),
                            rs.getString("Email"),
                            rs.getString("Telefono"),
                            rs.getString("Estado"),
                            rs.getString("Asistio"),
                            formatearFechaAsistencia(rs.getTimestamp("FechaHoraAsis"))
                        };
                        modeloTabla.addRow(fila);
                        
                        totalReservas++;
                        if ("SÍ".equals(rs.getString("Asistio"))) {
                            totalAsistencias++;
                        }
                    }
                    
                    actualizarInformacion(totalReservas, totalAsistencias);
                }
            }

        } catch (SQLException e) {
            ValidadorUI.mostrarError(this, "Error al cargar reservas: " + e.getMessage());
        }
    }

    private String formatearFecha(LocalDateTime fechaHora) {
        if (fechaHora == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaHora.format(formatter);
    }

    private String formatearFechaAsistencia(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "";
        LocalDateTime fechaHora = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaHora.format(formatter);
    }

    private void actualizarInformacion(int totalReservas, int totalAsistencias) {
        double porcentaje = totalReservas > 0 ? (totalAsistencias * 100.0 / totalReservas) : 0;
        lblInfo.setText(String.format(
            "Total reservas: %d | Asistencias: %d | Porcentaje asistencia: %.1f%%",
            totalReservas, totalAsistencias, porcentaje
        ));
    }

    private void actualizarEstadoBotones() {
        int selectedRow = tablaReservas.getSelectedRow();
        
        if (selectedRow != -1) {
            String asistio = (String) modeloTabla.getValueAt(selectedRow, 5);
            
            // Habilitar marcar asistencia solo si no ha asistido
            btnMarcarAsistencia.setEnabled("NO".equals(asistio));
            
            // Habilitar cancelar reserva (siempre disponible)
            btnCancelarReserva.setEnabled(true);
        } else {
            btnMarcarAsistencia.setEnabled(false);
            btnCancelarReserva.setEnabled(false);
        }
    }

    private void marcarAsistencia(java.awt.event.ActionEvent e) {
        int selectedRow = tablaReservas.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            String numDocumento = (String) modeloTabla.getValueAt(selectedRow, 0);
            String nombreSocio = (String) modeloTabla.getValueAt(selectedRow, 1);

            int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Confirma marcar la asistencia de " + nombreSocio + "?",
                "Confirmar Asistencia",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                servicioAsistencia.marcarAsistencia(numDocumento, programacion.getIdProgramacion());
                ValidadorUI.mostrarExito(this, "Asistencia marcada exitosamente");
                cargarReservas(); // Actualizar tabla
            }

        } catch (Exception ex) {
            ValidadorUI.mostrarError(this, "Error al marcar asistencia: " + ex.getMessage());
        }
    }

    private void cancelarReserva(java.awt.event.ActionEvent e) {
        int selectedRow = tablaReservas.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            String numDocumento = (String) modeloTabla.getValueAt(selectedRow, 0);
            String nombreSocio = (String) modeloTabla.getValueAt(selectedRow, 1);

            int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de cancelar la reserva de " + nombreSocio + "?\n\n" +
                "Esta acción no se puede deshacer.",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                servicioReserva.cancelarReserva(programacion.getIdProgramacion(), numDocumento);
                ValidadorUI.mostrarExito(this, "Reserva cancelada exitosamente");
                cargarReservas(); // Actualizar tabla
            }

        } catch (Exception ex) {
            ValidadorUI.mostrarError(this, "Error al cancelar reserva: " + ex.getMessage());
        }
    }
}