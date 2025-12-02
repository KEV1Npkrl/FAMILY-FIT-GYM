package dominio;

import java.time.LocalDateTime;

public class Programacion {
    private int idProgramacion;            // INT PK
    private String lugarEvento;            // VARCHAR(100)
    private LocalDateTime fechaHoraEvento; // DATETIME
    private String numDocumento;           // FK a EMPLEADO(NumDocumento)
    private int idEvento;                  // FK a EVENTOS(IdEvento)

    public Programacion() {}

    public Programacion(int idProgramacion, String lugarEvento, LocalDateTime fechaHoraEvento,
                        String numDocumento, int idEvento) {
        this.idProgramacion = idProgramacion;
        this.lugarEvento = lugarEvento;
        this.fechaHoraEvento = fechaHoraEvento;
        this.numDocumento = numDocumento;
        this.idEvento = idEvento;
    }

    public int getIdProgramacion() { return idProgramacion; }
    public void setIdProgramacion(int idProgramacion) { this.idProgramacion = idProgramacion; }

    public String getLugarEvento() { return lugarEvento; }
    public void setLugarEvento(String lugarEvento) { this.lugarEvento = lugarEvento; }

    public LocalDateTime getFechaHoraEvento() { return fechaHoraEvento; }
    public void setFechaHoraEvento(LocalDateTime fechaHoraEvento) { this.fechaHoraEvento = fechaHoraEvento; }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
}
