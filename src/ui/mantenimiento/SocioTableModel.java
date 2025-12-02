package ui.mantenimiento;

import dominio.Socio;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SocioTableModel extends AbstractTableModel {
    private final String[] columnas = {"Documento", "Nombres", "Apellidos", "Celular", "Correo", "Registro"};
    private List<Socio> datos = new ArrayList<>();

    public void setDatos(List<Socio> lista) { this.datos = lista!=null? lista: new ArrayList<>(); fireTableDataChanged(); }
    public Socio getAt(int fila) { return datos.get(fila); }

    @Override public int getRowCount() { return datos.size(); }
    @Override public int getColumnCount() { return columnas.length; }
    @Override public String getColumnName(int column) { return columnas[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Socio s = datos.get(rowIndex);
        switch (columnIndex) {
            case 0: return s.getNumDocumento();
            case 1: return s.getNombres();
            case 2: return s.getApellidos();
            case 3: return s.getCelular();
            case 4: return s.getCorreo();
            case 5: return s.getFechaRegistro();
            default: return "";
        }
    }
}
