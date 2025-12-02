package excepciones;

/**
 * Excepción personalizada para errores específicos del negocio del gimnasio
 */
public class GimnasioException extends Exception {
    
    public enum TipoError {
        MEMBRESIA_VENCIDA("La membresía ha expirado"),
        CUPO_AGOTADO("No hay cupo disponible"),
        HORARIO_INVALIDO("Horario no permitido"),
        PLAN_INCOMPATIBLE("El plan no permite esta acción"),
        SOCIO_INACTIVO("El socio no está activo"),
        EMPLEADO_SIN_PERMISOS("El empleado no tiene permisos suficientes"),
        ASISTENCIA_DUPLICADA("Ya se registró asistencia hoy"),
        EVENTO_CANCELADO("El evento ha sido cancelado"),
        PAGO_PENDIENTE("Hay pagos pendientes");
        
        private final String descripcion;
        
        TipoError(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    private final TipoError tipoError;
    private final String entidad;
    private final String identificador;
    
    public GimnasioException(TipoError tipoError) {
        super(tipoError.getDescripcion());
        this.tipoError = tipoError;
        this.entidad = null;
        this.identificador = null;
    }
    
    public GimnasioException(TipoError tipoError, String entidad, String identificador) {
        super(tipoError.getDescripcion());
        this.tipoError = tipoError;
        this.entidad = entidad;
        this.identificador = identificador;
    }
    
    public GimnasioException(String mensaje, TipoError tipoError) {
        super(mensaje);
        this.tipoError = tipoError;
        this.entidad = null;
        this.identificador = null;
    }
    
    public GimnasioException(String mensaje, TipoError tipoError, Throwable causa) {
        super(mensaje, causa);
        this.tipoError = tipoError;
        this.entidad = null;
        this.identificador = null;
    }
    
    public TipoError getTipoError() {
        return tipoError;
    }
    
    public String getEntidad() {
        return entidad;
    }
    
    public String getIdentificador() {
        return identificador;
    }
    
    /**
     * Obtiene un mensaje detallado que incluye el contexto del error
     */
    public String getMensajeDetallado() {
        StringBuilder sb = new StringBuilder(getMessage());
        
        if (entidad != null || identificador != null) {
            sb.append(" (");
            if (entidad != null) {
                sb.append("Entidad: ").append(entidad);
                if (identificador != null) {
                    sb.append(", ID: ").append(identificador);
                }
            } else if (identificador != null) {
                sb.append("ID: ").append(identificador);
            }
            sb.append(")");
        }
        
        return sb.toString();
    }
    
    /**
     * Verifica si el error es recuperable (puede intentarse nuevamente)
     */
    public boolean esRecuperable() {
        switch (tipoError) {
            case CUPO_AGOTADO:
            case HORARIO_INVALIDO:
            case ASISTENCIA_DUPLICADA:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Obtiene sugerencias para resolver el error
     */
    public String getSugerencia() {
        switch (tipoError) {
            case MEMBRESIA_VENCIDA:
                return "Renueve su membresía para continuar usando los servicios";
            case CUPO_AGOTADO:
                return "Intente en otro horario o inscríbase en la lista de espera";
            case HORARIO_INVALIDO:
                return "Verifique los horarios permitidos para su plan";
            case PLAN_INCOMPATIBLE:
                return "Upgrade su plan o seleccione servicios incluidos";
            case SOCIO_INACTIVO:
                return "Contacte administración para reactivar su cuenta";
            case EMPLEADO_SIN_PERMISOS:
                return "Contacte un administrador para obtener los permisos necesarios";
            case ASISTENCIA_DUPLICADA:
                return "Su asistencia ya fue registrada hoy";
            case EVENTO_CANCELADO:
                return "Seleccione otro evento disponible";
            case PAGO_PENDIENTE:
                return "Complete los pagos pendientes para continuar";
            default:
                return "Contacte el soporte técnico para asistencia";
        }
    }
}