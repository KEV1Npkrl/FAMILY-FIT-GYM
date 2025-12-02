package ui.mantenimiento;

import dominio.MetodoPago;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MetodoPagoTableModel extends AbstractTableModel {
    private final String[] columnas = {"Id", "Nombre", "Activo"};
    private List<MetodoPago> datos = new ArrayList<>();

    public void setDatos(List<MetodoPago> lista) { this.datos = lista != null ? lista : new ArrayList<>(); fireTableDataChanged(); }
    public MetodoPago getAt(int fila) { return datos.get(fila); }

    @Override public int getRowCount() { return datos.size(); }
    @Override public int getColumnCount() { return columnas.length; }
    @Override public String getColumnName(int column) { return columnas[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MetodoPago m = datos.get(rowIndex);
        switch (columnIndex) {
            case 0: return m.getIdMetodoPago();
            case 1: return m.getNombreMetodo();
            case 2: return m.isEstadoActivo();
            default: return "";
        }
    }
}
