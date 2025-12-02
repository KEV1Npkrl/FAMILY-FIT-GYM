package ui.socios;

import dominio.SesionUsuario;
import servicios.ServicioReserva;
import utilidades.ValidadorUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class DialogoMisReservas extends JDialog {
    
    private SesionUsuario sesion;
    private ServicioReserva servicioReserva;
    private JTable tablaReservas;
    private DefaultTableModel modeloTabla;
    private JButton btnCancelar;
    
    public DialogoMisReservas(Window parent) {
        super(parent, "Mis Reservas", Dialog.ModalityType.APPLICATION_MODAL);
        this.sesion = SesionUsuario.getInstance();
        this.servicioReserva = new ServicioReserva();
        
        inicializarComponentes();
        cargarReservas();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(getParent());
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.add(new JLabel("Reservas de " + sesion.getNombreCompleto()));
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con tabla
        String[] columnas = {
            "ID", "Evento", "Fecha", "Hora", "Lugar", "Instructor", "Estado"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaReservas = new JTable(modeloTabla);
        tablaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaReservas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    cancelarReserva();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaReservas);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnCancelar = new JButton("Cancelar Reserva");
        btnCancelar.addActionListener(e -> cancelarReserva());
        panelBotones.add(btnCancelar);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelBotones.add(btnCerrar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarReservas() {
        try {
            modeloTabla.setRowCount(0);
            
            String sql = "SELECT r.IdProgramacion, e.NombreEvento, p.FechaHoraEvento, " +
                        "p.LugarEvento, p.NumDocumento " +
                        "FROM RESERVA r " +
                        "INNER JOIN PROGRAMACION p ON r.IdProgramacion = p.IdProgramacion " +
                        "INNER JOIN EVENTOS e ON p.IdEvento = e.IdEvento " +
                        "WHERE r.NumDocumento = ? " +
                        "ORDER BY p.FechaHoraEvento";
            
            try (Connection conn = otros.Conexion.iniciarConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, sesion.getNumDocumento());
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.time.LocalDateTime fechaHora = rs.getTimestamp("FechaHoraEvento").toLocalDateTime();
                        String estado = fechaHora.isAfter(java.time.LocalDateTime.now()) ? "ACTIVA" : "FINALIZADA";
                        
                        Object[] fila = {
                            rs.getInt("IdProgramacion"),
                            rs.getString("NombreEvento"),
                            fechaHora.toLocalDate(),
                            fechaHora.toLocalTime(),
                            rs.getString("LugarEvento"),
                            rs.getString("NumDocumento") != null ? rs.getString("NumDocumento") : "Sin asignar",
                            estado
                        };
                        modeloTabla.addRow(fila);
                    }
                }
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar reservas: " + e.getMessage());
        }
    }
    
    private void cancelarReserva() {
        int filaSeleccionada = tablaReservas.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidadorUI.mostrarAdvertencia(this, "Debe seleccionar una reserva");
            return;
        }
        
        String estado = (String) modeloTabla.getValueAt(filaSeleccionada, 6);
        if ("FINALIZADA".equals(estado)) {
            ValidadorUI.mostrarAdvertencia(this, "No se puede cancelar una reserva de un evento finalizado");
            return;
        }
        
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea cancelar esta reserva?",
            "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
            
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                int idProgramacion = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
                String numDocumento = sesion.getNumDocumento();
                
                if (servicioReserva.cancelarReserva(idProgramacion, numDocumento)) {
                    JOptionPane.showMessageDialog(this, "Reserva cancelada correctamente");
                    cargarReservas(); // Actualizar tabla
                }
                
            } catch (Exception e) {
                ValidadorUI.mostrarError(this, "Error al cancelar reserva: " + e.getMessage());
            }
        }
    }
}