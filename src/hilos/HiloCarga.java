package hilos;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo para cargar datos de manera asíncrona con indicador visual de progreso
 * Útil para operaciones que toman tiempo (consultas BD, reportes, etc.)
 */
public class HiloCarga<T> extends Thread {
    
    // Interfaces funcionales para callbacks
    public interface TareaCarga<T> {
        T cargar(ProgressCallback callback) throws Exception;
    }
    
    public interface CallbackResultado<T> {
        void onExito(T resultado);
        void onError(Exception error);
        void onProgreso(int porcentaje, String mensaje);
    }
    
    public interface ProgressCallback {
        void actualizarProgreso(int porcentaje, String mensaje);
        boolean esCancelado();
    }
    
    private final TareaCarga<T> tarea;
    private final CallbackResultado<T> callback;
    private final AtomicBoolean cancelado = new AtomicBoolean(false);
    private final JProgressBar progressBar;
    private final JLabel labelEstado;
    
    private volatile int porcentajeActual = 0;
    private volatile String mensajeActual = "Iniciando...";
    
    public HiloCarga(TareaCarga<T> tarea, CallbackResultado<T> callback, 
                     JProgressBar progressBar, JLabel labelEstado) {
        super("HiloCarga");
        this.tarea = tarea;
        this.callback = callback;
        this.progressBar = progressBar;
        this.labelEstado = labelEstado;
        setDaemon(true);
    }
    
    @Override
    public void run() {
        try {
            // Crear callback de progreso
            ProgressCallback progressCallback = new ProgressCallback() {
                @Override
                public void actualizarProgreso(int porcentaje, String mensaje) {
                    porcentajeActual = porcentaje;
                    mensajeActual = mensaje;
                    
                    SwingUtilities.invokeLater(() -> {
                        if (progressBar != null) {
                            progressBar.setValue(porcentaje);
                            progressBar.setString(porcentaje + "%");
                        }
                        if (labelEstado != null) {
                            labelEstado.setText(mensaje);
                        }
                        if (callback != null) {
                            callback.onProgreso(porcentaje, mensaje);
                        }
                    });
                }
                
                @Override
                public boolean esCancelado() {
                    return cancelado.get() || Thread.currentThread().isInterrupted();
                }
            };
            
            // Ejecutar la tarea
            T resultado = tarea.cargar(progressCallback);
            
            // Verificar si fue cancelado
            if (cancelado.get()) {
                return;
            }
            
            // Notificar éxito en el EDT
            SwingUtilities.invokeLater(() -> {
                if (callback != null) {
                    callback.onExito(resultado);
                }
            });
            
        } catch (Exception e) {
            // Manejar error en el EDT
            SwingUtilities.invokeLater(() -> {
                if (callback != null) {
                    callback.onError(e);
                }
            });
        }
    }
    
    /**
     * Cancela la operación de carga
     */
    public void cancelar() {
        cancelado.set(true);
        interrupt();
    }
    
    /**
     * Verifica si la operación fue cancelada
     */
    public boolean esCancelado() {
        return cancelado.get();
    }
    
    /**
     * Obtiene el porcentaje actual de progreso
     */
    public int getPorcentajeActual() {
        return porcentajeActual;
    }
    
    /**
     * Obtiene el mensaje actual de estado
     */
    public String getMensajeActual() {
        return mensajeActual;
    }
    
    /**
     * Builder para crear fácilmente hilos de carga
     */
    public static class Builder<T> {
        private TareaCarga<T> tarea;
        private CallbackResultado<T> callback;
        private JProgressBar progressBar;
        private JLabel labelEstado;
        
        public Builder<T> tarea(TareaCarga<T> tarea) {
            this.tarea = tarea;
            return this;
        }
        
        public Builder<T> callback(CallbackResultado<T> callback) {
            this.callback = callback;
            return this;
        }
        
        public Builder<T> progressBar(JProgressBar progressBar) {
            this.progressBar = progressBar;
            return this;
        }
        
        public Builder<T> labelEstado(JLabel labelEstado) {
            this.labelEstado = labelEstado;
            return this;
        }
        
        public HiloCarga<T> construir() {
            return new HiloCarga<>(tarea, callback, progressBar, labelEstado);
        }
        
        public HiloCarga<T> construirYEjecutar() {
            HiloCarga<T> hilo = construir();
            hilo.start();
            return hilo;
        }
    }
    
    /**
     * Método estático para crear el builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
    
    /**
     * Pool de hilos para múltiples cargas concurrentes
     */
    public static class PoolCarga {
        private static final ExecutorService executor = 
            Executors.newFixedThreadPool(3, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("PoolCarga-" + System.currentTimeMillis() + "-" + Thread.currentThread().hashCode());
                return t;
            });
        
        /**
         * Ejecuta una tarea de carga en el pool
         */
        public static <T> Future<?> ejecutar(HiloCarga<T> hiloCarga) {
            return executor.submit(hiloCarga);
        }
        
        /**
         * Cierra el pool de hilos
         */
        public static void cerrar() {
            executor.shutdown();
        }
    }
}