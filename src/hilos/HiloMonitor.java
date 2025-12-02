package hilos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Hilo monitor del sistema que verifica periódicamente el estado de la aplicación
 * Monitorea memoria, conexiones BD, hilos activos y otros recursos del sistema
 */
public class HiloMonitor extends Thread {
    
    /**
     * Interfaz para callbacks de monitoreo
     */
    public interface MonitorCallback {
        void onEstadoSistema(EstadoSistema estado);
        void onAdvertencia(TipoAdvertencia tipo, String mensaje);
        void onErrorCritico(String mensaje, Exception causa);
    }
    
    /**
     * Estado actual del sistema
     */
    public static class EstadoSistema {
        public final long memoriaUsada;
        public final long memoriaTotal;
        public final long memoriaLibre;
        public final int hilosActivos;
        public final LocalDateTime timestamp;
        public final boolean conexionBDActiva;
        public final double porcentajeMemoria;
        
        public EstadoSistema(long memoriaUsada, long memoriaTotal, long memoriaLibre,
                           int hilosActivos, boolean conexionBDActiva) {
            this.memoriaUsada = memoriaUsada;
            this.memoriaTotal = memoriaTotal;
            this.memoriaLibre = memoriaLibre;
            this.hilosActivos = hilosActivos;
            this.conexionBDActiva = conexionBDActiva;
            this.timestamp = LocalDateTime.now();
            this.porcentajeMemoria = (double) memoriaUsada / memoriaTotal * 100;
        }
        
        @Override
        public String toString() {
            return String.format("Sistema[Memoria: %.1f%% (%d/%d MB), Hilos: %d, BD: %s]",
                porcentajeMemoria,
                memoriaUsada / (1024 * 1024),
                memoriaTotal / (1024 * 1024),
                hilosActivos,
                conexionBDActiva ? "OK" : "ERROR");
        }
    }
    
    /**
     * Tipos de advertencias del sistema
     */
    public enum TipoAdvertencia {
        MEMORIA_ALTA("Uso alto de memoria"),
        HILOS_EXCESIVOS("Demasiados hilos activos"),
        CONEXION_BD_PERDIDA("Conexión a BD perdida"),
        RENDIMIENTO_DEGRADADO("Rendimiento degradado");
        
        private final String descripcion;
        
        TipoAdvertencia(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    private final AtomicBoolean ejecutando = new AtomicBoolean(true);
    private final MonitorCallback callback;
    private final int intervaloMs;
    private final AtomicLong contadorCiclos = new AtomicLong(0);
    
    // Umbrales configurables
    private final double umbralMemoria;
    private final int umbralHilos;
    
    public HiloMonitor(MonitorCallback callback, int intervaloMs, 
                      double umbralMemoria, int umbralHilos) {
        super("HiloMonitor");
        this.callback = callback;
        this.intervaloMs = intervaloMs;
        this.umbralMemoria = umbralMemoria;
        this.umbralHilos = umbralHilos;
        setDaemon(true);
    }
    
    /**
     * Constructor con valores por defecto
     */
    public HiloMonitor(MonitorCallback callback) {
        this(callback, 30000, 80.0, 50); // 30 seg, 80% memoria, 50 hilos
    }
    
    /**
     * Constructor sin callback (solo logging)
     */
    public HiloMonitor() {
        this(new MonitorCallback() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            @Override
            public void onEstadoSistema(EstadoSistema estado) {
                System.out.printf("[%s] %s%n", 
                    estado.timestamp.format(fmt), estado.toString());
            }
            
            @Override
            public void onAdvertencia(TipoAdvertencia tipo, String mensaje) {
                System.out.printf("ADVERTENCIA [%s]: %s%n", tipo, mensaje);
            }
            
            @Override
            public void onErrorCritico(String mensaje, Exception causa) {
                System.err.printf("ERROR CRÍTICO: %s%n", mensaje);
                if (causa != null) {
                    causa.printStackTrace();
                }
            }
        });
    }
    
