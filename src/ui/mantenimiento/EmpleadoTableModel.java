package ui.mantenimiento;

import dominio.Empleado;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoTableModel extends AbstractTableModel {
    private final String[] cols = {"Documento", "Nombres", "Apellidos", "Celular", "Correo", "Registro", "Tipo"};
    private final List<Empleado> datos = new ArrayList<>();

    public void setDatos(List<Empleado> lista) { datos.clear(); if (lista!=null) datos.addAll(lista); fireTableDataChanged(); }
    public Empleado getAt(int row) { return datos.get(row); }

    @Override public int getRowCount() { return datos.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int column) { return cols[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Empleado e = datos.get(rowIndex);
        switch (columnIndex) {
            case 0: return e.getNumDocumento();
            case 1: return e.getNombres();
            case 2: return e.getApellidos();
            case 3: return e.getCelular();
            case 4: return e.getCorreo();
            case 5: return e.getFechaRegistro();
            case 6: return e.getTipoEmpleado();
            default: return "";
        }
    }
}
