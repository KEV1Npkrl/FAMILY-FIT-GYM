package persistencia.jdbc;

import dominio.Asistencia;
import otros.Conexion;
import persistencia.AsistenciaRepositorio;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaRepositorioJdbc implements AsistenciaRepositorio {

    private Asistencia mapear(ResultSet rs) throws SQLException {
        return new Asistencia(
                rs.getInt("IdAsistencia"),
                rs.getString("NumDocumento"),
                rs.getTimestamp("FechaHoraEntrada").toLocalDateTime()
        );
    }

    @Override
    public boolean insertar(Asistencia a) {
        String sql = "INSERT INTO ASISTENCIA (IdAsistencia, NumDocumento, FechaHoraEntrada) VALUES (?,?,?)";
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, a.getIdAsistencia());
            ps.setString(2, a.getNumDocumento());
            ps.setTimestamp(3, Timestamp.valueOf(a.getFechaHoraEntrada()!=null? a.getFechaHoraEntrada(): LocalDateTime.now()));
            return ps.executeUpdate()==1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Asistencia> listarPorSocio(String numDocumento) {
        String sql = "SELECT IdAsistencia, NumDocumento, FechaHoraEntrada FROM ASISTENCIA WHERE NumDocumento=? ORDER BY FechaHoraEntrada DESC";
        List<Asistencia> lista = new ArrayList<>();
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, numDocumento);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) lista.add(mapear(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
