package persistencia.jdbc;

import dominio.Pago;
import otros.Conexion;
import persistencia.PagoRepositorio;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PagoRepositorioJdbc implements PagoRepositorio {

    private Pago mapear(ResultSet rs) throws SQLException {
        return new Pago(
                rs.getInt("IdPago"),
                rs.getTimestamp("FechaPago").toLocalDateTime(),
                rs.getBigDecimal("Monto"),
                rs.getString("NumDocumento"),
                rs.getString("IdMetodoPago"),
                rs.getInt("IdMembresia")
        );
    }

    @Override
    public Optional<Pago> obtenerPorId(int idPago) {
        String sql = "SELECT IdPago, FechaPago, Monto, NumDocumento, IdMetodoPago, IdMembresia FROM PAGO WHERE IdPago=?";
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPago);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(mapear(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Pago> listarPorMembresia(int idMembresia) {
        String sql = "SELECT IdPago, FechaPago, Monto, NumDocumento, IdMetodoPago, IdMembresia FROM PAGO WHERE IdMembresia=?";
        List<Pago> lista = new ArrayList<>();
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMembresia);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) lista.add(mapear(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public boolean insertar(Pago p) {
        String sql = "INSERT INTO PAGO (IdPago, FechaPago, Monto, NumDocumento, IdMetodoPago, IdMembresia) VALUES (?,?,?,?,?,?)";
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdPago());
            ps.setTimestamp(2, Timestamp.valueOf(p.getFechaPago()!=null? p.getFechaPago(): LocalDateTime.now()));
            ps.setBigDecimal(3, p.getMonto());
            ps.setString(4, p.getNumDocumento());
            ps.setString(5, p.getIdMetodoPago());
            ps.setInt(6, p.getIdMembresia());
            return ps.executeUpdate()==1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
