package ui.mantenimiento;

import dominio.Plan;
import servicios.ServicioGestionPlanes;

import javax.swing.*;
import java.awt.*;

public class PanelPlanes extends JPanel {
    private final ServicioGestionPlanes servicio = new ServicioGestionPlanes();
    private final PlanTableModel modelo = new PlanTableModel();
    private final JTable tabla = new JTable(modelo);

    public PanelPlanes() {
        setLayout(new BorderLayout());
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        cargarDatos();
    }

    private JComponent crearBarraSuperior() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRecargar = new JButton("Recargar");

        btnNuevo.addActionListener(e -> crearPlan());
        btnEditar.addActionListener(e -> editarPlanSeleccionado());
        btnEliminar.addActionListener(e -> eliminarPlanSeleccionado());
        btnRecargar.addActionListener(e -> cargarDatos());

        p.add(btnNuevo); p.add(btnEditar); p.add(btnEliminar); p.add(btnRecargar);
        return p;
    }

    private void cargarDatos() {
        // Usar TareaBackground para cargar datos sin bloquear la UI
        utilidades.TareaBackground.ejecutar(
            () -> servicio.listar(), // Tarea en segundo plano
            planes -> modelo.setDatos(planes), // Actualización UI en EDT
            error -> {
                error.printStackTrace();
                mostrar("Error al cargar planes: " + error.getMessage());
            }
        );
    }

    private void crearPlan() {
        DialogoPlan dlg = new DialogoPlan(SwingUtilities.getWindowAncestor(this), "Nuevo plan");
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            Plan plan = dlg.getPlan();
            if (servicio.guardar(plan)) cargarDatos();
            else mostrar("No se pudo guardar el plan");
        }
    }

    private void editarPlanSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { mostrar("Seleccione un plan"); return; }
        Plan seleccionado = modelo.getPlanAt(fila);
        DialogoPlan dlg = new DialogoPlan(SwingUtilities.getWindowAncestor(this), "Editar plan");
        dlg.setPlan(seleccionado);
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            Plan plan = dlg.getPlan();
            if (servicio.guardar(plan)) cargarDatos();
            else mostrar("No se pudo actualizar el plan");
        }
    }

    private void eliminarPlanSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { mostrar("Seleccione un plan"); return; }
        Plan p = modelo.getPlanAt(fila);
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar plan " + p.getNombrePlan() + "?", "Confirmación",
                JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            if (servicio.eliminar(p.getIdPlan())) cargarDatos();
            else mostrar("No se pudo eliminar el plan");
        }
    }

    private void mostrar(String m) { JOptionPane.showMessageDialog(this, m); }
}
