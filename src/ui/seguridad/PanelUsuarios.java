package ui.seguridad;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para gestión de usuarios del sistema
 */
public class PanelUsuarios extends JPanel {
    
    public PanelUsuarios() {
        construirUI();
    }
    
    private void construirUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.add(new JLabel("Gestión de Usuarios del Sistema"));
        add(header, BorderLayout.NORTH);
        
        // Contenido principal
        JPanel contenido = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Información temporal
        JLabel lblInfo = new JLabel("<html>" +
            "<div style='text-align: center;'>" +
            "<h2>Módulo de Usuarios</h2>" +
            "<p>Este módulo permitirá:</p>" +
            "<ul>" +
            "<li>• Crear usuarios administradores</li>" +
            "<li>• Asignar roles y permisos</li>" +
            "<li>• Gestionar acceso al sistema</li>" +
            "<li>• Configurar niveles de autorización</li>" +
            "</ul>" +
            "<br><p><strong>En desarrollo...</strong></p>" +
            "</div></html>");
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        contenido.add(lblInfo, gbc);
        
        add(contenido, BorderLayout.CENTER);
    }
}