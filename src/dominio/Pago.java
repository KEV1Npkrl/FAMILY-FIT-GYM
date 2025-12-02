package dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {
    private int idPago;                    // INT PK
    private LocalDateTime fechaPago;       // DATETIME
    private BigDecimal monto;              // DECIMAL(10,2)
    private String numDocumento;           // FK a EMPLEADO(NumDocumento)
    private String idMetodoPago;           // FK a METODOPAGO(IdMetodoPago)
    private int idMembresia;               // FK a MEMBRESIA(IdMembresia)

    public Pago() {}

    public Pago(int idPago, LocalDateTime fechaPago, BigDecimal monto, String numDocumento, String idMetodoPago, int idMembresia) {
        this.idPago = idPago;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.numDocumento = numDocumento;
        this.idMetodoPago = idMetodoPago;
        this.idMembresia = idMembresia;
    }

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }

    public String getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(String idMetodoPago) { this.idMetodoPago = idMetodoPago; }

    public int getIdMembresia() { return idMembresia; }
    public void setIdMembresia(int idMembresia) { this.idMembresia = idMembresia; }
}