    @Override
    public void run() {
        System.out.println("HiloMonitor iniciado (intervalo: " + intervaloMs + "ms)");
        
        while (ejecutando.get() && !isInterrupted()) {
            try {
                // Recopilar información del sistema
                EstadoSistema estado = recopilarEstadoSistema();
                
                // Incrementar contador de ciclos
                contadorCiclos.incrementAndGet();
                
                // Notificar estado al callback
                if (callback != null) {
                    callback.onEstadoSistema(estado);
                }
                
                // Verificar advertencias
                verificarAdvertencias(estado);
                
                // Esperar hasta el próximo ciclo
                Thread.sleep(intervaloMs);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                if (callback != null) {
                    callback.onErrorCritico("Error en ciclo de monitoreo", e);
                } else {
                    System.err.println("Error en HiloMonitor: " + e.getMessage());
                    e.printStackTrace();
                }
                
                try {
                    Thread.sleep(intervaloMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("HiloMonitor terminado (ciclos ejecutados: " + contadorCiclos.get() + ")");
    }
    
    /**
     * Recopila información actual del sistema
     */
    private EstadoSistema recopilarEstadoSistema() {
        Runtime runtime = Runtime.getRuntime();
        
        long memoriaTotal = runtime.totalMemory();
        long memoriaLibre = runtime.freeMemory();
        long memoriaUsada = memoriaTotal - memoriaLibre;
        
        int hilosActivos = Thread.activeCount();
        
        boolean conexionBDActiva = verificarConexionBD();
        
        return new EstadoSistema(memoriaUsada, memoriaTotal, memoriaLibre, 
                               hilosActivos, conexionBDActiva);
    }
    
    /**
     * Verifica la conexión a la base de datos
     */
    private boolean verificarConexionBD() {
        try {
            // Intentar obtener una conexión rápida
            java.sql.Connection conn = otros.Conexion.iniciarConexion();
            if (conn != null) {
                conn.close();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verifica si hay advertencias basadas en el estado actual
     */
    private void verificarAdvertencias(EstadoSistema estado) {
        if (callback == null) return;
        
        // Verificar uso de memoria
        if (estado.porcentajeMemoria > umbralMemoria) {
            callback.onAdvertencia(TipoAdvertencia.MEMORIA_ALTA,
                String.format("Uso de memoria: %.1f%% (umbral: %.1f%%)", 
                    estado.porcentajeMemoria, umbralMemoria));
        }
        
        // Verificar número de hilos
        if (estado.hilosActivos > umbralHilos) {
            callback.onAdvertencia(TipoAdvertencia.HILOS_EXCESIVOS,
                String.format("Hilos activos: %d (umbral: %d)", 
                    estado.hilosActivos, umbralHilos));
        }
        
        // Verificar conexión BD
        if (!estado.conexionBDActiva) {
            callback.onAdvertencia(TipoAdvertencia.CONEXION_BD_PERDIDA,
                "No se puede conectar a la base de datos");
        }
    }
    
    /**
     * Detiene el monitor de manera segura
     */
    public void detener() {
        ejecutando.set(false);
        interrupt();
    }
    
    /**
     * Obtiene el número de ciclos ejecutados
     */
    public long getCiclosEjecutados() {
        return contadorCiclos.get();
    }
    
    /**
     * Verifica si el monitor está activo
     */
    public boolean estaActivo() {
        return ejecutando.get() && isAlive();
    }
    
    /**
     * Fuerza una recolección de basura
     */
    public void forzarGarbageCollection() {
        System.gc();
        // System.runFinalization() está deprecado y marcado para eliminación
    }
    
    /**
     * Obtiene estadísticas de memoria actual
     */
    public String getEstadisticasMemoria() {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long libre = runtime.freeMemory();
        long usado = total - libre;
        long max = runtime.maxMemory();
        
        return String.format(
            "Memoria: Usada=%d MB, Libre=%d MB, Total=%d MB, Max=%d MB (%.1f%% usado)",
            usado / (1024 * 1024),
            libre / (1024 * 1024), 
            total / (1024 * 1024),
            max / (1024 * 1024),
            (double) usado / total * 100
        );
    }
}