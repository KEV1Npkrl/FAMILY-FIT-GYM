package dominio;

/**
 * Clase que representa un evento programado en el gimnasio
 * Coincide con la estructura de la tabla EVENTOS en la base de datos
 */
public class Evento {
    private int idEvento;        // INT NOT NULL PK
    private String nombreEvento; // VARCHAR(100) NOT NULL
    private String descripcion;  // VARCHAR(200) NULL

    public Evento() {
    }

    public Evento(String nombreEvento, String descripcion) {
        this.nombreEvento = nombreEvento;
        this.descripcion = descripcion;
    }

    public Evento(int idEvento, String nombreEvento, String descripcion) {
        this.idEvento = idEvento;
        this.nombreEvento = nombreEvento;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdEvento() { 
        return idEvento; 
    }
    
    public void setIdEvento(int idEvento) { 
        this.idEvento = idEvento;
    }

    public String getNombreEvento() { 
        return nombreEvento; 
    }
    
    public void setNombreEvento(String nombreEvento) { 
        this.nombreEvento = nombreEvento;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    @Override
    public String toString() {
        return nombreEvento + (descripcion != null && !descripcion.trim().isEmpty() ? " - " + descripcion : "");
    }
}
