package dominio;

import java.time.LocalDate;

/**
 * Mapea la tabla PERSONA (PK: NumDocumento)
 */
public abstract class Persona {
    private String numDocumento;           // PK (VARCHAR(20))
    private String nombres;                // VARCHAR(100)
    private String apellidos;              // VARCHAR(100)
    private String passwordHass;           // VARCHAR(60)
    private LocalDate fechaRegistro;       // DATE
    private String celular;                // VARCHAR(20)
    private String correo;                 // VARCHAR(100)

    public Persona() {}

    public Persona(String numDocumento, String nombres, String apellidos, String passwordHass,
                   LocalDate fechaRegistro, String celular, String correo) {
        this.numDocumento = numDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.passwordHass = passwordHass;
        this.fechaRegistro = fechaRegistro;
        this.celular = celular;
        this.correo = correo;
    }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getPasswordHass() { return passwordHass; }
    public void setPasswordHass(String passwordHass) { this.passwordHass = passwordHass; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    // Alias para compatibilidad
    public String getEmail() { return correo; }
    public void setEmail(String email) { this.correo = email; }
    
    public String getTelefono() { return celular; }
    public void setTelefono(String telefono) { this.celular = telefono; }
}
