package servicios;

import dominio.AsistenciaEvento;
import otros.Conexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestión de asistencia a eventos
 */
public class ServicioAsistenciaEvento {
    
    /**
     * Verificar si un socio está registrado en un evento
     */
    public boolean estaRegistrado(String documentoSocio, int idEvento) {
        String sql = "SELECT 1 FROM ASISTENCIA_EVENTO WHERE NumDocumento=? AND IdEvento=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, documentoSocio);
            ps.setInt(2, idEvento);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar registro en evento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Contar inscritos en un evento
     */
    public int contarInscritos(int idEvento) {
        String sql = "SELECT COUNT(*) as total FROM ASISTENCIA_EVENTO WHERE IdEvento=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idEvento);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar inscritos: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Contar eventos en los que está registrado un socio
     */
    public int contarEventosRegistrados(String documentoSocio) {
        String sql = "SELECT COUNT(*) as total FROM ASISTENCIA_EVENTO ae " +
                    "INNER JOIN EVENTO e ON ae.IdEvento = e.Id " +
                    "WHERE ae.NumDocumento=? AND e.Activo=1";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, documentoSocio);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar eventos registrados: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Registrar un socio en un evento
     */
    public boolean registrarEnEvento(String documentoSocio, int idEvento) {
        String sql = "INSERT INTO ASISTENCIA_EVENTO (NumDocumento, IdEvento, FechaRegistro) VALUES (?, ?, ?)";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, documentoSocio);
            ps.setInt(2, idEvento);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar en evento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cancelar registro de un socio en un evento
     */
    public boolean cancelarRegistro(String documentoSocio, int idEvento) {
        String sql = "DELETE FROM ASISTENCIA_EVENTO WHERE NumDocumento=? AND IdEvento=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, documentoSocio);
            ps.setInt(2, idEvento);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al cancelar registro: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Marcar asistencia de un socio a un evento
     */
    public boolean marcarAsistencia(String documentoSocio, int idEvento) {
        String sql = "UPDATE ASISTENCIA_EVENTO SET Asistio=1, FechaAsistencia=? " +
                    "WHERE NumDocumento=? AND IdEvento=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, documentoSocio);
            ps.setInt(3, idEvento);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al marcar asistencia a evento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Listar eventos en los que está registrado un socio
     */
    public List<AsistenciaEvento> listarEventosRegistrados(String documentoSocio) {
        List<AsistenciaEvento> lista = new ArrayList<>();
        String sql = "SELECT ae.*, e.Nombre as NombreEvento, e.FechaHora " +
                    "FROM ASISTENCIA_EVENTO ae " +
                    "INNER JOIN EVENTO e ON ae.IdEvento = e.Id " +
                    "WHERE ae.NumDocumento=? AND e.Activo=1 " +
                    "ORDER BY e.FechaHora";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, documentoSocio);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AsistenciaEvento asistencia = new AsistenciaEvento();
                    asistencia.setNumDocumento(rs.getString("NumDocumento"));
                    asistencia.setIdEvento(rs.getInt("IdEvento"));
                    
                    Timestamp fechaReg = rs.getTimestamp("FechaRegistro");
                    if (fechaReg != null) {
                        asistencia.setFechaRegistro(fechaReg.toLocalDateTime());
                    }
                    
                    asistencia.setAsistio(rs.getBoolean("Asistio"));
                    
                    Timestamp fechaAsist = rs.getTimestamp("FechaAsistencia");
                    if (fechaAsist != null) {
                        asistencia.setFechaAsistencia(fechaAsist.toLocalDateTime());
                    }
                    
                    lista.add(asistencia);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar eventos registrados: " + e.getMessage());
        }
        
        return lista;
    }
    
    /**
     * Verificar si un socio puede registrarse en más eventos según su plan
     */
    public boolean puedeRegistrarseEnMasEventos(String documentoSocio, String nombrePlan) {
        int eventosRegistrados = contarEventosRegistrados(documentoSocio);
        
        switch (nombrePlan.toLowerCase()) {
            case "básico":
                return eventosRegistrados < 2; // Máximo 2 eventos por mes
            case "estudiante":
                return eventosRegistrados < 4; // Máximo 4 eventos por mes
            case "premium":
                return true; // Sin límite
            default:
                return false; // Plan desconocido = sin acceso
        }
    }
}