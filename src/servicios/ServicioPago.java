package servicios;

import dominio.Pago;
import otros.Conexion;
import persistencia.PagoRepositorio;
import persistencia.jdbc.PagoRepositorioJdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ServicioPago {
    private final PagoRepositorio repo = new PagoRepositorioJdbc();

    public boolean registrarPago(int idMembresia, String idMetodoPago, String numDocumentoEmpleado, BigDecimal monto) {
        int nuevoId = siguienteId("PAGO", "IdPago");
        Pago p = new Pago(nuevoId, LocalDateTime.now(), monto, numDocumentoEmpleado, idMetodoPago, idMembresia);
        return repo.insertar(p);
    }

    public List<Pago> listarPorMembresia(int idMembresia) { return repo.listarPorMembresia(idMembresia); }

    private int siguienteId(String tabla, String campoId) {
        String sql = "SELECT ISNULL(MAX("+campoId+"),0)+1 AS NextId FROM "+tabla;
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt("NextId"); }
        } catch (SQLException e) { e.printStackTrace(); }
        return (int)(System.currentTimeMillis()/1000);
    }
}
