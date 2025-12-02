package servicios;

import dominio.Persona;
import dominio.Socio;
import otros.Conexion;
import persistencia.PersonaRepositorio;
import persistencia.SocioRepositorio;
import persistencia.jdbc.PersonaRepositorioJdbc;
import persistencia.jdbc.SocioRepositorioJdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class ServicioSocio {
    private final PersonaRepositorio personaRepo = new PersonaRepositorioJdbc();
    private final SocioRepositorio socioRepo = new SocioRepositorioJdbc();

    public List<Socio> listar() { return socioRepo.listarTodos(); }

    public List<Socio> obtenerTodos() { return socioRepo.listarTodos(); }

    public Optional<Socio> obtener(String numDocumento) { return socioRepo.obtenerPorDocumento(numDocumento); }

    public boolean crear(Persona persona) {
        // Inserta PERSONA y luego SOCIO
        if (persona.getFechaRegistro()==null) persona.setFechaRegistro(LocalDate.now());
        boolean okPersona = personaRepo.insertar(persona);
        if (!okPersona) return false;
        Socio s = new Socio();
        s.setNumDocumento(persona.getNumDocumento());
        return socioRepo.insertar(s);
    }

    public boolean actualizar(Persona persona) {
        return personaRepo.actualizar(persona);
    }

    public boolean eliminar(String numDocumento) {
        // Eliminar primero SOCIO y luego PERSONA
        boolean okSocio = socioRepo.eliminar(numDocumento);
        boolean okPersona = personaRepo.eliminar(numDocumento);
        return okSocio && okPersona;
    }

    public boolean existePersona(String numDocumento) {
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement("SELECT 1 FROM PERSONA WHERE NumDocumento=?")) {
            ps.setString(1, numDocumento);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Busca socios según criterios específicos
     */
    public List<Socio> buscarPorCriterios(String documento, String nombres, String apellidos, 
                                         LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Socio> resultados = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.NumDocumento, p.Nombres, p.Apellidos, ");
        sql.append("p.Celular, p.Correo, p.FechaRegistro ");
        sql.append("FROM PERSONA p INNER JOIN SOCIO s ON p.NumDocumento = s.NumDocumento WHERE 1=1 ");
        
        List<Object> parametros = new ArrayList<>();
        
        if (documento != null && !documento.trim().isEmpty()) {
            sql.append("AND p.NumDocumento LIKE ? ");
            parametros.add("%" + documento.trim() + "%");
        }
        
        if (nombres != null && !nombres.trim().isEmpty()) {
            sql.append("AND p.Nombres LIKE ? ");
            parametros.add("%" + nombres.trim() + "%");
        }
        
        if (apellidos != null && !apellidos.trim().isEmpty()) {
            sql.append("AND p.Apellidos LIKE ? ");
            parametros.add("%" + apellidos.trim() + "%");
        }
        
        if (fechaDesde != null) {
            sql.append("AND p.FechaRegistro >= ? ");
            parametros.add(fechaDesde);
        }
        
        if (fechaHasta != null) {
            sql.append("AND p.FechaRegistro <= ? ");
            parametros.add(fechaHasta);
        }
        
        sql.append("ORDER BY p.FechaRegistro DESC, p.Nombres");
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            
            // Establecer parámetros
            for (int i = 0; i < parametros.size(); i++) {
                if (parametros.get(i) instanceof LocalDate) {
                    ps.setDate(i + 1, java.sql.Date.valueOf((LocalDate) parametros.get(i)));
                } else {
                    ps.setString(i + 1, parametros.get(i).toString());
                }
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Socio socio = new Socio();
                    socio.setNumDocumento(rs.getString("NumDocumento"));
                    socio.setNombres(rs.getString("Nombres"));
                    socio.setApellidos(rs.getString("Apellidos"));
                    socio.setCelular(rs.getString("Celular"));
                    socio.setCorreo(rs.getString("Correo"));
                    socio.setFechaRegistro(rs.getDate("FechaRegistro").toLocalDate());
                    socio.setActivo(true); // Por defecto activo
                    
                    resultados.add(socio);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar socios por criterios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultados;
    }
    
    /**
     * Verifica si la contraseña proporcionada es correcta para el socio
     */
    public boolean verificarPassword(String numDocumento, String password) {
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement("SELECT PasswordHass FROM PERSONA WHERE NumDocumento=?")) {
            ps.setString(1, numDocumento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passwordBD = rs.getString("PasswordHass");
                    return passwordBD != null && passwordBD.equals(password);
                }
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cambia la contraseña del socio
     */
    public boolean cambiarPassword(String numDocumento, String nuevaPassword) {
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement("UPDATE PERSONA SET PasswordHass=? WHERE NumDocumento=?")) {
            ps.setString(1, nuevaPassword);
            ps.setString(2, numDocumento);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }
}