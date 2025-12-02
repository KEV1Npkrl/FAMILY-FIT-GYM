package dominio;

import java.time.LocalDateTime;

/**
 * Clase que representa la asistencia de un socio a un evento
 */
public class AsistenciaEvento {
    private String numDocumento;        // VARCHAR(20) - documento del socio
    private int idEvento;              // INT - id del evento
    private LocalDateTime fechaRegistro; // DATETIME - cuando se registró
    private boolean asistio;           // BIT - si asistió o no
    private LocalDateTime fechaAsistencia; // DATETIME - cuando asistió
    
    // Legacy fields para compatibilidad
    private int idProgramacion;
    private LocalDateTime fechaHoraAsis;

    public AsistenciaEvento() {
        this.asistio = false;
    }

    public AsistenciaEvento(String numDocumento, int idEvento) {
        this.numDocumento = numDocumento;
        this.idEvento = idEvento;
        this.fechaRegistro = LocalDateTime.now();
        this.asistio = false;
    }

    // Legacy constructor
    public AsistenciaEvento(int idProgramacion, String numDocumento, LocalDateTime fechaHoraAsis) {
        this.idProgramacion = idProgramacion;
        this.numDocumento = numDocumento;
        this.fechaHoraAsis = fechaHoraAsis;
    }

    // Getters y Setters nuevos
    public String getNumDocumento() {
        return numDocumento;
    }
    
    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }
    
    public int getIdEvento() {
        return idEvento;
    }
    
    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public boolean isAsistio() {
        return asistio;
    }
    
    public boolean getAsistio() {
        return asistio;
    }
    
    public void setAsistio(boolean asistio) {
        this.asistio = asistio;
    }
    
    public LocalDateTime getFechaAsistencia() {
        return fechaAsistencia;
    }
    
    public void setFechaAsistencia(LocalDateTime fechaAsistencia) {
        this.fechaAsistencia = fechaAsistencia;
    }

    // Legacy getters/setters para compatibilidad
    public int getIdProgramacion() { 
        return idProgramacion != 0 ? idProgramacion : idEvento; 
    }
    
    public void setIdProgramacion(int idProgramacion) { 
        this.idProgramacion = idProgramacion; 
        this.idEvento = idProgramacion; // Sincronizar
    }

    public LocalDateTime getFechaHoraAsis() { 
        return fechaHoraAsis != null ? fechaHoraAsis : fechaAsistencia; 
    }
    
    public void setFechaHoraAsis(LocalDateTime fechaHoraAsis) { 
        this.fechaHoraAsis = fechaHoraAsis; 
        this.fechaAsistencia = fechaHoraAsis; // Sincronizar
    }
    
    @Override
    public String toString() {
        return "AsistenciaEvento{" +
                "numDocumento='" + numDocumento + '\'' +
                ", idEvento=" + idEvento +
                ", fechaRegistro=" + fechaRegistro +
                ", asistio=" + asistio +
                '}';
    }
}
