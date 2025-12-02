package persistencia.jdbc;

import dominio.Socio;
import otros.Conexion;
import persistencia.SocioRepositorio;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SocioRepositorioJdbc implements SocioRepositorio {

    private Socio mapear(ResultSet rs) throws SQLException {
        Socio s = new Socio();
        s.setNumDocumento(rs.getString("NumDocumento"));
        s.setNombres(rs.getString("Nombres"));
        s.setApellidos(rs.getString("Apellidos"));
        s.setPasswordHass(rs.getString("PasswordHass"));
        Date fr = rs.getDate("FechaRegistro");
        s.setFechaRegistro(fr!=null? fr.toLocalDate(): LocalDate.now());
        s.setCelular(rs.getString("Celular"));
        s.setCorreo(rs.getString("Correo"));
        return s;
    }

    @Override
    public Optional<Socio> obtenerPorDocumento(String numDocumento) {
        String sql = "SELECT p.NumDocumento, p.Nombres, p.Apellidos, p.PasswordHass, p.FechaRegistro, p.Celular, p.Correo " +
                "FROM SOCIO s JOIN PERSONA p ON s.NumDocumento=p.NumDocumento WHERE s.NumDocumento=?";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en obtenerPorDocumento(Socio)"); return Optional.empty(); }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numDocumento);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(mapear(rs)); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Socio> listarTodos() {
        String sql = "SELECT p.NumDocumento, p.Nombres, p.Apellidos, p.PasswordHass, p.FechaRegistro, p.Celular, p.Correo " +
                "FROM SOCIO s JOIN PERSONA p ON s.NumDocumento=p.NumDocumento";
        List<Socio> lista = new ArrayList<>();
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en listarTodos(Socio)"); return lista; }
            try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public boolean insertar(Socio socio) {
        String sql = "INSERT INTO SOCIO (NumDocumento) VALUES (?)";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en insertar(Socio)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, socio.getNumDocumento());
                return ps.executeUpdate()==1;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean eliminar(String numDocumento) {
        String sql = "DELETE FROM SOCIO WHERE NumDocumento=?";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en eliminar(Socio)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, numDocumento);
                return ps.executeUpdate()==1;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
