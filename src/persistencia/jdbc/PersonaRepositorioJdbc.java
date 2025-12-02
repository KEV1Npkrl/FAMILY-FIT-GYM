package persistencia.jdbc;

import dominio.Persona;
import otros.Conexion;
import persistencia.PersonaRepositorio;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonaRepositorioJdbc implements PersonaRepositorio {

    private Persona mapear(ResultSet rs) throws SQLException {
        Persona p = new Persona(rs.getString("NumDocumento"),
                rs.getString("Nombres"),
                rs.getString("Apellidos"),
                rs.getString("PasswordHass"),
                rs.getDate("FechaRegistro").toLocalDate(),
                rs.getString("Celular"),
                rs.getString("Correo")) {};
        return p;
    }

    @Override
    public Optional<Persona> obtenerPorDocumento(String numDocumento) {
        String sql = "SELECT NumDocumento, Nombres, Apellidos, PasswordHass, FechaRegistro, Celular, Correo FROM PERSONA WHERE NumDocumento=?";
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numDocumento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Persona> listarTodos() {
        String sql = "SELECT NumDocumento, Nombres, Apellidos, PasswordHass, FechaRegistro, Celular, Correo FROM PERSONA";
        List<Persona> lista = new ArrayList<>();
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public boolean insertar(Persona persona) {
        String sql = "INSERT INTO PERSONA (NumDocumento, Nombres, Apellidos, PasswordHass, FechaRegistro, Celular, Correo) VALUES (?,?,?,?,?,?,?)";
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, persona.getNumDocumento());
            ps.setString(2, persona.getNombres());
            ps.setString(3, persona.getApellidos());
            ps.setString(4, persona.getPasswordHass());
            ps.setDate(5, Date.valueOf(persona.getFechaRegistro()!=null? persona.getFechaRegistro() : LocalDate.now()));
            ps.setString(6, persona.getCelular());
            ps.setString(7, persona.getCorreo());
            return ps.executeUpdate()==1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean actualizar(Persona persona) {
        String sql = "UPDATE PERSONA SET Nombres=?, Apellidos=?, PasswordHass=?, FechaRegistro=?, Celular=?, Correo=? WHERE NumDocumento=?";
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, persona.getNombres());
            ps.setString(2, persona.getApellidos());
            ps.setString(3, persona.getPasswordHass());
            ps.setDate(4, Date.valueOf(persona.getFechaRegistro()));
            ps.setString(5, persona.getCelular());
            ps.setString(6, persona.getCorreo());
            ps.setString(7, persona.getNumDocumento());
            return ps.executeUpdate()==1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean eliminar(String numDocumento) {
        String sql = "DELETE FROM PERSONA WHERE NumDocumento=?";
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numDocumento);
            return ps.executeUpdate()==1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
