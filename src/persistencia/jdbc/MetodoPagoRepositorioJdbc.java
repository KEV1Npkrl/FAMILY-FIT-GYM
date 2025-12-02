package persistencia.jdbc;

import dominio.MetodoPago;
import otros.Conexion;
import persistencia.MetodoPagoRepositorio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MetodoPagoRepositorioJdbc implements MetodoPagoRepositorio {

    private MetodoPago mapear(ResultSet rs) throws SQLException {
        String id = rs.getString("IdMetodoPago");
        String nombre = rs.getString("NombreMetodo");
        boolean activo = rs.getBoolean("EstadoActivo");
        return new MetodoPago(id, nombre, activo);
    }

    @Override
    public Optional<MetodoPago> obtenerPorId(String idMetodoPago) {
        String sql = "SELECT IdMetodoPago, NombreMetodo, EstadoActivo FROM METODOPAGO WHERE IdMetodoPago = ?";
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, idMetodoPago);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<MetodoPago> listarTodos() {
        String sql = "SELECT IdMetodoPago, NombreMetodo, EstadoActivo FROM METODOPAGO";
        List<MetodoPago> lista = new ArrayList<>();
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(MetodoPago metodoPago) {
        String sql = "INSERT INTO METODOPAGO (IdMetodoPago, NombreMetodo, EstadoActivo) VALUES (?, ?, ?)";
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, metodoPago.getIdMetodoPago());
            ps.setString(2, metodoPago.getNombreMetodo());
            ps.setBoolean(3, metodoPago.isEstadoActivo());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(MetodoPago metodoPago) {
        String sql = "UPDATE METODOPAGO SET NombreMetodo=?, EstadoActivo=? WHERE IdMetodoPago=?";
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, metodoPago.getNombreMetodo());
            ps.setBoolean(2, metodoPago.isEstadoActivo());
            ps.setString(3, metodoPago.getIdMetodoPago());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(String idMetodoPago) {
        String sql = "DELETE FROM METODOPAGO WHERE IdMetodoPago=?";
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, idMetodoPago);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
