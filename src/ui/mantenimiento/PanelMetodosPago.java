package ui.mantenimiento;

import dominio.MetodoPago;
import servicios.ServicioGestionMetodosPago;

import javax.swing.*;
import java.awt.*;

public class PanelMetodosPago extends JPanel {
    private final ServicioGestionMetodosPago servicio = new ServicioGestionMetodosPago();
    private final MetodoPagoTableModel modelo = new MetodoPagoTableModel();
    private final JTable tabla = new JTable(modelo);

    public PanelMetodosPago() {
        setLayout(new BorderLayout());
        add(crearToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        recargar();
    }

    private JComponent crearToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRecargar = new JButton("Recargar");

        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnRecargar.addActionListener(e -> recargar());

        p.add(btnNuevo); p.add(btnEditar); p.add(btnEliminar); p.add(btnRecargar);
        return p;
    }

    private void recargar() {
        modelo.setDatos(servicio.listar());
    }

    private void nuevo() {
        DialogoMetodoPago dlg = new DialogoMetodoPago(SwingUtilities.getWindowAncestor(this), "Nuevo método de pago");
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            MetodoPago m = dlg.getMetodo();
            if (servicio.guardar(m)) recargar(); else mostrar("No se pudo guardar");
        }
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { mostrar("Seleccione un registro"); return; }
        MetodoPago sel = modelo.getAt(fila);
        DialogoMetodoPago dlg = new DialogoMetodoPago(SwingUtilities.getWindowAncestor(this), "Editar método de pago");
        dlg.setMetodo(sel);
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            MetodoPago m = dlg.getMetodo();
            if (servicio.guardar(m)) recargar(); else mostrar("No se pudo actualizar");
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { mostrar("Seleccione un registro"); return; }
        MetodoPago sel = modelo.getAt(fila);
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar método " + sel.getNombreMetodo() + "?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            if (servicio.eliminar(sel.getIdMetodoPago())) recargar(); else mostrar("No se pudo eliminar");
        }
    }

    private void mostrar(String m) { JOptionPane.showMessageDialog(this, m); }
}
