package aplicacion;

import ui.autenticacion.PantallaLogin;

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
        
        // Iniciar aplicación con pantalla de login
        SwingUtilities.invokeLater(() -> {
            PantallaLogin login = new PantallaLogin();
            login.setVisible(true);
        });
    }
}
