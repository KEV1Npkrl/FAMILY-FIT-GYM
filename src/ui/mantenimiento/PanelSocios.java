package ui.mantenimiento;

import dominio.Socio;
import servicios.ServicioSocio;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelSocios extends JPanel {
    private final ServicioSocio servicio;
    private final SocioTableModel modelo;
    private final JTable tabla;

    public PanelSocios() {
        this.servicio = new ServicioSocio();
        this.modelo = new SocioTableModel();
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

        btnNuevo.addActionListener(e -> crearSocio());
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
    }

    private void recargar() {
        List<Socio> lista = servicio.listar();
        modelo.setDatos(lista);
    }

    private void crearSocio() {
        DialogoSocio dlg = new DialogoSocio(SwingUtilities.getWindowAncestor(this), "Nuevo socio");
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            try {
                servicio.crear(dlg.getSocio());
                recargar();
            } catch (Exception ex) {
                mostrarError(ex);
            }
        }
    }

    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Seleccione un socio"); return; }
        Socio s = modelo.getAt(fila);
        DialogoSocio dlg = new DialogoSocio(SwingUtilities.getWindowAncestor(this), "Editar socio");
        dlg.setSocio(s);
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            try {
                servicio.actualizar(dlg.getSocio());
                recargar();
            } catch (Exception ex) {
                mostrarError(ex);
            }
        }
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Seleccione un socio"); return; }
        Socio s = modelo.getAt(fila);
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar al socio " + s.getNombres() + " " + s.getApellidos() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            try {
                servicio.eliminar(s.getNumDocumento());
                recargar();
            } catch (Exception ex) {
                mostrarError(ex);
            }
        }
    }

    private void mostrarError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
