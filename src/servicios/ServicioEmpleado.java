package servicios;

import dominio.Empleado;
import dominio.Persona;
import otros.Conexion;
import persistencia.EmpleadoRepositorio;
import persistencia.PersonaRepositorio;
import persistencia.jdbc.EmpleadoRepositorioJdbc;
import persistencia.jdbc.PersonaRepositorioJdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServicioEmpleado {
    private final PersonaRepositorio personaRepo = new PersonaRepositorioJdbc();
    private final EmpleadoRepositorio empleadoRepo = new EmpleadoRepositorioJdbc();

    public List<Empleado> listar() { return empleadoRepo.listarTodos(); }
    public Optional<Empleado> obtener(String numDocumento) { return empleadoRepo.obtenerPorDocumento(numDocumento); }

    public boolean crear(Persona persona, String tipoEmpleado) {
        if (persona.getFechaRegistro()==null) persona.setFechaRegistro(LocalDate.now());
        boolean okPersona = existePersona(persona.getNumDocumento()) ? personaRepo.actualizar(persona) : personaRepo.insertar(persona);
        if (!okPersona) return false;
        Empleado e = new Empleado();
        e.setNumDocumento(persona.getNumDocumento());
        e.setTipoEmpleado(tipoEmpleado);
        return empleadoRepo.insertar(e);
    }

    public boolean actualizar(Empleado empleado) {
        // Actualiza persona y el tipo de empleado
        Persona p = empleado;
        boolean okPersona = personaRepo.actualizar(p);
        boolean okEmp = empleadoRepo.actualizar(empleado);
        return okPersona && okEmp;
    }

    public boolean eliminar(String numDocumento) {
        boolean okEmp = empleadoRepo.eliminar(numDocumento);
        boolean okPer = personaRepo.eliminar(numDocumento);
        return okEmp && okPer;
    }

    private boolean existePersona(String numDocumento) {
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement("SELECT 1 FROM PERSONA WHERE NumDocumento=?")) {
            ps.setString(1, numDocumento);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
