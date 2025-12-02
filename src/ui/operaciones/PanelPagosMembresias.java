package ui.operaciones;

import dominio.Membresia;
import dominio.MetodoPago;
import dominio.SesionUsuario;
import servicios.ServicioMembresiaSimple;
import utilidades.ValidadorUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelPagosMembresias extends JPanel {
    private ServicioMembresiaSimple servicioMembresia;
    private DefaultTableModel modeloTabla;
    private JTable tablaMembresiasVencidas;
    private JButton btnRenovar;
    private JButton btnActualizar;
    private SesionUsuario sesion;
    
    public PanelPagosMembresias(SesionUsuario sesion) {
        this.sesion = sesion;
        this.servicioMembresia = new ServicioMembresiaSimple();
        
        inicializarComponentes();
        cargarMembresiasVencidas();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Panel superior con título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.add(new JLabel("Gestión de Pagos - Membresías Vencidas"));
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con tabla
        String[] columnas = {
            "ID", "Socio", "Documento", "Plan", "Fecha Fin", "Estado", "Monto"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMembresiasVencidas = new JTable(modeloTabla);
        tablaMembresiasVencidas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMembresiasVencidas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    renovarMembresia();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaMembresiasVencidas);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.addActionListener(e -> cargarMembresiasVencidas());
        panelBotones.add(btnActualizar);
        
        btnRenovar = new JButton("Renovar Membresía");
        btnRenovar.addActionListener(e -> renovarMembresia());
        panelBotones.add(btnRenovar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarMembresiasVencidas() {
        try {
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            List<Membresia> membresiasVencidas = servicioMembresia.obtenerMembresiasVencidas();
            
            for (Membresia membresia : membresiasVencidas) {
                Object[] fila = {
                    membresia.getIdMembresia(),
                    membresia.getNombreSocio(),
                    membresia.getNumDocumento(),
                    membresia.getNombrePlan(),
                    membresia.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    membresia.getEstado(),
                    "$" + membresia.getPrecio().toString()
                };
                modeloTabla.addRow(fila);
            }
            
            if (membresiasVencidas.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay membresías vencidas en este momento.", 
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al cargar membresías vencidas: " + e.getMessage());
        }
    }
    
    private void renovarMembresia() {
        int filaSeleccionada = tablaMembresiasVencidas.getSelectedRow();
        if (filaSeleccionada == -1) {
            ValidadorUI.mostrarAdvertencia(this, "Debe seleccionar una membresía para renovar");
            return;
        }
        
        try {
            // Obtener datos de la fila seleccionada
            int idMembresia = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            String nombreSocio = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
            String documento = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
            String nombrePlan = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
            String montoStr = (String) modeloTabla.getValueAt(filaSeleccionada, 6);
            
            // Confirmar renovación
            String mensaje = String.format(
                "¿Desea renovar la membresía?\n\n" +
                "Socio: %s (%s)\n" +
                "Plan: %s\n" +
                "Monto: %s",
                nombreSocio, documento, nombrePlan, montoStr
            );
            
            if (!ValidadorUI.confirmar(this, mensaje)) {
                return;
            }
            
            // Mostrar diálogo para seleccionar método de pago
            DialogoMetodoPago dialogoPago = new DialogoMetodoPago(
                (Window) SwingUtilities.getWindowAncestor(this)
            );
            dialogoPago.setVisible(true);
            
            if (dialogoPago.isAceptado()) {
                String metodoPago = dialogoPago.getMetodoPagoSeleccionado();
                String empleadoCajero = sesion.getNumDocumento();
                
                // Procesar renovación
                boolean exito = servicioMembresia.renovarMembresia(idMembresia, metodoPago, empleadoCajero);
                
                if (exito) {
                    ValidadorUI.mostrarExito(this, "Membresía renovada exitosamente");
                    cargarMembresiasVencidas(); // Actualizar lista
                } else {
                    ValidadorUI.mostrarError(this, "Error al procesar la renovación");
                }
            }
            
        } catch (Exception e) {
            ValidadorUI.mostrarError(this, "Error al renovar membresía: " + e.getMessage());
        }
    }
}

/**
 * Diálogo para seleccionar método de pago
 */
class DialogoMetodoPago extends JDialog {
    private JComboBox<MetodoPago> cmbMetodoPago;
    private JButton btnAceptar;
    private JButton btnCancelar;
    private boolean aceptado = false;
    private ServicioMembresiaSimple servicioMembresia;
    
    public DialogoMetodoPago(Window parent) {
        super(parent, "Seleccionar Método de Pago", Dialog.ModalityType.APPLICATION_MODAL);
        this.servicioMembresia = new ServicioMembresiaSimple();
        
        initComponents();
        cargarMetodosPago();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Método de pago
        gbc.gridx = 0; gbc.gridy = 0;
        panelPrincipal.add(new JLabel("Método de Pago:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbMetodoPago = new JComboBox<>();
        panelPrincipal.add(cmbMetodoPago, gbc);
        
        add(panelPrincipal, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnAceptar = new JButton("Procesar Pago");
        btnAceptar.addActionListener(e -> aceptar());
        panelBotones.add(btnAceptar);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarMetodosPago() {
        try {
            cmbMetodoPago.removeAllItems();
            List<MetodoPago> metodos = servicioMembresia.obtenerMetodosPagoActivos();
            for (MetodoPago metodo : metodos) {
                cmbMetodoPago.addItem(metodo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar métodos de pago: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aceptar() {
        if (cmbMetodoPago.getSelectedItem() == null) {
            ValidadorUI.mostrarAdvertencia(this, "Debe seleccionar un método de pago");
            return;
        }
        
        aceptado = true;
        dispose();
    }
    
    public boolean isAceptado() {
        return aceptado;
    }
    
    public String getMetodoPagoSeleccionado() {
        if (cmbMetodoPago.getSelectedItem() != null) {
            return ((MetodoPago) cmbMetodoPago.getSelectedItem()).getIdMetodoPago();
        }
        return null;
    }
}