package servicios;

import dominio.Evento;
import otros.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de eventos programados
 */
public class ServicioEvento {
    
    /**
     * Listar todos los eventos
     */
    public List<Evento> listarActivos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM EVENTOS ORDER BY NombreEvento";
        
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
     * Obtener todos los eventos
     */
    public List<Evento> obtenerTodos() throws SQLException {
        String sql = "SELECT IdEvento, NombreEvento, Descripcion FROM EVENTOS ORDER BY NombreEvento";
        List<Evento> eventos = new ArrayList<>();
        
        try (Connection conn = Conexion.iniciarConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                eventos.add(mapearEvento(rs));
            }
        }
        
        return eventos;
    }
    
    /**
     * Obtener evento por ID
     */
    public Optional<Evento> obtener(int idEvento) {
        String sql = "SELECT * FROM EVENTOS WHERE IdEvento=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idEvento);
            
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
        String sql = "SELECT * FROM EVENTOS ORDER BY NombreEvento";
        
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
        // Obtener el siguiente ID disponible
        int nuevoId = obtenerSiguienteId();
        if (nuevoId == -1) {
            return false;
        }
        
        String sql = "INSERT INTO EVENTOS (IdEvento, NombreEvento, Descripcion) VALUES (?, ?, ?)";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, nuevoId);
            ps.setString(2, evento.getNombreEvento());
            ps.setString(3, evento.getDescripcion());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                evento.setIdEvento(nuevoId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al crear evento: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Obtener el siguiente ID disponible
     */
    private int obtenerSiguienteId() {
        String sql = "SELECT COALESCE(MAX(IdEvento), 0) + 1 as SiguienteId FROM EVENTOS";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("SiguienteId");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener siguiente ID: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Actualizar un evento
     */
    public boolean actualizar(Evento evento) {
        String sql = "UPDATE EVENTOS SET NombreEvento=?, Descripcion=? WHERE IdEvento=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, evento.getNombreEvento());
            ps.setString(2, evento.getDescripcion());
            ps.setInt(3, evento.getIdEvento());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar evento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar un evento
     */
    public boolean eliminar(int idEvento) {
        String sql = "DELETE FROM EVENTOS WHERE IdEvento=?";
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, idEvento);
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
        String sql = "SELECT * FROM EVENTOS WHERE NombreEvento LIKE ? ORDER BY NombreEvento";
        
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
     * Mapear ResultSet a objeto Evento
     */
    private Evento mapearEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setIdEvento(rs.getInt("IdEvento"));
        evento.setNombreEvento(rs.getString("NombreEvento"));
        evento.setDescripcion(rs.getString("Descripcion"));
        
        return evento;
    }
}