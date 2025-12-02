package dominio;

public class MetodoPago {
    private String idMetodoPago;   // VARCHAR(20) PK
    private String nombreMetodo;   // VARCHAR(50)
    private boolean estadoActivo;  // BIT

    public MetodoPago() {}

    public MetodoPago(String idMetodoPago, String nombreMetodo, boolean estadoActivo) {
        this.idMetodoPago = idMetodoPago;
        this.nombreMetodo = nombreMetodo;
        this.estadoActivo = estadoActivo;
    }

    public String getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(String idMetodoPago) { this.idMetodoPago = idMetodoPago; }

    public String getNombreMetodo() { return nombreMetodo; }
    public void setNombreMetodo(String nombreMetodo) { this.nombreMetodo = nombreMetodo; }

    public boolean isEstadoActivo() { return estadoActivo; }
    public void setEstadoActivo(boolean estadoActivo) { this.estadoActivo = estadoActivo; }
    
    @Override
    public String toString() {
        return nombreMetodo;
    }
}
