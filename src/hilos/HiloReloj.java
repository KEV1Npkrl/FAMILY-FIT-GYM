package hilos;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo para mostrar un reloj digital en tiempo real
 * Se puede usar en cualquier componente que necesite mostrar la hora actual
 */
public class HiloReloj extends Thread {
    
    private final JLabel labelHora;
    private final AtomicBoolean ejecutando = new AtomicBoolean(true);
    private final DateTimeFormatter formatter;
    private final int intervaloMs;
    
    /**
     * Constructor con formato personalizado
     * 
     * @param labelHora Label donde se mostrará la hora
     * @param patron Patrón de formato (ej: "HH:mm:ss", "dd/MM/yyyy HH:mm:ss")
     * @param intervaloMs Intervalo de actualización en milisegundos
     */
    public HiloReloj(JLabel labelHora, String patron, int intervaloMs) {
        super("HiloReloj");
        this.labelHora = labelHora;
        this.formatter = DateTimeFormatter.ofPattern(patron);
        this.intervaloMs = intervaloMs;
        setDaemon(true); // Hilo daemon para que no impida el cierre de la aplicación
    }
    
    /**
     * Constructor con formato por defecto HH:mm:ss
     */
    public HiloReloj(JLabel labelHora) {
        this(labelHora, "HH:mm:ss", 1000);
    }
    
    /**
     * Constructor con formato de fecha y hora completo
     */
    public HiloReloj(JLabel labelHora, boolean incluirFecha) {
        this(labelHora, 
             incluirFecha ? "dd/MM/yyyy HH:mm:ss" : "HH:mm:ss", 
             1000);
    }
    
    @Override
    public void run() {
        while (ejecutando.get() && !isInterrupted()) {
            try {
                // Obtener la hora actual
                LocalDateTime ahora = LocalDateTime.now();
                String horaFormateada = ahora.format(formatter);
                
                // Actualizar la UI en el Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    if (labelHora != null && ejecutando.get()) {
                        labelHora.setText(horaFormateada);
                        labelHora.repaint();
                    }
                });
                
                // Esperar hasta la próxima actualización
                Thread.sleep(intervaloMs);
                
            } catch (InterruptedException e) {
                // Hilo interrumpido, salir del bucle
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Manejar cualquier otro error sin detener el hilo
                System.err.println("Error en HiloReloj: " + e.getMessage());
                try {
                    Thread.sleep(intervaloMs); // Esperar antes de intentar de nuevo
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("HiloReloj terminado");
    }
    
    /**
     * Detiene el hilo de manera segura
     */
    public void detener() {
        ejecutando.set(false);
        interrupt();
    }
    
    /**
     * Verifica si el hilo está ejecutándose
     */
    public boolean estaEjecutando() {
        return ejecutando.get() && isAlive();
    }
    
    /**
     * Cambia el intervalo de actualización
     */
    public void cambiarIntervalo(int nuevoIntervaloMs) {
        // Nota: Para cambiar el intervalo durante la ejecución, 
        // se necesitaría una implementación más compleja con wait/notify
        System.out.println("Para cambiar el intervalo, detenga y reinicie el hilo");
    }
    
    /**
     * Método estático para crear y iniciar un reloj fácilmente
     */
    public static HiloReloj crearYIniciar(JLabel label) {
        HiloReloj reloj = new HiloReloj(label);
        reloj.start();
        return reloj;
    }
    
    /**
     * Método estático para crear un reloj con fecha y hora
     */
    public static HiloReloj crearRelojCompleto(JLabel label) {
        HiloReloj reloj = new HiloReloj(label, true);
        reloj.start();
        return reloj;
    }
}