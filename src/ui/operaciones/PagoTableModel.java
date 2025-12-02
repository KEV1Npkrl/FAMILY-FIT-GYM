package ui.operaciones;

import dominio.Pago;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PagoTableModel extends AbstractTableModel {
    private final String[] cols = {"Id", "Fecha", "Monto", "Empleado", "Método", "Membresía"};
    private final List<Pago> datos = new ArrayList<>();

    public void setDatos(List<Pago> lista) { datos.clear(); if (lista!=null) datos.addAll(lista); fireTableDataChanged(); }
    public Pago getAt(int row) { return datos.get(row); }

    @Override public int getRowCount() { return datos.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int column) { return cols[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Pago p = datos.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getIdPago();
            case 1: return p.getFechaPago();
            case 2: return p.getMonto();
            case 3: return p.getNumDocumento();
            case 4: return p.getIdMetodoPago();
            case 5: return p.getIdMembresia();
            default: return "";
        }
    }
}
