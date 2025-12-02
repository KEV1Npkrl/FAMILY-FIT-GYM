package dominio;

import java.time.LocalDate;

/**
 * Mapea la tabla SOCIO (PK y FK: NumDocumento)
 * Extiende Persona con campos específicos de socio.
 */
public class Socio extends Persona {
    private LocalDate fechaIngreso;
    private boolean activo;
    
    public Socio() {}

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
