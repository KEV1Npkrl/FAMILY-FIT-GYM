package utilidades;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utilidad para ejecutar tareas pesadas en hilos de segundo plano
 * Evita bloquear el Event Dispatch Thread (EDT) de Swing
 */
public class TareaBackground {
    
    // Pool de hilos para tareas en segundo plano
    private static final ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true); // Hilos daemon para que no impidan el cierre de la aplicación
        t.setName("BackgroundTask-" + System.currentTimeMillis() + "-" + Thread.currentThread().hashCode());
        return t;
    });
    
    /**
     * Ejecuta una tarea en segundo plano y actualiza la UI en el EDT
     * 
     * @param <T> Tipo del resultado de la tarea
     * @param tarea Función que se ejecuta en segundo plano
     * @param onSuccess Función que se ejecuta en el EDT con el resultado exitoso
     * @param onError Función que se ejecuta en el EDT si hay error (opcional)
     * @return Future para cancelar la tarea si es necesario
     */
    public static <T> Future<?> ejecutar(
            Supplier<T> tarea,
            Consumer<T> onSuccess,
            Consumer<Exception> onError) {
        
        return executor.submit(() -> {
            try {
                // Ejecutar tarea en segundo plano
                T resultado = tarea.get();
                
                // Actualizar UI en el EDT
                SwingUtilities.invokeLater(() -> onSuccess.accept(resultado));
                
            } catch (Exception e) {
                // Manejar error en el EDT
                SwingUtilities.invokeLater(() -> {
                    if (onError != null) {
                        onError.accept(e);
                    } else {
                        // Comportamiento por defecto: mostrar error
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, 
                            "Error en tarea en segundo plano: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });
    }
    
    /**
     * Ejecuta una tarea en segundo plano sin resultado de retorno
     * 
     * @param tarea Runnable que se ejecuta en segundo plano
     * @param onSuccess Runnable que se ejecuta en el EDT cuando termina exitosamente
     * @param onError Función que se ejecuta en el EDT si hay error (opcional)
     * @return Future para cancelar la tarea si es necesario
     */
    public static Future<?> ejecutar(
            Runnable tarea,
            Runnable onSuccess,
            Consumer<Exception> onError) {
        
        return ejecutar(
            () -> { tarea.run(); return null; },
            result -> onSuccess.run(),
            onError
        );
    }
    
    /**
     * Ejecuta una tarea simple en segundo plano con manejo básico de errores
     * 
     * @param tarea Runnable que se ejecuta en segundo plano
     * @param onSuccess Runnable que se ejecuta en el EDT cuando termina exitosamente
     * @return Future para cancelar la tarea si es necesario
     */
    public static Future<?> ejecutarSimple(Runnable tarea, Runnable onSuccess) {
        return ejecutar(tarea, onSuccess, null);
    }
    
    /**
     * Ejecuta una tarea con indicador de carga visual
     * 
     * @param <T> Tipo del resultado de la tarea
     * @param padre Componente padre para mostrar el diálogo de carga
     * @param mensaje Mensaje a mostrar durante la carga
     * @param tarea Función que se ejecuta en segundo plano
     * @param onSuccess Función que se ejecuta en el EDT con el resultado exitoso
     * @param onError Función que se ejecuta en el EDT si hay error (opcional)
     */
    public static <T> void ejecutarConCarga(
            java.awt.Component padre,
            String mensaje,
            Supplier<T> tarea,
            Consumer<T> onSuccess,
            Consumer<Exception> onError) {
        
        // Crear diálogo de progreso
        Window ventanaAncestro = SwingUtilities.getWindowAncestor(padre);
        JDialog dialogo;
        if (ventanaAncestro instanceof Frame) {
            dialogo = new JDialog((Frame) ventanaAncestro, "Procesando...", true);
        } else if (ventanaAncestro instanceof Dialog) {
            dialogo = new JDialog((Dialog) ventanaAncestro, "Procesando...", true);
        } else {
            dialogo = new JDialog((Frame) null, "Procesando...", true);
        }
        dialogo.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialogo.setSize(300, 120);
        dialogo.setLocationRelativeTo(padre);
        
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblMensaje = new JLabel(mensaje, SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        panel.add(lblMensaje, java.awt.BorderLayout.CENTER);
        panel.add(progressBar, java.awt.BorderLayout.SOUTH);
        
        dialogo.add(panel);
        
        // Ejecutar tarea
        Future<?> future = ejecutar(
            tarea,
            resultado -> {
                dialogo.dispose();
                onSuccess.accept(resultado);
            },
            error -> {
                dialogo.dispose();
                if (onError != null) {
                    onError.accept(error);
                } else {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(padre,
                        "Error durante la operación: " + error.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        );
        
        // Permitir cancelar la tarea al cerrar el diálogo
        dialogo.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                future.cancel(true);
                dialogo.dispose();
            }
        });
        
        // Mostrar diálogo (bloquea hasta que se complete la tarea)
        dialogo.setVisible(true);
    }
    
    /**
     * Ejecuta múltiples tareas en paralelo
     * 
     * @param tareas Array de tareas a ejecutar
     * @param onAllComplete Función que se ejecuta cuando todas las tareas terminan
     * @param onError Función que se ejecuta si alguna tarea falla
     */
    public static void ejecutarParalelo(
            Runnable[] tareas,
            Runnable onAllComplete,
            Consumer<Exception> onError) {
        
        Future<?>[] futures = new Future<?>[tareas.length];
        
        // Lanzar todas las tareas
        for (int i = 0; i < tareas.length; i++) {
            final int index = i;
            futures[i] = executor.submit(() -> {
                try {
                    tareas[index].run();
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> onError.accept(e));
                    throw e;
                }
            });
        }
        
        // Esperar a que todas terminen en un hilo separado
        executor.submit(() -> {
            try {
                for (Future<?> future : futures) {
                    future.get(); // Esperar a que termine
                }
                // Todas las tareas terminaron exitosamente
                SwingUtilities.invokeLater(onAllComplete);
            } catch (Exception e) {
                // Al menos una tarea falló (ya se manejó arriba)
            }
        });
    }
    
    /**
     * Cierra el pool de hilos de manera ordenada
     * Llamar al cerrar la aplicación
     */
    public static void cerrarPool() {
        executor.shutdown();
        try {
            // Esperar hasta 5 segundos para que terminen las tareas
            if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Clase helper para crear tareas con progreso personalizado
     */
    public static class TareaConProgreso<T> extends SwingWorker<T, String> {
        private final java.util.function.Function<ProgressCallback, T> tarea;
        private final Consumer<T> onSuccess;
        private final Consumer<Exception> onError;
        
        public TareaConProgreso(
                java.util.function.Function<ProgressCallback, T> tarea,
                Consumer<T> onSuccess,
                Consumer<Exception> onError) {
            this.tarea = tarea;
            this.onSuccess = onSuccess;
            this.onError = onError;
        }
        
        @Override
        protected T doInBackground() throws Exception {
            return tarea.apply(new ProgressCallback() {
                @Override
                public void actualizarProgreso(int progreso, String mensaje) {
                    setProgress(progreso);
                    publish(mensaje);
                }
                
                @Override
                public boolean esCancelado() {
                    return isCancelled();
                }
            });
        }
        
        @Override
        protected void process(java.util.List<String> chunks) {
            // Subclases pueden sobrescribir para manejar mensajes de progreso
        }
        
        @Override
        protected void done() {
            try {
                if (!isCancelled()) {
                    T resultado = get();
                    onSuccess.accept(resultado);
                }
            } catch (Exception e) {
                if (onError != null) {
                    onError.accept(e);
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Interfaz para callbacks de progreso
     */
    public interface ProgressCallback {
        void actualizarProgreso(int progreso, String mensaje);
        boolean esCancelado();
    }
}