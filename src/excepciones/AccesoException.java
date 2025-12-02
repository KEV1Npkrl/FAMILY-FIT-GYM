package excepciones;

/**
 * Excepción personalizada para errores de autorización y permisos
 */
public class AccesoException extends RuntimeException {
    
    private final String usuario;
    private final String accionDenegada;
    private final String nivelRequerido;
    
    public AccesoException(String mensaje) {
        super(mensaje);
        this.usuario = null;
        this.accionDenegada = null;
        this.nivelRequerido = null;
    }
    
    public AccesoException(String mensaje, String usuario, String accionDenegada) {
        super(mensaje);
        this.usuario = usuario;
        this.accionDenegada = accionDenegada;
        this.nivelRequerido = null;
    }
    
    public AccesoException(String mensaje, String usuario, String accionDenegada, String nivelRequerido) {
        super(mensaje);
        this.usuario = usuario;
        this.accionDenegada = accionDenegada;
        this.nivelRequerido = nivelRequerido;
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public String getAccionDenegada() {
        return accionDenegada;
    }
    
    public String getNivelRequerido() {
        return nivelRequerido;
    }
    
    /**
     * Obtiene un mensaje detallado que incluye información de seguridad
     */
    public String getMensajeDetallado() {
        StringBuilder sb = new StringBuilder(getMessage());
        
        if (usuario != null || accionDenegada != null || nivelRequerido != null) {
            sb.append(" (");
            
            if (usuario != null) {
                sb.append("Usuario: ").append(usuario);
                if (accionDenegada != null || nivelRequerido != null) sb.append(", ");
            }
            
            if (accionDenegada != null) {
                sb.append("Acción: ").append(accionDenegada);
                if (nivelRequerido != null) sb.append(", ");
            }
            
            if (nivelRequerido != null) {
                sb.append("Nivel requerido: ").append(nivelRequerido);
            }
            
            sb.append(")");
        }
        
        return sb.toString();
    }
}