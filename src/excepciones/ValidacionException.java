package excepciones;

/**
 * Excepción personalizada para errores de validación de datos de negocio
 */
public class ValidacionException extends Exception {
    
    private final String campo;
    private final Object valorInvalido;
    
    public ValidacionException(String mensaje) {
        super(mensaje);
        this.campo = null;
        this.valorInvalido = null;
    }
    
    public ValidacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.campo = null;
        this.valorInvalido = null;
    }
    
    public ValidacionException(String mensaje, String campo, Object valorInvalido) {
        super(mensaje);
        this.campo = campo;
        this.valorInvalido = valorInvalido;
    }
    
    public String getCampo() {
        return campo;
    }
    
    public Object getValorInvalido() {
        return valorInvalido;
    }
    
    /**
     * Obtiene un mensaje detallado que incluye el campo y valor problemático
     */
    public String getMensajeDetallado() {
        StringBuilder sb = new StringBuilder(getMessage());
        
        if (campo != null) {
            sb.append(" (Campo: ").append(campo);
            if (valorInvalido != null) {
                sb.append(", Valor: ").append(valorInvalido);
            }
            sb.append(")");
        }
        
        return sb.toString();
    }
}