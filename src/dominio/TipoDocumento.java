package dominio;

/**
 * Tipos de documento válidos en el sistema
 */
public enum TipoDocumento {
    DNI("DNI"),
    PASAPORTE("PASAPORTE"), 
    CARNET_EXTRANJERIA("CARNET_EXTRANJERIA");
    
    private final String valor;
    
    TipoDocumento(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
    
    @Override
    public String toString() {
        return valor;
    }
}