package servicios;

import dominio.Empleado;
import otros.Conexion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioEmpleadoSimple {
    
    public ServicioEmpleadoSimple() {
    }
    
    /**
     * Obtener todos los empleados
     */
    public List<Empleado> obtenerTodos() throws SQLException {
        String sql = "SELECT e.NumDocumento, p.Nombres, p.Apellidos, e.TipoEmpleado " +
                    "FROM EMPLEADO e " +
                    "INNER JOIN PERSONA p ON e.NumDocumento = p.NumDocumento " +
                    "ORDER BY p.Nombres, p.Apellidos";
        
        List<Empleado> empleados = new ArrayList<>();
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setNumDocumento(rs.getString("NumDocumento"));
                empleado.setNombres(rs.getString("Nombres"));
                empleado.setApellidos(rs.getString("Apellidos"));
                empleado.setTipoEmpleado(rs.getString("TipoEmpleado"));
                empleados.add(empleado);
            }
        }
        
        return empleados;
    }
}