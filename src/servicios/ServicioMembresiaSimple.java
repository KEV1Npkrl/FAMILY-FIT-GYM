package servicios;

import dominio.Membresia;
import dominio.MetodoPago;
import otros.Conexion;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioMembresiaSimple {
    
    public ServicioMembresiaSimple() {
    }
    
    /**
     * Obtener todas las membresías vencidas
     */
    public List<Membresia> obtenerMembresiasVencidas() throws SQLException {
        String sql = "SELECT m.IdMembresia, m.FechaInicio, m.FechaFin, m.Estado, " +
                    "m.NumDocumento, m.IdPlan, p.NombrePlan, p.Costo, " +
                    "per.Nombres, per.Apellidos " +
                    "FROM MEMBRESIA m " +
                    "INNER JOIN PLANES p ON m.IdPlan = p.IdPlan " +
                    "INNER JOIN SOCIO s ON m.NumDocumento = s.NumDocumento " +
                    "INNER JOIN PERSONA per ON s.NumDocumento = per.NumDocumento " +
                    "WHERE m.FechaFin < GETDATE() OR m.Estado = 'VENCIDA' " +
                    "ORDER BY m.FechaFin DESC";
        
        List<Membresia> membresias = new ArrayList<>();
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Membresia membresia = new Membresia();
                membresia.setIdMembresia(rs.getInt("IdMembresia"));
                membresia.setFechaInicio(rs.getDate("FechaInicio").toLocalDate());
                membresia.setFechaFin(rs.getDate("FechaFin").toLocalDate());
                membresia.setEstado(rs.getString("Estado"));
                membresia.setNumDocumento(rs.getString("NumDocumento"));
                membresia.setIdPlan(rs.getInt("IdPlan"));
                membresia.setPrecio(rs.getBigDecimal("Costo"));
                
                // Agregar información adicional para mostrar
                membresia.setNombrePlan(rs.getString("NombrePlan"));
                membresia.setNombreSocio(rs.getString("Nombres") + " " + rs.getString("Apellidos"));
                
                membresias.add(membresia);
            }
        }
        
        return membresias;
    }
    
    /**
     * Renovar membresía - crear nueva y desactivar la anterior
     */
    public boolean renovarMembresia(int idMembresiaAnterior, String metodoPago, String empleadoCajero) throws SQLException {
        Connection conn = null;
        try {
            conn = Conexion.iniciarConexion();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // 1. Obtener datos de la membresía anterior
            String sqlMembresia = "SELECT NumDocumento, IdPlan FROM MEMBRESIA WHERE IdMembresia = ?";
            String numDocumento = null;
            int idPlan = 0;
            BigDecimal costo = BigDecimal.ZERO;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlMembresia)) {
                ps.setInt(1, idMembresiaAnterior);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        numDocumento = rs.getString("NumDocumento");
                        idPlan = rs.getInt("IdPlan");
                    } else {
                        throw new SQLException("Membresía no encontrada");
                    }
                }
            }
            
            // 2. Obtener costo del plan
            String sqlPlan = "SELECT Costo, DuracionDias FROM PLANES WHERE IdPlan = ?";
            int duracionDias = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlPlan)) {
                ps.setInt(1, idPlan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        costo = rs.getBigDecimal("Costo");
                        duracionDias = rs.getInt("DuracionDias");
                    }
                }
            }
            
            // 3. Obtener el próximo ID para membresía
            String sqlNextId = "SELECT ISNULL(MAX(IdMembresia), 0) + 1 AS NextId FROM MEMBRESIA";
            int nextIdMembresia = 1;
            try (PreparedStatement ps = conn.prepareStatement(sqlNextId);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nextIdMembresia = rs.getInt("NextId");
                }
            }
            
            // 4. Crear nueva membresía
            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaFin = fechaInicio.plusDays(duracionDias);
            
            String sqlNuevaMembresia = "INSERT INTO MEMBRESIA (IdMembresia, FechaInicio, FechaFin, Estado, NumDocumento, IdPlan) " +
                                     "VALUES (?, ?, ?, 'ACTIVA', ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlNuevaMembresia)) {
                ps.setInt(1, nextIdMembresia);
                ps.setDate(2, Date.valueOf(fechaInicio));
                ps.setDate(3, Date.valueOf(fechaFin));
                ps.setString(4, numDocumento);
                ps.setInt(5, idPlan);
                ps.executeUpdate();
            }
            
            // 5. Actualizar membresía anterior a RENOVADA
            String sqlActualizarAnterior = "UPDATE MEMBRESIA SET Estado = 'RENOVADA' WHERE IdMembresia = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlActualizarAnterior)) {
                ps.setInt(1, idMembresiaAnterior);
                ps.executeUpdate();
            }
            
            // 6. Obtener próximo ID para pago
            String sqlNextIdPago = "SELECT ISNULL(MAX(IdPago), 0) + 1 AS NextId FROM PAGO";
            int nextIdPago = 1;
            try (PreparedStatement ps = conn.prepareStatement(sqlNextIdPago);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nextIdPago = rs.getInt("NextId");
                }
            }
            
            // 7. Registrar el pago
            String sqlPago = "INSERT INTO PAGO (IdPago, FechaPago, Monto, NumDocumento, IdMetodoPago, IdMembresia) " +
                           "VALUES (?, GETDATE(), ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPago)) {
                ps.setInt(1, nextIdPago);
                ps.setBigDecimal(2, costo);
                ps.setString(3, empleadoCajero);
                ps.setString(4, metodoPago);
                ps.setInt(5, nextIdMembresia);
                ps.executeUpdate();
            }
            
            conn.commit(); // Confirmar transacción
            return true;
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir transacción en caso de error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Obtener métodos de pago activos
     */
    public List<MetodoPago> obtenerMetodosPagoActivos() throws SQLException {
        String sql = "SELECT IdMetodoPago, NombreMetodo FROM METODOPAGO WHERE EstadoActivo = 1 ORDER BY NombreMetodo";
        
        List<MetodoPago> metodos = new ArrayList<>();
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                MetodoPago metodo = new MetodoPago();
                metodo.setIdMetodoPago(rs.getString("IdMetodoPago"));
                metodo.setNombreMetodo(rs.getString("NombreMetodo"));
                metodos.add(metodo);
            }
        }
        
        return metodos;
    }
}