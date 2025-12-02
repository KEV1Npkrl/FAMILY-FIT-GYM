package ui.mantenimiento;

import dominio.Empleado;
import servicios.ServicioEmpleado;

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
        JToolBar tb = new JToolBar();
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        tb.add(btnNuevo); tb.add(btnEditar); tb.add(btnEliminar);
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
        DialogoEmpleado dlg = new DialogoEmpleado(SwingUtilities.getWindowAncestor(this), "Nuevo empleado");
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            try {
                Empleado e = dlg.getEmpleado();
                servicio.crear(e, e.getTipoEmpleado());
                recargar();
            } catch (Exception ex) { mostrarError(ex); }
        }
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Seleccione un empleado"); return; }
        Empleado e = modelo.getAt(fila);
        DialogoEmpleado dlg = new DialogoEmpleado(SwingUtilities.getWindowAncestor(this), "Editar empleado");
        dlg.setEmpleado(e);
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            try {
                Empleado actualizado = dlg.getEmpleado();
                servicio.actualizar(actualizado);
                recargar();
            } catch (Exception ex) { mostrarError(ex); }
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Seleccione un empleado"); return; }
        Empleado e = modelo.getAt(fila);
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar al empleado "+e.getNombres()+" "+e.getApellidos()+"?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r==JOptionPane.YES_OPTION) {
            try {
                servicio.eliminar(e.getNumDocumento());
                recargar();
            } catch (Exception ex) { mostrarError(ex); }
        }
    }

    private void mostrarError(Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
}
