package ui.mantenimiento;

import dominio.Plan;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PlanTableModel extends AbstractTableModel {
    private final String[] columnas = {"Id", "Nombre", "Duración (días)", "Costo", "Descripción", "Tipo"};
    private List<Plan> datos = new ArrayList<>();

    public void setDatos(List<Plan> lista) {
        this.datos = lista != null ? lista : new ArrayList<>();
        fireTableDataChanged();
    }

    public Plan getPlanAt(int fila) { return datos.get(fila); }

    @Override
    public int getRowCount() { return datos.size(); }

    @Override
    public int getColumnCount() { return columnas.length; }

    @Override
    public String getColumnName(int column) { return columnas[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Plan p = datos.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getIdPlan();
            case 1: return p.getNombrePlan();
            case 2: return p.getDuracionDias();
            case 3: return p.getCosto();
            case 4: return p.getDescripcion();
            case 5: return p.getTipo();
            default: return "";
        }
    }
}
