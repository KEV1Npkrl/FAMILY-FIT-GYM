package dominio;

import java.time.LocalDateTime;

/**
 * Clase para manejar la sesión activa del usuario
 */
public class SesionUsuario {
    private static SesionUsuario instancia;
    private String documentoUsuario;
    private String nombres;
    private String apellidos;
    private TipoUsuario tipoUsuario;
    private TipoEmpleado tipoEmpleado; // Rol específico del empleado
    private LocalDateTime fechaInicio;
    private boolean sesionActiva;
    
    private SesionUsuario() {
        sesionActiva = false;
    }
    
    public static SesionUsuario getInstance() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }
    
    public void iniciarSesion(String documento, String nombres, String apellidos, TipoUsuario tipo) {
        this.documentoUsuario = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoUsuario = tipo;
        this.tipoEmpleado = null; // Se establece después si es empleado
        this.fechaInicio = LocalDateTime.now();
        this.sesionActiva = true;
    }
    
    public void iniciarSesionEmpleado(String documento, String nombres, String apellidos, TipoEmpleado tipoEmpleado) {
        this.documentoUsuario = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoUsuario = TipoUsuario.EMPLEADO;
        this.tipoEmpleado = tipoEmpleado;
        this.fechaInicio = LocalDateTime.now();
        this.sesionActiva = true;
    }
    
    public void cerrarSesion() {
        this.documentoUsuario = null;
        this.nombres = null;
        this.apellidos = null;
        this.tipoUsuario = null;
        this.tipoEmpleado = null;
        this.fechaInicio = null;
        this.sesionActiva = false;
    }
    
    public boolean esSesionActiva() {
        return sesionActiva;
    }
    
    public String getDocumentoUsuario() {
        return documentoUsuario;
    }
    
    public String getNumDocumento() {
        return documentoUsuario;
    }
    
    public String getNombresCompletos() {
        return nombres + " " + apellidos;
    }
    
    public String getNombreUsuario() {
        return nombres + " " + apellidos;
    }
    
    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }
    
    public TipoEmpleado getTipoEmpleado() {
        return tipoEmpleado;
    }
    
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }
    
    public boolean isAutenticado() {
        return sesionActiva;
    }
    
    public String getNombreCompleto() {
        if (nombres != null && apellidos != null) {
            return nombres + " " + apellidos;
        }
        return documentoUsuario; // Fallback
    }
    
    /**
     * Verifica si el usuario actual es Admin
     */
    public boolean esAdmin() {
        return tipoUsuario == TipoUsuario.EMPLEADO && 
               tipoEmpleado != null && tipoEmpleado.esAdmin();
    }
}