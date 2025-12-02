package dominio;

/**
 * Enumeración que define los tipos de usuario del sistema
 */
public enum TipoUsuario {
    SOCIO("Socio"),
    EMPLEADO("Empleado");
    
    private final String descripcion;
    
    TipoUsuario(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
}