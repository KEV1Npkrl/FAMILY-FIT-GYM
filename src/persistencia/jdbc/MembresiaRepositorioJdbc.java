package persistencia.jdbc;

import dominio.Membresia;
import otros.Conexion;
import persistencia.MembresiaRepositorio;
import utilidades.ValidadorEntidades;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembresiaRepositorioJdbc implements MembresiaRepositorio {

    private Membresia mapear(ResultSet rs) throws SQLException {
        return new Membresia(
                rs.getInt("IdMembresia"),
                rs.getDate("FechaInicio").toLocalDate(),
                rs.getDate("FechaFin").toLocalDate(),
                rs.getString("Estado"),
                rs.getString("NumDocumento"),
                rs.getInt("IdPlan")
        );
    }

    @Override
    public Optional<Membresia> obtenerPorId(int idMembresia) {
        String sql = "SELECT IdMembresia, FechaInicio, FechaFin, Estado, NumDocumento, IdPlan FROM MEMBRESIA WHERE IdMembresia=?";
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMembresia);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(mapear(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Membresia> listarPorSocio(String numDocumento) {
        String sql = "SELECT IdMembresia, FechaInicio, FechaFin, Estado, NumDocumento, IdPlan FROM MEMBRESIA WHERE NumDocumento=?";
        List<Membresia> lista = new ArrayList<>();
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numDocumento);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) lista.add(mapear(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public List<Membresia> listarTodas() {
        String sql = "SELECT IdMembresia, FechaInicio, FechaFin, Estado, NumDocumento, IdPlan FROM MEMBRESIA ORDER BY FechaInicio DESC";
        List<Membresia> lista = new ArrayList<>();
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) lista.add(mapear(rs)); }
        } catch (SQLException e) { 
            System.err.println("Error al listar todas las membresías: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean insertar(Membresia m) {
        // Validar antes de insertar
        try {
            ValidadorEntidades.validarMembresia(m);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación en insertar(Membresia): " + e.getMessage());
            return false;
        }
        
        String sql = "INSERT INTO MEMBRESIA (IdMembresia, FechaInicio, FechaFin, Estado, NumDocumento, IdPlan) VALUES (?,?,?,?,?,?)";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en insertar(Membresia)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, m.getIdMembresia());
                ps.setDate(2, Date.valueOf(m.getFechaInicio()!=null? m.getFechaInicio(): LocalDate.now()));
                ps.setDate(3, Date.valueOf(m.getFechaFin()));
                ps.setString(4, m.getEstado());
                ps.setString(5, m.getNumDocumento());
                ps.setInt(6, m.getIdPlan());
                return ps.executeUpdate()==1;
            }
        } catch (SQLException e) { 
            System.err.println("Error SQL en insertar(Membresia): " + e.getMessage());
            e.printStackTrace(); 
            return false; 
        }
    }

    @Override
    public boolean actualizar(Membresia m) {
        // Validar antes de actualizar
        try {
            ValidadorEntidades.validarMembresia(m);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación en actualizar(Membresia): " + e.getMessage());
            return false;
        }
        
        String sql = "UPDATE MEMBRESIA SET FechaInicio=?, FechaFin=?, Estado=?, NumDocumento=?, IdPlan=? WHERE IdMembresia=?";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en actualizar(Membresia)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(m.getFechaInicio()));
                ps.setDate(2, Date.valueOf(m.getFechaFin()));
                ps.setString(3, m.getEstado());
                ps.setString(4, m.getNumDocumento());
                ps.setInt(5, m.getIdPlan());
                ps.setInt(6, m.getIdMembresia());
                return ps.executeUpdate()==1;
            }
        } catch (SQLException e) { 
            System.err.println("Error SQL en actualizar(Membresia): " + e.getMessage());
            e.printStackTrace(); 
            return false; 
        }
    }

    @Override
    public boolean eliminar(int idMembresia) {
        String sql = "DELETE FROM MEMBRESIA WHERE IdMembresia=?";
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMembresia);
            return ps.executeUpdate()==1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
