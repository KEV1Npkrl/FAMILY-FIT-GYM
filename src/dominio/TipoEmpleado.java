package dominio;

/**
 * Enumeración para los tipos de empleados del sistema
 */
public enum TipoEmpleado {
    ADMIN("Admin"),
    CAJERO("Cajero"),
    ENTRENADOR("Entrenador");
    
    private final String descripcion;
    
    TipoEmpleado(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
    
    /**
     * Obtiene el tipo de empleado a partir de la descripción
     */
    public static TipoEmpleado fromDescripcion(String descripcion) {
        for (TipoEmpleado tipo : values()) {
            if (tipo.getDescripcion().equals(descripcion)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de empleado no válido: " + descripcion);
    }
    
    /**
     * Verifica si el tipo de empleado es Admin
     */
    public boolean esAdmin() {
        return this == ADMIN;
    }
    
    /**
     * Verifica si el tipo de empleado puede gestionar otros empleados
     */
    public boolean puedeGestionarEmpleados() {
        return this == ADMIN;
    }
    
    /**
     * Verifica si el tipo de empleado puede cambiar contraseñas de otros usuarios
     */
    public boolean puedeCambiarPasswordDeOtros() {
        return this == ADMIN;
    }
}