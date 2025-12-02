package excepciones;

/**
 * Excepción personalizada para errores de conexión a la base de datos
 */
public class ConexionException extends RuntimeException {
    
    private final String servidor;
    private final String baseDatos;
    
    public ConexionException(String mensaje) {
        super(mensaje);
        this.servidor = null;
        this.baseDatos = null;
    }
    
    public ConexionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.servidor = null;
        this.baseDatos = null;
    }
    
    public ConexionException(String mensaje, String servidor, String baseDatos) {
        super(mensaje);
        this.servidor = servidor;
        this.baseDatos = baseDatos;
    }
    
    public ConexionException(String mensaje, String servidor, String baseDatos, Throwable causa) {
        super(mensaje, causa);
        this.servidor = servidor;
        this.baseDatos = baseDatos;
    }
    
    public String getServidor() {
        return servidor;
    }
    
    public String getBaseDatos() {
        return baseDatos;
    }
    
    /**
     * Obtiene un mensaje detallado que incluye información del servidor y BD
     */
    public String getMensajeDetallado() {
        StringBuilder sb = new StringBuilder(getMessage());
        
        if (servidor != null || baseDatos != null) {
            sb.append(" (");
            if (servidor != null) {
                sb.append("Servidor: ").append(servidor);
                if (baseDatos != null) {
                    sb.append(", ");
                }
            }
            if (baseDatos != null) {
                sb.append("BD: ").append(baseDatos);
            }
            sb.append(")");
        }
        
        return sb.toString();
    }
}