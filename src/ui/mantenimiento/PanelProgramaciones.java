package ui.mantenimiento;

import dominio.Programacion;
import servicios.ServicioProgramacion;
import utilidades.ValidadorUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

public class PanelProgramaciones extends JPanel {
    
    private JTable tablaProgramaciones;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    
    private ServicioProgramacion servicioProgramacion;
    
    public PanelProgramaciones() {
        this.servicioProgramacion = new ServicioProgramacion();
        inicializarComponentes();
        cargarProgramaciones();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.add(new JLabel("Gestion de Programaciones de Eventos"));
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con tabla
        String[] columnas = {
            "ID", "Evento", "Fecha", "Hora", "Lugar", "Instructor", "Reservados"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProgramaciones = new JTable(modeloTabla);
        tablaProgramaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProgramaciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarProgramacion();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProgramaciones);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnNuevo = new JButton("Nueva Programacion");
        btnNuevo.addActionListener(e -> nuevaProgramacion());
        panelBotones.add(btnNuevo);
        
        btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarProgramacion());
        panelBotones.add(btnEditar);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarProgramacion());
        panelBotones.add(btnEliminar);
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarProgramaciones());
        panelBotones.add(btnActualizar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarProgramaciones() {
        try {
            modeloTabla.setRowCount(0);
            
            for (Programacion programacion : servicioProgramacion.obtenerTodas()) {
                Object[] fila = {
                    programacion.getIdProgramacion(),
                    programacion.getNombreEvento(),
                    programacion.getFechaHoraEvento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    programacion.getFechaHoraEvento().format(DateTimeFormatter.ofPattern("HH:mm")),
                    programacion.getLugarEvento(),
                    programacion.getNumDocumento() != null ? programacion.getNumDocumento() : "Sin asignar",
                    programacion.getReservados()
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar programaciones: " + e.getMessage());
        }
    }
    
    private void nuevaProgramacion() {
        try {
            DialogoProgramacion dialogo = new DialogoProgramacion(
                (Window) SwingUtilities.getWindowAncestor(this));
            dialogo.setVisible(true);
            cargarProgramaciones(); // Actualizar tabla
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al abrir dialogo: " + e.getMessage());
        }
    }
    
    private void editarProgramacion() {
        int filaSeleccionada = tablaProgramaciones.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidadorUI.mostrarAdvertencia(this, "Debe seleccionar una programacion");
            return;
        }
        
        try {
            int idProgramacion = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            Programacion programacion = servicioProgramacion.obtenerPorId(idProgramacion);
            
            if (programacion != null) {
                DialogoProgramacion dialogo = new DialogoProgramacion(
                    (Window) SwingUtilities.getWindowAncestor(this), programacion);
                dialogo.setVisible(true);
                cargarProgramaciones(); // Actualizar tabla
            }
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al editar programacion: " + e.getMessage());
        }
    }
    
    private void eliminarProgramacion() {
        int filaSeleccionada = tablaProgramaciones.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidadorUI.mostrarAdvertencia(this, "Debe seleccionar una programacion");
            return;
        }
        
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Esta seguro de que desea eliminar esta programacion?",
            "Confirmar Eliminacion", JOptionPane.YES_NO_OPTION);
            
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                int idProgramacion = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
                servicioProgramacion.eliminar(idProgramacion);
                cargarProgramaciones();
                JOptionPane.showMessageDialog(this, "Programacion eliminada correctamente");
            } catch (Exception e) {
                ValidadorUI.mostrarError(this, "Error al eliminar programacion: " + e.getMessage());
            }
        }
    }
}