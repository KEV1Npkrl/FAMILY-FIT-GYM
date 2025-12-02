package persistencia.jdbc;

import dominio.Empleado;
import otros.Conexion;
import persistencia.EmpleadoRepositorio;
import utilidades.ValidadorEntidades;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpleadoRepositorioJdbc implements EmpleadoRepositorio {

    private Empleado mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setNumDocumento(rs.getString("NumDocumento"));
        e.setNombres(rs.getString("Nombres"));
        e.setApellidos(rs.getString("Apellidos"));
        e.setPasswordHass(rs.getString("PasswordHass"));
        Date fr = rs.getDate("FechaRegistro");
        e.setFechaRegistro(fr!=null? fr.toLocalDate(): LocalDate.now());
        e.setCelular(rs.getString("Celular"));
        e.setCorreo(rs.getString("Correo"));
        e.setTipoEmpleado(rs.getString("TipoEmpleado"));
        return e;
    }

    @Override
    public Optional<Empleado> obtenerPorDocumento(String numDocumento) {
        String sql = "SELECT p.NumDocumento, p.Nombres, p.Apellidos, p.PasswordHass, p.FechaRegistro, p.Celular, p.Correo, e.TipoEmpleado " +
                "FROM EMPLEADO e JOIN PERSONA p ON e.NumDocumento=p.NumDocumento WHERE e.NumDocumento=?";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en obtenerPorDocumento(Empleado)"); return Optional.empty(); }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, numDocumento);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(mapear(rs)); }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Empleado> listarTodos() {
        String sql = "SELECT p.NumDocumento, p.Nombres, p.Apellidos, p.PasswordHass, p.FechaRegistro, p.Celular, p.Correo, e.TipoEmpleado " +
                "FROM EMPLEADO e JOIN PERSONA p ON e.NumDocumento=p.NumDocumento";
        List<Empleado> lista = new ArrayList<>();
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en listarTodos(Empleado)"); return lista; }
            try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    @Override
    public boolean insertar(Empleado empleado) {
        // Validar antes de insertar
        try {
            ValidadorEntidades.validarEmpleado(empleado);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación en insertar(Empleado): " + e.getMessage());
            return false;
        }
        
        String sql = "INSERT INTO EMPLEADO (NumDocumento, TipoEmpleado) VALUES (?,?)";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en insertar(Empleado)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, empleado.getNumDocumento());
                ps.setString(2, empleado.getTipoEmpleado());
                return ps.executeUpdate()==1;
            }
        } catch (SQLException ex) { 
            System.err.println("Error SQL en insertar(Empleado): " + ex.getMessage());
            ex.printStackTrace(); 
            return false; 
        }
    }

    @Override
    public boolean actualizar(Empleado empleado) {
        // Validar antes de actualizar
        try {
            ValidadorEntidades.validarEmpleado(empleado);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación en actualizar(Empleado): " + e.getMessage());
            return false;
        }
        
        String sql = "UPDATE EMPLEADO SET TipoEmpleado=? WHERE NumDocumento=?";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en actualizar(Empleado)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, empleado.getTipoEmpleado());
                ps.setString(2, empleado.getNumDocumento());
                return ps.executeUpdate()==1;
            }
        } catch (SQLException ex) { 
            System.err.println("Error SQL en actualizar(Empleado): " + ex.getMessage());
            ex.printStackTrace(); 
            return false; 
        }
    }

    @Override
    public boolean eliminar(String numDocumento) {
        String sql = "DELETE FROM EMPLEADO WHERE NumDocumento=?";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en eliminar(Empleado)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, numDocumento);
                return ps.executeUpdate()==1;
            }
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }
}
