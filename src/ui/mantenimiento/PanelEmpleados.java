package ui.mantenimiento;

import dominio.Empleado;
import servicios.ServicioEmpleado;
import seguridad.ControladorPermisos;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelEmpleados extends JPanel {
    private final ServicioEmpleado servicio;
    private final EmpleadoTableModel modelo;
    private final JTable tabla;

    public PanelEmpleados() {
        this.servicio = new ServicioEmpleado();
        this.modelo = new EmpleadoTableModel();
        this.tabla = new JTable(modelo);
        construirUI();
        recargar();
    }

    private void construirUI() {
        setLayout(new BorderLayout(8,8));
        
        // Verificar permisos antes de mostrar controles
        boolean puedeModificar = ControladorPermisos.puedeModificarEmpleados();
        
        JToolBar tb = new JToolBar();
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        
        // Habilitar botones según permisos
        btnNuevo.setEnabled(puedeModificar);
        btnEditar.setEnabled(puedeModificar);
        btnEliminar.setEnabled(puedeModificar);
        
        if (!puedeModificar) {
            // Agregar etiqueta informativa
            JLabel lblInfo = new JLabel("Solo administradores pueden gestionar empleados");
            lblInfo.setForeground(Color.RED);
            lblInfo.setFont(lblInfo.getFont().deriveFont(Font.ITALIC));
            tb.add(lblInfo);
            tb.addSeparator();
        }
        
        tb.add(btnNuevo); 
        tb.add(btnEditar); 
        tb.add(btnEliminar);
        add(tb, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> crear());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
    }

    private void recargar() {
        List<Empleado> lista = servicio.listar();
        modelo.setDatos(lista);
    }

    private void crear() {
        if (!ControladorPermisos.verificarYMostrarError(() -> ControladorPermisos.puedeModificarEmpleados(), this)) {
            return;
        }
        
        DialogoEmpleado dlg = new DialogoEmpleado(SwingUtilities.getWindowAncestor(this), "Nuevo empleado");
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            try {
                Empleado e = dlg.getEmpleado();
                servicio.crear(e, e.getTipoEmpleado());
                recargar();
                JOptionPane.showMessageDialog(this, "Empleado creado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { mostrarError(ex); }
        }
    }

    private void editar() {
        if (!ControladorPermisos.verificarYMostrarError(() -> ControladorPermisos.puedeModificarEmpleados(), this)) {
            return;
        }
        
        int fila = tabla.getSelectedRow();
        if (fila < 0) { 
            JOptionPane.showMessageDialog(this, "Seleccione un empleado para editar"); 
            return; 
        }
        
        Empleado e = modelo.getAt(fila);
        DialogoEmpleado dlg = new DialogoEmpleado(SwingUtilities.getWindowAncestor(this), "Editar empleado");
        dlg.setEmpleado(e);
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            try {
                Empleado actualizado = dlg.getEmpleado();
                servicio.actualizar(actualizado);
                recargar();
                JOptionPane.showMessageDialog(this, "Empleado actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { mostrarError(ex); }
        }
    }

    private void eliminar() {
        if (!ControladorPermisos.verificarYMostrarError(() -> ControladorPermisos.puedeModificarEmpleados(), this)) {
            return;
        }
        
        int fila = tabla.getSelectedRow();
        if (fila < 0) { 
            JOptionPane.showMessageDialog(this, "Seleccione un empleado para eliminar"); 
            return; 
        }
        
        Empleado e = modelo.getAt(fila);
        
        // Verificar que no se elimine a sí mismo
        if (e.getNumDocumento().equals(ControladorPermisos.getDocumentoUsuarioActual())) {
            JOptionPane.showMessageDialog(this, 
                "No puedes eliminar tu propia cuenta", 
                "Operación no permitida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int r = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar al empleado " + e.getNombres() + " " + e.getApellidos() + "?\n\n" +
            "Esta acción no se puede deshacer.", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (r == JOptionPane.YES_OPTION) {
            try {
                servicio.eliminar(e.getNumDocumento());
                recargar();
                JOptionPane.showMessageDialog(this, "Empleado eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { mostrarError(ex); }
        }
    }

    private void mostrarError(Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
}
