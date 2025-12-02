package dominio;

/**
 * Mapea la tabla EMPLEADO (PK y FK: NumDocumento)
 */
public class Empleado extends Persona {
    private String tipoEmpleado; // VARCHAR(20)

    public Empleado() {}

    public String getTipoEmpleado() { return tipoEmpleado; }
    public void setTipoEmpleado(String tipoEmpleado) { this.tipoEmpleado = tipoEmpleado; }
}
