package dominio;

import java.time.LocalDateTime;

public class Programacion {
    private int idProgramacion;            // INT PK
    private int idEvento;                  // FK a EVENTOS(IdEvento)
    private LocalDateTime fechaHoraEvento; // DATETIME (FechaHoraEvento)
    private String lugarEvento;            // VARCHAR(100) (LugarEvento)
    private String numDocumento;           // VARCHAR(20) FK a EMPLEADO
    
    // Para mostrar información relacionada
    private String nombreEvento;
    private int reservados;

    public Programacion() {}

    public Programacion(int idEvento, LocalDateTime fechaHoraEvento, String lugarEvento, 
                        String numDocumento) {
        this.idEvento = idEvento;
        this.fechaHoraEvento = fechaHoraEvento;
        this.lugarEvento = lugarEvento;
        this.numDocumento = numDocumento;
    }

    public int getIdProgramacion() { return idProgramacion; }
    public void setIdProgramacion(int idProgramacion) { this.idProgramacion = idProgramacion; }

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }

    public LocalDateTime getFechaHoraEvento() { return fechaHoraEvento; }
    public void setFechaHoraEvento(LocalDateTime fechaHoraEvento) { this.fechaHoraEvento = fechaHoraEvento; }

    public String getLugarEvento() { return lugarEvento; }
    public void setLugarEvento(String lugarEvento) { this.lugarEvento = lugarEvento; }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }
    
    public String getNombreEvento() { return nombreEvento; }
    public void setNombreEvento(String nombreEvento) { this.nombreEvento = nombreEvento; }

    public int getReservados() { return reservados; }
    public void setReservados(int reservados) { this.reservados = reservados; }
    
    @Override
    public String toString() {
        return "Programacion{" +
                "idProgramacion=" + idProgramacion +
                ", nombreEvento='" + nombreEvento + '\'' +
                ", fechaHoraEvento=" + fechaHoraEvento +
                ", lugarEvento='" + lugarEvento + '\'' +
                ", numDocumento='" + numDocumento + '\'' +
                '}';
    }
}
