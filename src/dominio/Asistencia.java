package dominio;

import java.time.LocalDateTime;

public class Asistencia {
    private int idAsistencia;          // INT (parte de PK)
    private String numDocumento;       // VARCHAR(20) (parte de PK y FK a SOCIO)
    private LocalDateTime fechaHoraEntrada; // DATETIME

    public Asistencia() {}

    public Asistencia(int idAsistencia, String numDocumento, LocalDateTime fechaHoraEntrada) {
        this.idAsistencia = idAsistencia;
        this.numDocumento = numDocumento;
        this.fechaHoraEntrada = fechaHoraEntrada;
    }

    public int getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(int idAsistencia) { this.idAsistencia = idAsistencia; }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }

    public LocalDateTime getFechaHoraEntrada() { return fechaHoraEntrada; }
    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) { this.fechaHoraEntrada = fechaHoraEntrada; }
}
