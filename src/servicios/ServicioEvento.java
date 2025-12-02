package servicios;

import dominio.Evento;
import otros.Conexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de eventos programados
 */
public class ServicioEvento {
    
    /**
     * Listar todos los eventos activos
     */
    public List<Evento> listarActivos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM EVENTO WHERE Activo=1 ORDER BY FechaHora";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Evento evento = mapearEvento(rs);
                eventos.add(evento);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar eventos activos: " + e.getMessage());
        }
        
        return eventos;
    }
    
    /**
     * Obtener un evento por ID
     */
    public Optional<Evento> obtener(int id) {
        String sql = "SELECT * FROM EVENTO WHERE Id=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEvento(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener evento: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Listar todos los eventos
     */
    public List<Evento> listarTodos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM EVENTO ORDER BY FechaHora DESC";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Evento evento = mapearEvento(rs);
                eventos.add(evento);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar eventos: " + e.getMessage());
        }
        
        return eventos;
    }
    
    /**
     * Crear un nuevo evento
     */
    public boolean crear(Evento evento) {
        String sql = "INSERT INTO EVENTO (Nombre, Descripcion, FechaHora, Duracion, Instructor, CupoMaximo, Activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, evento.getNombre());
            ps.setString(2, evento.getDescripcion());
            ps.setTimestamp(3, Timestamp.valueOf(evento.getFechaHora()));
            ps.setInt(4, evento.getDuracion());
            ps.setString(5, evento.getInstructor());
            ps.setInt(6, evento.getCupoMaximo());
            ps.setBoolean(7, evento.isActivo());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        evento.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear evento: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Actualizar un evento
     */
    public boolean actualizar(Evento evento) {
        String sql = "UPDATE EVENTO SET Nombre=?, Descripcion=?, FechaHora=?, Duracion=?, " +
                    "Instructor=?, CupoMaximo=?, Activo=? WHERE Id=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, evento.getNombre());
            ps.setString(2, evento.getDescripcion());
            ps.setTimestamp(3, Timestamp.valueOf(evento.getFechaHora()));
            ps.setInt(4, evento.getDuracion());
            ps.setString(5, evento.getInstructor());
            ps.setInt(6, evento.getCupoMaximo());
            ps.setBoolean(7, evento.isActivo());
            ps.setInt(8, evento.getId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar evento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar (desactivar) un evento
     */
    public boolean eliminar(int id) {
        String sql = "UPDATE EVENTO SET Activo=0 WHERE Id=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar evento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Buscar eventos por nombre
     */
    public List<Evento> buscarPorNombre(String nombre) {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM EVENTO WHERE Nombre LIKE ? AND Activo=1 ORDER BY FechaHora";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    eventos.add(mapearEvento(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar eventos por nombre: " + e.getMessage());
        }
        
        return eventos;
    }
    
    /**
     * Listar eventos de hoy en adelante
     */
    public List<Evento> listarProximos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM EVENTO WHERE FechaHora >= ? AND Activo=1 ORDER BY FechaHora";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    eventos.add(mapearEvento(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar eventos próximos: " + e.getMessage());
        }
        
        return eventos;
    }
    
    /**
     * Mapear ResultSet a objeto Evento
     */
    private Evento mapearEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getInt("Id"));
        evento.setNombre(rs.getString("Nombre"));
        evento.setDescripcion(rs.getString("Descripcion"));
        
        Timestamp fechaHora = rs.getTimestamp("FechaHora");
        if (fechaHora != null) {
            evento.setFechaHora(fechaHora.toLocalDateTime());
        }
        
        evento.setDuracion(rs.getInt("Duracion"));
        evento.setInstructor(rs.getString("Instructor"));
        evento.setCupoMaximo(rs.getInt("CupoMaximo"));
        evento.setActivo(rs.getBoolean("Activo"));
        
        return evento;
    }
}