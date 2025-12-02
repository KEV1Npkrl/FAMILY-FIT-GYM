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
        this.fechaInicio = LocalDateTime.now();
        this.sesionActiva = true;
    }
    
    public void cerrarSesion() {
        this.documentoUsuario = null;
        this.nombres = null;
        this.apellidos = null;
        this.tipoUsuario = null;
        this.fechaInicio = null;
        this.sesionActiva = false;
    }
    
    public boolean esSesionActiva() {
        return sesionActiva;
    }
    
    public String getDocumentoUsuario() {
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
    
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }
    
    public boolean isAutenticado() {
        return sesionActiva;
    }
}