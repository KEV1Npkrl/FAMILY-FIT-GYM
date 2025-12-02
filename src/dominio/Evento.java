package dominio;

import java.time.LocalDateTime;

/**
 * Clase que representa un evento programado en el gimnasio
 */
public class Evento {
    private int id;                    // INT PK - nuevo nombre
    private String nombre;             // VARCHAR(100) - nuevo nombre
    private String descripcion;        // VARCHAR(200) NULL
    private LocalDateTime fechaHora;   // DATETIME - nuevo campo
    private int duracion;              // INT - duración en minutos
    private String instructor;         // VARCHAR(100) - nuevo campo  
    private int cupoMaximo;            // INT - nuevo campo
    private boolean activo;            // BIT - nuevo campo
    
    // Legacy fields para compatibilidad
    private int idEvento;
    private String nombreEvento;

    public Evento() {
        this.activo = true;
    }

    public Evento(String nombre, String descripcion, LocalDateTime fechaHora, 
                  int duracion, String instructor, int cupoMaximo) {
        this.nombre = nombre;
        this.nombreEvento = nombre; // Sincronizar
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.duracion = duracion;
        this.instructor = instructor;
        this.cupoMaximo = cupoMaximo;
        this.activo = true;
    }

    // Legacy constructor
    public Evento(int idEvento, String nombreEvento, String descripcion) {
        this.idEvento = idEvento;
        this.id = idEvento; // Sincronizar
        this.nombreEvento = nombreEvento;
        this.nombre = nombreEvento; // Sincronizar
        this.descripcion = descripcion;
        this.activo = true;
    }

    // Getters y Setters nuevos
    public int getId() {
        return id != 0 ? id : idEvento;
    }
    
    public void setId(int id) {
        this.id = id;
        this.idEvento = id; // Sincronizar
    }
    
    public String getNombre() {
        return nombre != null ? nombre : nombreEvento;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.nombreEvento = nombre; // Sincronizar
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public int getDuracion() {
        return duracion;
    }
    
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
    
    public String getInstructor() {
        return instructor;
    }
    
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
    
    public int getCupoMaximo() {
        return cupoMaximo;
    }
    
    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Legacy getters/setters para compatibilidad
    public int getIdEvento() { 
        return idEvento != 0 ? idEvento : id; 
    }
    
    public void setIdEvento(int idEvento) { 
        this.idEvento = idEvento;
        this.id = idEvento; // Sincronizar
    }

    public String getNombreEvento() { 
        return nombreEvento != null ? nombreEvento : nombre; 
    }
    
    public void setNombreEvento(String nombreEvento) { 
        this.nombreEvento = nombreEvento;
        this.nombre = nombreEvento; // Sincronizar
    }
    
    @Override
    public String toString() {
        return "Evento{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", fechaHora=" + fechaHora +
                ", instructor='" + instructor + '\'' +
                ", cupoMaximo=" + cupoMaximo +
                '}';
    }
}
