package dominio;

import java.time.LocalDate;

/**
 * Mapea la tabla EMPLEADO (PK y FK: NumDocumento)
 */
public class Empleado extends Persona {
    private String tipoEmpleado; // VARCHAR(20)

    public Empleado() {}
    
    public Empleado(String numDocumento, String nombres, String apellidos, String tipoEmpleado) {
        super();
        setNumDocumento(numDocumento);
        setNombres(nombres);
        setApellidos(apellidos);
        this.tipoEmpleado = tipoEmpleado;
    }

    public String getTipoEmpleado() { return tipoEmpleado; }
    public void setTipoEmpleado(String tipoEmpleado) { this.tipoEmpleado = tipoEmpleado; }
    
    public String getNombreCompleto() {
        return (getNombres() != null ? getNombres() : "") + " " + (getApellidos() != null ? getApellidos() : "");
    }
    
    @Override
    public String toString() {
        return getNombreCompleto() + " (" + getNumDocumento() + ")";
    }
}
