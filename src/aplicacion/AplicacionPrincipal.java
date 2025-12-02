package aplicacion;

import ui.autenticacion.PantallaLogin;
import ui.splash.SplashScreen;
import utilidades.TareaBackground;
import hilos.GestorHilos;
import hilos.HiloNotificaciones;

import javax.swing.SwingUtilities;

public class AplicacionPrincipal {
    public static void main(String[] args) {
        // Configurar look and feel del sistema
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el look and feel del sistema");
        }
        
        // Inicializar sistema de hilos
        GestorHilos.inicializar();
        
        // Enviar notificación de inicio de aplicación
        GestorHilos.enviarNotificacion(
            HiloNotificaciones.TipoEvento.SISTEMA_INICIADO,
            "Aplicación Family Fit Gym iniciando..."
        );
        
        // Configurar el cierre ordenado del sistema
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Iniciando cierre ordenado de la aplicación...");
            
            // Cerrar sistemas
            GestorHilos.detenerTodos();
            TareaBackground.cerrarPool();
            
            System.out.println("Aplicación cerrada correctamente");
        }));
        
        // Mostrar splash screen y luego iniciar aplicación
        SplashScreen.mostrar(() -> {
            SwingUtilities.invokeLater(() -> {
                // Enviar notificación de apertura de login
                GestorHilos.enviarNotificacion(
                    HiloNotificaciones.TipoEvento.SESION_INICIADA,
                    "Pantalla de login mostrada"
                );
                
                PantallaLogin login = new PantallaLogin();
                login.setVisible(true);
            });
        });
    }
}
