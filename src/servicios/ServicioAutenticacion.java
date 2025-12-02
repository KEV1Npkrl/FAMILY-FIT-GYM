package servicios;

import dominio.*;
import persistencia.jdbc.SocioRepositorioJdbc;
import persistencia.jdbc.EmpleadoRepositorioJdbc;

import java.util.Optional;

/**
 * Servicio para manejar autenticación de usuarios
 */
public class ServicioAutenticacion {
    private SocioRepositorioJdbc socioRepo;
    private EmpleadoRepositorioJdbc empleadoRepo;
    
    public ServicioAutenticacion() {
        this.socioRepo = new SocioRepositorioJdbc();
        this.empleadoRepo = new EmpleadoRepositorioJdbc();
    }
    
    /**
     * Autentica un socio
     */
    public boolean autenticarSocio(String documento, String password) {
        try {
            Optional<Socio> socioOpt = socioRepo.obtenerPorDocumento(documento);
            if (socioOpt.isPresent()) {
                Socio socio = socioOpt.get();
                if (verificarPassword(socio.getPasswordHass(), password)) {
                    SesionUsuario.getInstance().iniciarSesion(
                        socio.getNumDocumento(), 
                        socio.getNombres(), 
                        socio.getApellidos(), 
                        TipoUsuario.SOCIO
                    );
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error autenticando socio: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Autentica un empleado
     */
    public boolean autenticarEmpleado(String documento, String password) {
        try {
            Optional<Empleado> empleadoOpt = empleadoRepo.obtenerPorDocumento(documento);
            if (empleadoOpt.isPresent()) {
                Empleado empleado = empleadoOpt.get();
                if (verificarPassword(empleado.getPasswordHass(), password)) {
                    SesionUsuario.getInstance().iniciarSesion(
                        empleado.getNumDocumento(), 
                        empleado.getNombres(), 
                        empleado.getApellidos(), 
                        TipoUsuario.EMPLEADO
                    );
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error autenticando empleado: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Verifica password (por ahora comparación simple, después se puede mejorar con hash)
     */
    private boolean verificarPassword(String passwordBD, String passwordIngresado) {
        return passwordBD != null && passwordBD.equals(passwordIngresado);
    }
    
    /**
     * Cierra sesión actual
     */
    public void cerrarSesion() {
        SesionUsuario.getInstance().cerrarSesion();
    }
    
    /**
     * Verifica si hay sesión activa
     */
    public boolean haySesionActiva() {
        return SesionUsuario.getInstance().esSesionActiva();
    }
}