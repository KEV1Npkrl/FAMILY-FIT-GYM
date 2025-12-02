package hilos;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo consumidor para procesar notificaciones y eventos del sistema
 * Implementa el patrón Productor-Consumidor para manejar eventos de manera asíncrona
 */
public class HiloNotificaciones extends Thread {
    
    /**
     * Interfaz para eventos de notificación
     */
    public interface EventoNotificacion {
        void procesar();
        TipoEvento getTipo();
        String getDescripcion();
        long getTimestamp();
    }
    
    /**
     * Tipos de eventos del sistema
     */
    public enum TipoEvento {
        ASISTENCIA_REGISTRADA("Asistencia registrada"),
        MEMBRESIA_VENCIDA("Membresía vencida"), 
        PAGO_RECIBIDO("Pago recibido"),
        NUEVO_SOCIO("Nuevo socio registrado"),
        EVENTO_CANCELADO("Evento cancelado"),
        SISTEMA_INICIADO("Sistema iniciado"),
        SESION_INICIADA("Sesión iniciada"),
        SESION_CERRADA("Sesión cerrada"),
        ERROR_CRITICO("Error crítico del sistema"),
        BACKUP_COMPLETADO("Backup completado");
        
        private final String descripcion;
        
        TipoEvento(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    /**
     * Implementación básica de evento
     */
    public static class Evento implements EventoNotificacion {
        private final TipoEvento tipo;
        private final String descripcion;
        private final long timestamp;
        private final Runnable accion;
        
        public Evento(TipoEvento tipo, String descripcion, Runnable accion) {
            this.tipo = tipo;
            this.descripcion = descripcion;
            this.timestamp = System.currentTimeMillis();
            this.accion = accion;
        }
        
        @Override
        public void procesar() {
            if (accion != null) {
                accion.run();
            }
        }
        
        @Override
        public TipoEvento getTipo() { return tipo; }
        
        @Override
        public String getDescripcion() { return descripcion; }
        
        @Override
        public long getTimestamp() { return timestamp; }
    }
    
    private final BlockingQueue<EventoNotificacion> colaEventos;
    private final AtomicBoolean ejecutando = new AtomicBoolean(true);
    private final ProcessorEventos processor;
    
    /**
     * Interfaz para procesar eventos
     */
    public interface ProcessorEventos {
        void procesar(EventoNotificacion evento);
        void onError(EventoNotificacion evento, Exception error);
    }
    
    public HiloNotificaciones(ProcessorEventos processor) {
        super("HiloNotificaciones");
        this.colaEventos = new LinkedBlockingQueue<>();
        this.processor = processor;
        setDaemon(true);
    }
    
    /**
     * Constructor con processor por defecto que solo imprime a consola
     */
    public HiloNotificaciones() {
        this(new ProcessorEventos() {
            @Override
            public void procesar(EventoNotificacion evento) {
                System.out.printf("[%s] %s: %s%n", 
                    evento.getTipo(), 
                    new java.util.Date(evento.getTimestamp()),
                    evento.getDescripcion());
                evento.procesar();
            }
            
            @Override
            public void onError(EventoNotificacion evento, Exception error) {
                System.err.printf("Error procesando evento %s: %s%n", 
                    evento.getTipo(), error.getMessage());
            }
        });
    }
    
    @Override
    public void run() {
        System.out.println("HiloNotificaciones iniciado");
        
        while (ejecutando.get() && !isInterrupted()) {
            try {
                // Esperar por el siguiente evento (bloquea hasta que haya uno disponible)
                EventoNotificacion evento = colaEventos.take();
                
                // Procesar el evento
                if (processor != null) {
                    processor.procesar(evento);
                } else {
                    evento.procesar(); // Procesamiento directo si no hay processor
                }
                
            } catch (InterruptedException e) {
                // Hilo interrumpido, salir del bucle
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Error durante el procesamiento, continuar con el siguiente evento
                System.err.println("Error en HiloNotificaciones: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("HiloNotificaciones terminado");
    }
    
    /**
     * Añade un evento a la cola para ser procesado
     */
    public boolean enviarEvento(EventoNotificacion evento) {
        if (ejecutando.get()) {
            return colaEventos.offer(evento);
        }
        return false;
    }
    
    /**
     * Método convenience para enviar eventos rápidamente
     */
    public boolean enviarEvento(TipoEvento tipo, String descripcion) {
        return enviarEvento(new Evento(tipo, descripcion, null));
    }
    
    /**
     * Método convenience para enviar eventos con acción
     */
    public boolean enviarEvento(TipoEvento tipo, String descripcion, Runnable accion) {
        return enviarEvento(new Evento(tipo, descripcion, accion));
    }
    
    /**
     * Detiene el hilo de manera segura
     */
    public void detener() {
        ejecutando.set(false);
        interrupt();
    }
    
    /**
     * Procesa todos los eventos pendientes y luego se detiene
     */
    public void detenerConProcesamiento() {
        ejecutando.set(false);
        
        // Procesar eventos restantes
        while (!colaEventos.isEmpty() && !isInterrupted()) {
            try {
                EventoNotificacion evento = colaEventos.poll();
                if (evento != null && processor != null) {
                    processor.procesar(evento);
                }
            } catch (Exception e) {
                System.err.println("Error procesando evento pendiente: " + e.getMessage());
            }
        }
        
        interrupt();
    }
    
    /**
     * Obtiene el número de eventos pendientes en la cola
     */
    public int getEventosPendientes() {
        return colaEventos.size();
    }
    
    /**
     * Verifica si el hilo está activo
     */
    public boolean estaActivo() {
        return ejecutando.get() && isAlive();
    }
    
    /**
     * Singleton para acceso global al sistema de notificaciones
     */
    private static volatile HiloNotificaciones instancia;
    
    public static HiloNotificaciones getInstance() {
        if (instancia == null) {
            synchronized (HiloNotificaciones.class) {
                if (instancia == null) {
                    instancia = new HiloNotificaciones();
                    instancia.start();
                }
            }
        }
        return instancia;
    }
    
    /**
     * Cierra la instancia singleton
     */
    public static void cerrarInstancia() {
        if (instancia != null) {
            instancia.detenerConProcesamiento();
            instancia = null;
        }
    }
}