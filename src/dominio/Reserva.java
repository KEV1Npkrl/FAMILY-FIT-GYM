package dominio;

import java.time.LocalDateTime;

public class Reserva {
    private int idProgramacion;            // INT (parte de PK y FK)
    private String numDocumento;           // VARCHAR(20) (parte de PK y FK a SOCIO)
    private LocalDateTime fechaHoraReserva;// DATETIME

    public Reserva() {}

    public Reserva(int idProgramacion, String numDocumento, LocalDateTime fechaHoraReserva) {
        this.idProgramacion = idProgramacion;
        this.numDocumento = numDocumento;
        this.fechaHoraReserva = fechaHoraReserva;
    }

    public int getIdProgramacion() { return idProgramacion; }
    public void setIdProgramacion(int idProgramacion) { this.idProgramacion = idProgramacion; }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }

    public LocalDateTime getFechaHoraReserva() { return fechaHoraReserva; }
    public void setFechaHoraReserva(LocalDateTime fechaHoraReserva) { this.fechaHoraReserva = fechaHoraReserva; }
}
