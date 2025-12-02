package ui;

import ui.mantenimiento.PanelPlanes;
import ui.mantenimiento.PanelSocios;
import ui.operaciones.PanelMembresias;
import ui.operaciones.PanelPagos;
import ui.mantenimiento.PanelEmpleados;
import ui.seguridad.PanelCambioPasswordAdmin;
import ui.autenticacion.PantallaLogin;
import ui.consultas.PanelConsultaSocios;
import ui.consultas.PanelConsultaMembresias;
import ui.socios.PanelAsistenciaSocio;
import ui.socios.PanelDatosSocio;
import ui.socios.PanelEventosSocio;
import ui.reportes.PanelReportes;
import dominio.SesionUsuario;
import seguridad.ControladorPermisos;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class VentanaPrincipal extends JFrame {

    private JMenuBar barraMenu;
    private JPanel panelContenido;
    private JLabel lblUsuarioLogueado;

    public VentanaPrincipal() {
        super("Family Fit Gym - Sistema de Gestión");
        verificarSesion();
        configurarVentana();
        inicializarMenu();
        mostrarBienvenida();
    }
    
    private void verificarSesion() {
        SesionUsuario sesion = SesionUsuario.getInstance();
        if (!sesion.esSesionActiva()) {
            JOptionPane.showMessageDialog(null, 
                "No hay sesión activa. Redirigiendo al login...", 
                "Sesión requerida", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        panelContenido = new JPanel(new BorderLayout());
        add(panelContenido, BorderLayout.CENTER);
    }

    private void inicializarMenu() {
        barraMenu = new JMenuBar();

        // MENÚ SEGURIDAD (Común para todos)
        JMenu menuSeguridad = new JMenu("Seguridad");
        
        // Solo Admin puede cambiar contraseñas de otros usuarios
        if (ControladorPermisos.puedeCambiarPasswordDeOtros()) {
            JMenuItem itemCambioPasswordAdmin = new JMenuItem("Cambiar Contraseñas (Admin)");
            itemCambioPasswordAdmin.addActionListener(e -> abrirModuloSeguridad("password-admin"));
            menuSeguridad.add(itemCambioPasswordAdmin);
        }
        
        // Cambio de contraseña para todos
        JMenuItem itemCambioPassword = new JMenuItem("Cambiar Mi Contraseña");
        itemCambioPassword.addActionListener(e -> abrirModuloSeguridad("password"));
        menuSeguridad.add(itemCambioPassword);

        menuSeguridad.addSeparator();
        
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        itemCerrarSesion.addActionListener(e -> cerrarSesion());
        menuSeguridad.add(itemCerrarSesion);

        // MENÚ PARA SOCIOS
        if (ControladorPermisos.esSocio()) {
            JMenu menuSocio = new JMenu("Mi Área");
            
            JMenuItem itemMisAsistencias = new JMenuItem("Marcar Asistencia");
            itemMisAsistencias.addActionListener(e -> abrirModuloSocio("asistencia"));
            menuSocio.add(itemMisAsistencias);
            
            JMenuItem itemMisDatos = new JMenuItem("Ver Mis Datos");
            itemMisDatos.addActionListener(e -> abrirModuloSocio("datos"));
            menuSocio.add(itemMisDatos);
            
            JMenuItem itemEventos = new JMenuItem("Registrarme en Eventos");
            itemEventos.addActionListener(e -> abrirModuloSocio("eventos"));
            menuSocio.add(itemEventos);
            
            barraMenu.add(menuSocio);
        }

        // MENÚ MAESTROS (Solo empleados)
        if (ControladorPermisos.puedeAccederMaestros()) {
            JMenu menuMaestros = new JMenu("Maestros");
            
            JMenuItem itemPlanes = new JMenuItem("Gestión de Planes");
            itemPlanes.addActionListener(e -> abrirModuloMaestros("planes"));
            menuMaestros.add(itemPlanes);
            
            JMenuItem itemMetodosPago = new JMenuItem("Métodos de Pago");
            itemMetodosPago.addActionListener(e -> abrirModuloMaestros("metodos-pago"));
            menuMaestros.add(itemMetodosPago);
            
            barraMenu.add(menuMaestros);
        }

        // MENÚ TRANSACCIONES (Solo empleados)
        if (ControladorPermisos.puedeAccederTransacciones()) {
            JMenu menuTransacciones = new JMenu("Transacciones");
            
            JMenuItem itemSocios = new JMenuItem("Gestión de Socios");
            itemSocios.addActionListener(e -> abrirModuloTransacciones("socios"));
            menuTransacciones.add(itemSocios);
            
            JMenuItem itemEmpleados = new JMenuItem("Gestión de Empleados");
            itemEmpleados.addActionListener(e -> abrirModuloTransacciones("empleados"));
            menuTransacciones.add(itemEmpleados);
            
            JMenuItem itemMembresias = new JMenuItem("Gestión de Membresías");
            itemMembresias.addActionListener(e -> abrirModuloTransacciones("membresias"));
            menuTransacciones.add(itemMembresias);
            
            JMenuItem itemPagos = new JMenuItem("Gestión de Pagos");
            itemPagos.addActionListener(e -> abrirModuloTransacciones("pagos"));
            menuTransacciones.add(itemPagos);
            
            barraMenu.add(menuTransacciones);
        }

        // MENÚ CONSULTAS (Solo empleados)
        if (ControladorPermisos.puedeAccederConsultas()) {
            JMenu menuConsultas = new JMenu("Consultas");
            
            JMenuItem itemConsultaSocios = new JMenuItem("Consultar Socios");
            itemConsultaSocios.addActionListener(e -> abrirModuloConsultas("socios"));
            menuConsultas.add(itemConsultaSocios);
            
            JMenuItem itemConsultaMembresias = new JMenuItem("Consultar Membresías");
            itemConsultaMembresias.addActionListener(e -> abrirModuloConsultas("membresias"));
            menuConsultas.add(itemConsultaMembresias);
            
            JMenuItem itemConsultaPagos = new JMenuItem("Consultar Pagos");
            itemConsultaPagos.addActionListener(e -> abrirModuloConsultas("pagos"));
            menuConsultas.add(itemConsultaPagos);
            
            barraMenu.add(menuConsultas);
        }

        // MENÚ REPORTES (Solo empleados)
        if (ControladorPermisos.puedeVerReportes()) {
            JMenu menuReportes = new JMenu("Reportes");
            
            JMenuItem itemReportesCompleto = new JMenuItem("Sistema de Reportes");
            itemReportesCompleto.addActionListener(e -> abrirModuloReportes("completo"));
            menuReportes.add(itemReportesCompleto);
            
            barraMenu.add(menuReportes);
        }

        // MENÚ SALIR (Común para todos)
        JMenu menuSalir = new JMenu("Salir");
        
        JMenuItem itemSalir = new JMenuItem("Salir del Sistema");
        itemSalir.addActionListener(e -> salirSistema());
        menuSalir.add(itemSalir);

        // Agregar menús comunes a la barra
        barraMenu.add(menuSeguridad);
        barraMenu.add(menuSalir);

        setJMenuBar(barraMenu);
    }

    private void mostrarBienvenida() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Usuario logueado
        SesionUsuario sesion = SesionUsuario.getInstance();
        lblUsuarioLogueado = new JLabel(String.format("Usuario: %s (%s)", 
            sesion.getNombresCompletos(), 
            sesion.getTipoUsuario().toString()));
        lblUsuarioLogueado.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblUsuarioLogueado.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(lblUsuarioLogueado, gbc);
        
        // Título de bienvenida
        JLabel titulo = new JLabel("Bienvenido a Family Fit Gym");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridy = 1; gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(titulo, gbc);
        
        // Información adicional
        JLabel info = new JLabel("Sistema de Gestión Integral");
        info.setFont(new Font("SansSerif", Font.PLAIN, 16));
        info.setForeground(Color.GRAY);
        gbc.gridy = 2; gbc.insets = new Insets(10, 10, 20, 10);
        panel.add(info, gbc);
        
        mostrarPanel(panel);
    }

    private void mostrarPanel(JComponent panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // MÉTODOS PARA MANEJAR NAVEGACIÓN DE MENÚS
    
    private void abrirModuloSeguridad(String modulo) {
        switch (modulo.toLowerCase()) {
            case "password-admin":
                if (ControladorPermisos.verificarYMostrarError(() -> ControladorPermisos.puedeCambiarPasswordDeOtros(), this)) {
                    mostrarPanel(new PanelCambioPasswordAdmin());
                }
                break;
            case "password":
                if (ControladorPermisos.esSocio()) {
                    mostrarPanel(new ui.socios.PanelCambioPassword());
                } else {
                    mostrarPanel(new ui.seguridad.PanelCambioPassword());
                }
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Módulo de seguridad en desarrollo: " + modulo,
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void abrirModuloSocio(String modulo) {
        switch (modulo.toLowerCase()) {
            case "asistencia":
                if (ControladorPermisos.verificarYMostrarError(() -> ControladorPermisos.puedeMarcarAsistencia(), this)) {
                    mostrarPanel(new PanelAsistenciaSocio());
                }
                break;
            case "datos":
                if (ControladorPermisos.verificarYMostrarError(() -> ControladorPermisos.puedeVerDatosPersonales(), this)) {
                    mostrarPanel(new PanelDatosSocio());
                }
                break;
            case "eventos":
                if (ControladorPermisos.verificarYMostrarError(() -> ControladorPermisos.puedeRegistrarseEnEventos(), this)) {
                    mostrarPanel(new PanelEventosSocio());
                }
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Módulo para socios en desarrollo: " + modulo,
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void abrirModuloMaestros(String modulo) {
        if (!ControladorPermisos.verificarYMostrarError(() -> ControladorPermisos.puedeVerReportes(), this)) {
            return;
        }
        
        switch (modulo.toLowerCase()) {
            case "planes":
                mostrarPanel(new PanelPlanes());
                break;
            case "metodos-pago":
                mostrarPanel(new ui.mantenimiento.PanelMetodosPago());
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Módulo de maestros en desarrollo: " + modulo,
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void abrirModuloTransacciones(String modulo) {
        switch (modulo.toLowerCase()) {
            case "socios":
                mostrarPanel(new PanelSocios());
                break;
            case "empleados":
                mostrarPanel(new PanelEmpleados());
                break;
            case "membresias":
                mostrarPanel(new PanelMembresias());
                break;
            case "pagos":
                mostrarPanel(new PanelPagos());
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Módulo de transacciones en desarrollo: " + modulo,
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void abrirModuloConsultas(String modulo) {
        switch (modulo.toLowerCase()) {
            case "socios":
                mostrarPanel(new PanelConsultaSocios());
                break;
            case "membresias":
                mostrarPanel(new PanelConsultaMembresias());
                break;
            case "pagos":
                // Panel de consulta de pagos pendiente de implementar
                JOptionPane.showMessageDialog(this, 
                    "Consulta de pagos en desarrollo",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Módulo de consultas en desarrollo: " + modulo,
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void abrirModuloReportes(String modulo) {
        if (!ControladorPermisos.verificarYMostrarError(() -> ControladorPermisos.puedeVerReportes(), this)) {
            return;
        }
        
        // Siempre abrir el panel completo de reportes
        mostrarPanel(new PanelReportes());
    }
    
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cerrar la sesión?",
            "Confirmar cierre de sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (opcion == JOptionPane.YES_OPTION) {
            SesionUsuario.getInstance().cerrarSesion();
            this.dispose();
            
            // Volver al login
            SwingUtilities.invokeLater(() -> {
                new PantallaLogin().setVisible(true);
            });
        }
    }
    
    private void salirSistema() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea salir del sistema?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (opcion == JOptionPane.YES_OPTION) {
            SesionUsuario.getInstance().cerrarSesion();
            System.exit(0);
        }
    }
}
