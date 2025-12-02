package dominio;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * Mapea la tabla PLANES
 */
public abstract class Plan {
    private int idPlan;                // INT PK
    private String nombrePlan;         // VARCHAR(60)
    private int duracionDias;          // INT
    private BigDecimal costo;          // DECIMAL(10,2)
    private String descripcion;        // VARCHAR(200)
    private TipoPlan tipo;             // Derivado para lógica de negocio

    public Plan() {}

    public Plan(int idPlan, String nombrePlan, int duracionDias, BigDecimal costo, String descripcion, TipoPlan tipo) {
        this.idPlan = idPlan;
        this.nombrePlan = nombrePlan;
        this.duracionDias = duracionDias;
        this.costo = costo;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    public int getIdPlan() { return idPlan; }
    public void setIdPlan(int idPlan) { this.idPlan = idPlan; }

    public String getNombrePlan() { return nombrePlan; }
    public void setNombrePlan(String nombrePlan) { this.nombrePlan = nombrePlan; }

    // Métodos de compatibilidad para la UI
    public String getNombre() { return nombrePlan; }
    public void setNombre(String nombre) { this.nombrePlan = nombre; }

    public int getDuracionDias() { return duracionDias; }
    public void setDuracionDias(int duracionDias) { this.duracionDias = duracionDias; }

    public BigDecimal getCosto() { return costo; }
    
    // Método de compatibilidad para la UI
    public BigDecimal getPrecio() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoPlan getTipo() { return tipo; }
    public void setTipo(TipoPlan tipo) { this.tipo = tipo; }

    // Políticas comunes expuestas por polimorfismo
    public abstract boolean incluyeFeriados();
    public abstract boolean permiteTrotadoras();
    public abstract boolean permiteCongelamiento();

    /**
     * Valida si el acceso es permitido en el día/horario indicado, considerando feriados.
     */
    public abstract boolean validarAcceso(LocalDateTime fechaHora, DayOfWeek diaSemana, boolean esFeriado);
}
