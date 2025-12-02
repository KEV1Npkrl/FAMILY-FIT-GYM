package dominio;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mapea la tabla MEMBRESIA
 */
public class Membresia {
    private int idMembresia;           // INT PK
    private LocalDate fechaInicio;     // DATE
    private LocalDate fechaFin;        // DATE
    private String estado;             // VARCHAR(50)
    private String numDocumento;       // FK a SOCIO(NumDocumento)
    private int idPlan;                // FK a PLANES(IdPlan)
    private BigDecimal precio;         // DECIMAL para el precio de la membresía
    private boolean activa;            // BOOLEAN para estado activo/inactivo
    
    // Campos adicionales para mostrar información
    private String nombrePlan;        // Para mostrar en la UI
    private String nombreSocio;       // Para mostrar en la UI

    public Membresia() {}

    public Membresia(int idMembresia, LocalDate fechaInicio, LocalDate fechaFin, String estado,
                     String numDocumento, int idPlan) {
        this.idMembresia = idMembresia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.numDocumento = numDocumento;
        this.idPlan = idPlan;
    }

    // Getters y setters
    public int getId() { return idMembresia; }
    public void setId(int idMembresia) { this.idMembresia = idMembresia; }
    
    public int getIdMembresia() { return idMembresia; }
    public void setIdMembresia(int idMembresia) { this.idMembresia = idMembresia; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }
    
    public String getNumDocumentoSocio() { return numDocumento; }
    public void setNumDocumentoSocio(String numDocumento) { this.numDocumento = numDocumento; }

    public int getIdPlan() { return idPlan; }
    public void setIdPlan(int idPlan) { this.idPlan = idPlan; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    
    public String getNombrePlan() { return nombrePlan; }
    public void setNombrePlan(String nombrePlan) { this.nombrePlan = nombrePlan; }
    
    public String getNombreSocio() { return nombreSocio; }
    public void setNombreSocio(String nombreSocio) { this.nombreSocio = nombreSocio; }
}
