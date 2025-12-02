package ui.mantenimiento;

import dominio.Evento;
import servicios.ServicioEvento;
import seguridad.ControladorPermisos;
import utilidades.ValidadorUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel para que los entrenadores y administradores gestionen eventos programados
 */
public class PanelEventos extends JPanel {
    
    private ServicioEvento servicioEvento;
    private JTable tablaEventos;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    
    public PanelEventos() {
        this.servicioEvento = new ServicioEvento();
        
        if (!ControladorPermisos.puedeGestionarEventos()) {
            mostrarErrorAcceso();
            return;
        }
        
        configurarPanel();
        inicializarComponentes();
        configurarEventos();
        cargarEventos();
    }
    
    private void mostrarErrorAcceso() {
        setLayout(new BorderLayout());
        JLabel lblError = new JLabel("<html><div style='text-align: center;'>" +
            "<h3>Acceso Denegado</h3>" +
            "Solo los entrenadores y administradores pueden gestionar eventos.<br>" +
            "Contacta con un administrador si necesitas acceso." +
            "</div></html>", SwingConstants.CENTER);
        lblError.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblError.setForeground(Color.RED);
        add(lblError, BorderLayout.CENTER);
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Gestión de Eventos Programados"));
    }
    
    private void inicializarComponentes() {
        // Panel superior con botones
        JPanel panelSuperior = crearPanelBotones();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con tabla
        JPanel panelCentral = crearPanelTabla();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con información
        JPanel panelInferior = crearPanelInfo();
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnNuevo = new JButton("Nuevo Evento");
        btnNuevo.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnNuevo.setBackground(new Color(34, 139, 34));
        btnNuevo.setForeground(Color.BLACK);
        btnNuevo.setPreferredSize(new Dimension(130, 35));
        
        btnEditar = new JButton("Editar");
        btnEditar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnEditar.setBackground(new Color(255, 165, 0));
        btnEditar.setForeground(Color.BLACK);
        btnEditar.setPreferredSize(new Dimension(100, 35));
        btnEditar.setEnabled(false);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnEliminar.setBackground(new Color(220, 20, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setPreferredSize(new Dimension(100, 35));
        btnEliminar.setEnabled(false);
        
        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnRefrescar.setBackground(new Color(70, 130, 180));
        btnRefrescar.setForeground(Color.BLACK);
        btnRefrescar.setPreferredSize(new Dimension(100, 35));
        
        panel.add(btnNuevo);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnRefrescar);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Eventos Programados"));
        
        // Crear modelo de tabla
        String[] columnas = {"ID", "Nombre del Evento", "Fecha y Hora", "Duración (min)", 
                            "Instructor", "Cupo Máximo", "Descripción", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable directamente
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 3 || column == 5) return Integer.class;
                return String.class;
            }
        };
        
        // Crear tabla
        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Configurar ancho de columnas
        tablaEventos.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaEventos.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        tablaEventos.getColumnModel().getColumn(2).setPreferredWidth(130); // Fecha/Hora
        tablaEventos.getColumnModel().getColumn(3).setPreferredWidth(80);  // Duración
        tablaEventos.getColumnModel().getColumn(4).setPreferredWidth(120); // Instructor
        tablaEventos.getColumnModel().getColumn(5).setPreferredWidth(80);  // Cupo
        tablaEventos.getColumnModel().getColumn(6).setPreferredWidth(200); // Descripción
        tablaEventos.getColumnModel().getColumn(7).setPreferredWidth(80);  // Estado
        
        // Scroll
        JScrollPane scrollPane = new JScrollPane(tablaEventos);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        JLabel lblInfo = new JLabel("💡 Tip: Selecciona un evento para editarlo o eliminarlo. " +
            "Solo los eventos futuros pueden ser modificados.");
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        panel.add(lblInfo);
        
        return panel;
    }
    
    private void configurarEventos() {
        // Botón nuevo evento
        btnNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearNuevoEvento();
            }
        });
        
        // Botón editar
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarEventoSeleccionado();
            }
        });
        
        // Botón eliminar
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarEventoSeleccionado();
            }
        });
        
        // Botón refrescar
        btnRefrescar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarEventos();
            }
        });
        
        // Selección en tabla
        tablaEventos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotones();
            }
        });
    }
    
    private void actualizarEstadoBotones() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        boolean haySeleccion = filaSeleccionada >= 0;
        
        btnEditar.setEnabled(haySeleccion);
        btnEliminar.setEnabled(haySeleccion);
    }
    
    private void cargarEventos() {
        try {
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            // Cargar todos los eventos
            List<Evento> eventos = servicioEvento.listarTodos();
            
            for (Evento evento : eventos) {
                Object[] fila = {
                    evento.getIdEvento(),
                    evento.getNombreEvento(),
                    evento.getDescripcion() != null ? evento.getDescripcion() : "Sin descripción"
                };
                
                modeloTabla.addRow(fila);
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar eventos: " + e.getMessage());
        }
    }
    
    private void crearNuevoEvento() {
        DialogoEvento dialogo = new DialogoEvento(SwingUtilities.getWindowAncestor(this), "Nuevo Evento");
        dialogo.setVisible(true);
        
        if (dialogo.isAceptado()) {
            try {
                Evento nuevoEvento = dialogo.getEvento();
                
                if (servicioEvento.crear(nuevoEvento)) {
                    ValidadorUI.mostrarExito(this, "Evento creado exitosamente");
                    cargarEventos(); // Refrescar tabla
                } else {
                    ValidadorUI.mostrarError(this, "Error al crear el evento");
                }
                
            } catch (Exception e) {
                ValidadorUI.mostrarError(this, "Error al crear evento: " + e.getMessage());
            }
        }
    }
    
    private void editarEventoSeleccionado() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada < 0) {
            ValidadorUI.mostrarError(this, "Selecciona un evento para editar");
            return;
        }
        
        try {
            int idEvento = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            var eventoOpt = servicioEvento.obtener(idEvento);
            
            if (eventoOpt.isPresent()) {
                Evento evento = eventoOpt.get();
                
                DialogoEvento dialogo = new DialogoEvento(SwingUtilities.getWindowAncestor(this), "Editar Evento");
                dialogo.setEvento(evento);
                dialogo.setVisible(true);
                
                if (dialogo.isAceptado()) {
                    Evento eventoEditado = dialogo.getEvento();
                    eventoEditado.setIdEvento(idEvento); // Mantener ID original
                    
                    if (servicioEvento.actualizar(eventoEditado)) {
                        ValidadorUI.mostrarExito(this, "Evento actualizado exitosamente");
                        cargarEventos(); // Refrescar tabla
                    } else {
                        ValidadorUI.mostrarError(this, "Error al actualizar el evento");
                    }
                }
            } else {
                ValidadorUI.mostrarError(this, "No se pudo cargar el evento seleccionado");
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al editar evento: " + e.getMessage());
        }
    }
    
    private void eliminarEventoSeleccionado() {
        int filaSeleccionada = tablaEventos.getSelectedRow();
        if (filaSeleccionada < 0) {
            ValidadorUI.mostrarError(this, "Selecciona un evento para eliminar");
            return;
        }
        
        try {
            int idEvento = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            String nombreEvento = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
            
            int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas eliminar el evento '" + nombreEvento + "'?\n" +
                "Esta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (servicioEvento.eliminar(idEvento)) {
                    ValidadorUI.mostrarExito(this, "Evento eliminado exitosamente");
                    cargarEventos(); // Refrescar tabla
                } else {
                    ValidadorUI.mostrarError(this, "Error al eliminar el evento");
                }
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al eliminar evento: " + e.getMessage());
        }
    }
}