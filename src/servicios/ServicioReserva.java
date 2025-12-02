package servicios;

import dominio.Reserva;
import otros.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestión de reservas de eventos
 */
public class ServicioReserva {
    
    /**
     * Crear una nueva reserva
     */
    public boolean crear(int idProgramacion, String numDocumento) {
        // Verificar que el socio no tenga ya una reserva para esta programación
        if (existeReserva(idProgramacion, numDocumento)) {
            System.err.println("Error: El socio ya tiene una reserva para este evento");
            return false;
        }
        
        // Verificar que la programación sea futura
        if (!esProgramacionFutura(idProgramacion)) {
            System.err.println("Error: No se puede reservar para eventos pasados");
            return false;
        }
        
        String sql = "INSERT INTO RESERVA (IdProgramacion, NumDocumento) VALUES (?, ?)";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            ps.setString(2, numDocumento);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear reserva: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar una reserva
     */
    public boolean eliminar(int idProgramacion, String numDocumento) {
        String sql = "DELETE FROM RESERVA WHERE IdProgramacion=? AND NumDocumento=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            ps.setString(2, numDocumento);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar reserva: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Listar reservas de un socio
     */
    public List<Reserva> listarPorSocio(String numDocumento) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT r.IdProgramacion, r.NumDocumento, " +
                    "s.Nombres + ' ' + s.Apellidos AS NombreSocio, " +
                    "e.NombreEvento, p.LugarEvento, p.FechaHoraEvento " +
                    "FROM RESERVA r " +
                    "INNER JOIN PROGRAMACION p ON r.IdProgramacion = p.IdProgramacion " +
                    "INNER JOIN EVENTOS e ON p.IdEvento = e.IdEvento " +
                    "INNER JOIN SOCIO s ON r.NumDocumento = s.NumDocumento " +
                    "WHERE r.NumDocumento = ? " +
                    "ORDER BY p.FechaHoraEvento";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, numDocumento);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = mapearReserva(rs);
                    reservas.add(reserva);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar reservas por socio: " + e.getMessage());
        }
        
        return reservas;
    }
    
    /**
     * Listar reservas de una programación
     */
    public List<Reserva> listarPorProgramacion(int idProgramacion) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT r.IdProgramacion, r.NumDocumento, " +
                    "s.Nombres + ' ' + s.Apellidos AS NombreSocio, " +
                    "e.NombreEvento, p.LugarEvento, p.FechaHoraEvento " +
                    "FROM RESERVA r " +
                    "INNER JOIN PROGRAMACION p ON r.IdProgramacion = p.IdProgramacion " +
                    "INNER JOIN EVENTOS e ON p.IdEvento = e.IdEvento " +
                    "INNER JOIN SOCIO s ON r.NumDocumento = s.NumDocumento " +
                    "WHERE r.IdProgramacion = ? " +
                    "ORDER BY s.Nombres, s.Apellidos";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = mapearReserva(rs);
                    reservas.add(reserva);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar reservas por programación: " + e.getMessage());
        }
        
        return reservas;
    }
    
    /**
     * Contar reservas de una programación
     */
    public int contarReservas(int idProgramacion) {
        String sql = "SELECT COUNT(*) FROM RESERVA WHERE IdProgramacion = ?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar reservas: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Verificar si existe una reserva
     */
    public boolean existeReserva(int idProgramacion, String numDocumento) {
        String sql = "SELECT COUNT(*) FROM RESERVA WHERE IdProgramacion = ? AND NumDocumento = ?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            ps.setString(2, numDocumento);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de reserva: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Verificar si ya está reservado (alias para existeReserva)
     */
    public boolean yaEstaReservado(int idProgramacion, String numDocumento) {
        return existeReserva(idProgramacion, numDocumento);
    }
    
    /**
     * Crear una nueva reserva
     */
    public boolean crearReserva(int idProgramacion, String numDocumento) throws Exception {
        // Verificar que no esté ya reservado
        if (yaEstaReservado(idProgramacion, numDocumento)) {
            throw new Exception("Ya existe una reserva para este evento");
        }
        
        // Verificar que el evento es futuro
        if (!esProgramacionFutura(idProgramacion)) {
            throw new Exception("No se puede reservar en un evento pasado");
        }
        
        String sql = "INSERT INTO RESERVA (IdProgramacion, NumDocumento) VALUES (?, ?)";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            ps.setString(2, numDocumento);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Reserva creada exitosamente para: " + numDocumento);
                return true;
            } else {
                throw new Exception("No se pudo crear la reserva");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear reserva: " + e.getMessage());
            throw new Exception("Error al crear reserva: " + e.getMessage());
        }
    }
    
    /**
     * Cancelar una reserva existente
     */
    public boolean cancelarReserva(int idProgramacion, String numDocumento) throws Exception {
        // Verificar que la reserva existe
        if (!yaEstaReservado(idProgramacion, numDocumento)) {
            throw new Exception("No existe una reserva para este evento");
        }
        
        // Verificar que el evento es futuro
        if (!esProgramacionFutura(idProgramacion)) {
            throw new Exception("No se puede cancelar una reserva de un evento pasado");
        }
        
        String sql = "DELETE FROM RESERVA WHERE IdProgramacion = ? AND NumDocumento = ?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            ps.setString(2, numDocumento);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Reserva cancelada exitosamente para: " + numDocumento);
                return true;
            } else {
                throw new Exception("No se pudo cancelar la reserva");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al cancelar reserva: " + e.getMessage());
            throw new Exception("Error al cancelar reserva: " + e.getMessage());
        }
    }
    
    /**
     * Contar reservas por evento
     */
    public int contarReservasPorEvento(int idProgramacion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RESERVA WHERE IdProgramacion = ?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Verificar si la programación es futura
     */
    private boolean esProgramacionFutura(int idProgramacion) {
        String sql = "SELECT COUNT(*) FROM PROGRAMACION WHERE IdProgramacion = ? AND FechaHoraEvento > NOW()";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar si programación es futura: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Mapear ResultSet a objeto Reserva
     */
    private Reserva mapearReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setIdProgramacion(rs.getInt("IdProgramacion"));
        reserva.setNumDocumento(rs.getString("NumDocumento"));
        // Los datos adicionales se obtendrán mediante joins cuando sea necesario
        // La clase Reserva simplificada solo maneja los campos básicos
        return reserva;
    }
}