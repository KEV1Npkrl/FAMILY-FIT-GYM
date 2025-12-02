package hilos;

/**
 * Clase utilitaria para gestionar y controlar todos los hilos del sistema
 * Proporciona métodos convenientes para iniciar, detener y monitorear hilos
 */
public class GestorHilos {
    
    // Referencias a los hilos del sistema
    private static HiloMonitor hiloMonitor;
    private static HiloNotificaciones hiloNotificaciones;
    
    // Estado de inicialización
    private static boolean inicializado = false;
    
    /**
     * Inicializa todos los hilos del sistema
     */
    public static synchronized void inicializar() {
        if (inicializado) {
            System.out.println("GestorHilos ya está inicializado");
            return;
        }
        
        System.out.println("Inicializando hilos del sistema...");
        
        try {
            // Inicializar hilo de notificaciones
            hiloNotificaciones = HiloNotificaciones.getInstance();
            
            // Inicializar hilo monitor con callback personalizado
            hiloMonitor = new HiloMonitor(new HiloMonitor.MonitorCallback() {
                @Override
                public void onEstadoSistema(HiloMonitor.EstadoSistema estado) {
                    // Enviar notificación del estado cada 10 ciclos para no saturar
                    if (hiloMonitor.getCiclosEjecutados() % 10 == 0) {
                        HiloNotificaciones.getInstance().enviarEvento(
                            HiloNotificaciones.TipoEvento.SISTEMA_INICIADO,
                            "Estado del sistema: " + estado.toString()
                        );
                    }
                }
                
                @Override
                public void onAdvertencia(HiloMonitor.TipoAdvertencia tipo, String mensaje) {
                    System.out.println("ADVERTENCIA SISTEMA: " + mensaje);
                    HiloNotificaciones.getInstance().enviarEvento(
                        HiloNotificaciones.TipoEvento.ERROR_CRITICO,
                        "Advertencia: " + tipo + " - " + mensaje
                    );
                }
                
                @Override
                public void onErrorCritico(String mensaje, Exception causa) {
                    System.err.println("ERROR CRÍTICO SISTEMA: " + mensaje);
                    if (causa != null) {
                        causa.printStackTrace();
                    }
                }
            });
            
            hiloMonitor.start();
            
            // Registrar shutdown hook para cierre ordenado
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Ejecutando cierre ordenado de hilos...");
                detenerTodos();
            }));
            
            inicializado = true;
            
            // Enviar notificación de sistema iniciado
            hiloNotificaciones.enviarEvento(
                HiloNotificaciones.TipoEvento.SISTEMA_INICIADO,
                "Todos los hilos del sistema iniciados correctamente"
            );
            
            System.out.println("Hilos del sistema inicializados correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al inicializar hilos del sistema: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error crítico al inicializar hilos", e);
        }
    }
    
    /**
     * Detiene todos los hilos de manera ordenada
     */
    public static synchronized void detenerTodos() {
        if (!inicializado) {
            return;
        }
        
        System.out.println("Deteniendo hilos del sistema...");
        
        try {
            // Enviar notificación de cierre
            if (hiloNotificaciones != null && hiloNotificaciones.estaActivo()) {
                hiloNotificaciones.enviarEvento(
                    HiloNotificaciones.TipoEvento.SESION_CERRADA,
                    "Sistema cerrándose - deteniendo hilos"
                );
                
                // Dar tiempo para procesar la notificación
                Thread.sleep(500);
            }
            
            // Detener monitor
            if (hiloMonitor != null && hiloMonitor.estaActivo()) {
                hiloMonitor.detener();
                hiloMonitor.join(2000); // Esperar máximo 2 segundos
            }
            
            // Detener notificaciones (debe ser el último)
            if (hiloNotificaciones != null && hiloNotificaciones.estaActivo()) {
                HiloNotificaciones.cerrarInstancia();
            }
            
            // Cerrar pools de hilos
            utilidades.TareaBackground.cerrarPool();
            HiloCarga.PoolCarga.cerrar();
            
            System.out.println("Todos los hilos detenidos correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al detener hilos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            inicializado = false;
        }
    }
    
    /**
     * Obtiene información del estado de todos los hilos
     */
    public static String getEstadoHilos() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADO DE HILOS DEL SISTEMA ===\n");
        
        if (!inicializado) {
            sb.append("Sistema no inicializado\n");
            return sb.toString();
        }
        
        // Estado del monitor
        if (hiloMonitor != null) {
            sb.append(String.format("HiloMonitor: %s (ciclos: %d)\n",
                hiloMonitor.estaActivo() ? "ACTIVO" : "INACTIVO",
                hiloMonitor.getCiclosEjecutados()));
            sb.append("  ").append(hiloMonitor.getEstadisticasMemoria()).append("\n");
        } else {
            sb.append("HiloMonitor: NO CREADO\n");
        }
        
        // Estado de notificaciones
        if (hiloNotificaciones != null) {
            sb.append(String.format("HiloNotificaciones: %s (pendientes: %d)\n",
                hiloNotificaciones.estaActivo() ? "ACTIVO" : "INACTIVO",
                hiloNotificaciones.getEventosPendientes()));
        } else {
            sb.append("HiloNotificaciones: NO CREADO\n");
        }
        
        // Hilos totales del sistema
        sb.append(String.format("Total hilos JVM: %d\n", Thread.activeCount()));
        
        return sb.toString();
    }
    
    /**
     * Crea un nuevo HiloReloj y lo devuelve (no lo gestiona automáticamente)
     */
    public static HiloReloj crearReloj(javax.swing.JLabel label) {
        return new HiloReloj(label);
    }
    
    /**
     * Crea un nuevo HiloReloj con fecha y hora
     */
    public static HiloReloj crearRelojCompleto(javax.swing.JLabel label) {
        return new HiloReloj(label, true);
    }
    
    /**
     * Crea un nuevo HiloCarga con builder pattern
     */
    public static <T> HiloCarga.Builder<T> crearCarga() {
        return HiloCarga.builder();
    }
    
    /**
     * Envía una notificación al sistema
     */
    public static boolean enviarNotificacion(HiloNotificaciones.TipoEvento tipo, String mensaje) {
        if (hiloNotificaciones != null && hiloNotificaciones.estaActivo()) {
            return hiloNotificaciones.enviarEvento(tipo, mensaje);
        }
        return false;
    }
    
    /**
     * Envía una notificación con acción
     */
    public static boolean enviarNotificacion(HiloNotificaciones.TipoEvento tipo, String mensaje, Runnable accion) {
        if (hiloNotificaciones != null && hiloNotificaciones.estaActivo()) {
            return hiloNotificaciones.enviarEvento(tipo, mensaje, accion);
        }
        return false;
    }
    
    /**
     * Fuerza recolección de basura
     */
    public static void forzarLimpiezaMemoria() {
        if (hiloMonitor != null) {
            hiloMonitor.forzarGarbageCollection();
            
            // Enviar notificación
            enviarNotificacion(
                HiloNotificaciones.TipoEvento.SISTEMA_INICIADO,
                "Limpieza de memoria ejecutada manualmente"
            );
        }
    }
    
    /**
     * Verifica si el sistema de hilos está inicializado
     */
    public static boolean estaInicializado() {
        return inicializado;
    }
    
    /**
     * Obtiene referencia al hilo de notificaciones
     */
    public static HiloNotificaciones getHiloNotificaciones() {
        return hiloNotificaciones;
    }
    
    /**
     * Obtiene referencia al hilo monitor
     */
    public static HiloMonitor getHiloMonitor() {
        return hiloMonitor;
    }
}