package ui.operaciones;

import dominio.Membresia;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MembresiaTableModel extends AbstractTableModel {
    private final String[] cols = {"Id", "Documento", "Plan", "Inicio", "Fin", "Estado"};
    private final List<Membresia> datos = new ArrayList<>();

    public void setDatos(List<Membresia> lista) { datos.clear(); if (lista!=null) datos.addAll(lista); fireTableDataChanged(); }
    public Membresia getAt(int row) { return datos.get(row); }

    @Override public int getRowCount() { return datos.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int column) { return cols[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Membresia m = datos.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> m.getIdMembresia();
            case 1 -> m.getNumDocumento();
            case 2 -> m.getIdPlan();
            case 3 -> m.getFechaInicio();
            case 4 -> m.getFechaFin();
            case 5 -> m.getEstado();
            default -> "";
        };
    }
}
