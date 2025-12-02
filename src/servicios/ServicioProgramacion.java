package servicios;

import dominio.Programacion;
import otros.Conexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioProgramacion {
    
    public ServicioProgramacion() {
    }
    
    /**
     * Crear una nueva programación
     */
    public boolean crear(Programacion programacion) throws SQLException {
        // Primero obtenemos el siguiente ID disponible
        String sqlId = "SELECT ISNULL(MAX(IdProgramacion), 0) + 1 AS NextId FROM PROGRAMACION";
        String sqlInsert = "INSERT INTO PROGRAMACION (IdProgramacion, IdEvento, FechaHoraEvento, LugarEvento, NumDocumento) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.iniciarConexion()) {
            // Obtener el siguiente ID
            int nextId = 1;
            try (PreparedStatement psId = conn.prepareStatement(sqlId);
                 ResultSet rs = psId.executeQuery()) {
                if (rs.next()) {
                    nextId = rs.getInt("NextId");
                }
            }
            
            // Insertar la programación con el nuevo ID
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, nextId);
                ps.setInt(2, programacion.getIdEvento());
                ps.setTimestamp(3, Timestamp.valueOf(programacion.getFechaHoraEvento()));
                ps.setString(4, programacion.getLugarEvento());
                ps.setString(5, programacion.getNumDocumento());
                
                boolean resultado = ps.executeUpdate() > 0;
                
                // Actualizar el ID en el objeto programación
                if (resultado) {
                    programacion.setIdProgramacion(nextId);
                }
                
                return resultado;
            }
        }
    }
    
    /**
     * Actualizar programación existente
     */
    public boolean actualizar(Programacion programacion) throws SQLException {
        String sql = "UPDATE PROGRAMACION SET IdEvento = ?, FechaHoraEvento = ?, LugarEvento = ?, NumDocumento = ? WHERE IdProgramacion = ?";
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, programacion.getIdEvento());
            ps.setTimestamp(2, Timestamp.valueOf(programacion.getFechaHoraEvento()));
            ps.setString(3, programacion.getLugarEvento());
            ps.setString(4, programacion.getNumDocumento());
            ps.setInt(5, programacion.getIdProgramacion());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Eliminar programación
     */
    public boolean eliminar(int idProgramacion) throws SQLException {
        String sql = "DELETE FROM PROGRAMACION WHERE IdProgramacion = ?";
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Obtener todas las programaciones
     */
    public List<Programacion> obtenerTodas() throws SQLException {
        String sql = "SELECT p.IdProgramacion, p.IdEvento, p.FechaHoraEvento, p.LugarEvento, " +
                    "p.NumDocumento, e.NombreEvento, " +
                    "(SELECT COUNT(*) FROM RESERVA r WHERE r.IdProgramacion = p.IdProgramacion) AS Reservados " +
                    "FROM PROGRAMACION p " +
                    "INNER JOIN EVENTOS e ON p.IdEvento = e.IdEvento " +
                    "ORDER BY p.FechaHoraEvento";
        
        List<Programacion> programaciones = new ArrayList<>();
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                programaciones.add(mapearProgramacion(rs));
            }
        }
        
        return programaciones;
    }
    
    /**
     * Obtener programación por ID
     */
    public Programacion obtenerPorId(int idProgramacion) throws SQLException {
        String sql = "SELECT p.IdProgramacion, p.IdEvento, p.FechaHoraEvento, p.LugarEvento, " +
                    "p.NumDocumento, e.NombreEvento, " +
                    "(SELECT COUNT(*) FROM RESERVA r WHERE r.IdProgramacion = p.IdProgramacion) AS Reservados " +
                    "FROM PROGRAMACION p " +
                    "INNER JOIN EVENTOS e ON p.IdEvento = e.IdEvento " +
                    "WHERE p.IdProgramacion = ?";
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProgramacion);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProgramacion(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Obtener programaciones por evento
     */
    public List<Programacion> obtenerProgramacionesPorEvento(int idEvento) throws SQLException {
        String sql = "SELECT p.IdProgramacion, p.IdEvento, p.FechaHoraEvento, p.LugarEvento, " +
                    "p.NumDocumento, e.NombreEvento, " +
                    "(SELECT COUNT(*) FROM RESERVA r WHERE r.IdProgramacion = p.IdProgramacion) AS Reservados " +
                    "FROM PROGRAMACION p " +
                    "INNER JOIN EVENTOS e ON p.IdEvento = e.IdEvento " +
                    "WHERE p.IdEvento = ? " +
                    "ORDER BY p.FechaHoraEvento";
        
        List<Programacion> programaciones = new ArrayList<>();
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idEvento);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    programaciones.add(mapearProgramacion(rs));
                }
            }
        }
        
        return programaciones;
    }
    
    /**
     * Obtener programaciones futuras
     */
    public List<Programacion> obtenerProgramacionesFuturas() throws SQLException {
        String sql = "SELECT p.IdProgramacion, p.IdEvento, p.FechaHoraEvento, p.LugarEvento, " +
                    "p.NumDocumento, e.NombreEvento, " +
                    "(SELECT COUNT(*) FROM RESERVA r WHERE r.IdProgramacion = p.IdProgramacion) AS Reservados " +
                    "FROM PROGRAMACION p " +
                    "INNER JOIN EVENTOS e ON p.IdEvento = e.IdEvento " +
                    "WHERE p.FechaHoraEvento > ? " +
                    "ORDER BY p.FechaHoraEvento";
        
        List<Programacion> programaciones = new ArrayList<>();
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    programaciones.add(mapearProgramacion(rs));
                }
            }
        }
        
        return programaciones;
    }
    
    /**
     * Verificar conflicto de horario
     */
    public boolean existeConflictoHorario(Programacion programacion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PROGRAMACION WHERE " +
                    "LugarEvento = ? AND " +
                    "ABS(TIMESTAMPDIFF(MINUTE, FechaHoraEvento, ?)) < 60 AND " +
                    "IdProgramacion != ?";
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, programacion.getLugarEvento());
            ps.setTimestamp(2, Timestamp.valueOf(programacion.getFechaHoraEvento()));
            ps.setInt(3, programacion.getIdProgramacion());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Mapear ResultSet a objeto Programacion
     */
    private Programacion mapearProgramacion(ResultSet rs) throws SQLException {
        Programacion prog = new Programacion();
        prog.setIdProgramacion(rs.getInt("IdProgramacion"));
        prog.setIdEvento(rs.getInt("IdEvento"));
        prog.setFechaHoraEvento(rs.getTimestamp("FechaHoraEvento").toLocalDateTime());
        prog.setLugarEvento(rs.getString("LugarEvento"));
        prog.setNumDocumento(rs.getString("NumDocumento"));
        prog.setNombreEvento(rs.getString("NombreEvento"));
        
        // Si existe la columna Reservados
        try {
            prog.setReservados(rs.getInt("Reservados"));
        } catch (SQLException e) {
            prog.setReservados(0);
        }
        
        return prog;
    }
}