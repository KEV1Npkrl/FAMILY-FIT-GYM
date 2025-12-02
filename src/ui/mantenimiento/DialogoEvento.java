package ui.mantenimiento;

import dominio.Evento;
import utilidades.ValidadorUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diálogo para crear y editar eventos programados
 */
public class DialogoEvento extends JDialog {
    
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JButton btnAceptar;
    private JButton btnCancelar;
    
    private boolean aceptado = false;
    
    public DialogoEvento(Window owner, String titulo) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        configurarDialogo();
        inicializarComponentes();
        configurarEventos();
    }
    
    private void configurarDialogo() {
        setSize(450, 350);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel principal con formulario
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        int fila = 0;
        
        // Nombre del evento
        gbc.gridx = 0; gbc.gridy = fila;
        panelPrincipal.add(new JLabel("Nombre del evento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(30);
        panelPrincipal.add(txtNombre, gbc);
        fila++;
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelPrincipal.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtDescripcion = new JTextArea(5, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(300, 120));
        panelPrincipal.add(scrollDesc, gbc);
        
        add(panelPrincipal, BorderLayout.CENTER);
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        
        btnAceptar = new JButton("Aceptar");
        btnAceptar.setPreferredSize(new Dimension(100, 35));
        btnAceptar.setBackground(new Color(34, 139, 34));
        btnAceptar.setForeground(Color.BLACK);
        btnAceptar.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        panelBotones.add(btnCancelar);
        panelBotones.add(btnAceptar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Información adicional
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        
        JLabel lblInfo = new JLabel("<html><div style='color: gray; font-size: 11px;'>" +
            "💡 <b>Consejos:</b><br>" +
            "• El nombre debe ser descriptivo y único<br>" +
            "• La descripción es opcional pero recomendada" +
            "</div></html>");
        panelInfo.add(lblInfo);
        
        add(panelInfo, BorderLayout.NORTH);
    }
    
    private void configurarEventos() {
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aceptado = false;
                dispose();
            }
        });
        
        btnAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validarDatos()) {
                    aceptado = true;
                    dispose();
                }
            }
        });
        
        // Enter en campos de texto ejecuta aceptar
        ActionListener aceptarAction = e -> {
            if (validarDatos()) {
                aceptado = true;
                dispose();
            }
        };
        
        txtNombre.addActionListener(aceptarAction);
    }
    
    private boolean validarDatos() {
        // Validar nombre
        if (!ValidadorUI.validarTexto(this, txtNombre.getText().trim(), "Nombre del evento", 100, true)) {
            txtNombre.requestFocus();
            return false;
        }
        
        // Validar descripción (opcional pero si se proporciona, validar longitud)
        String descripcion = txtDescripcion.getText().trim();
        if (!descripcion.isEmpty() && descripcion.length() > 200) {
            ValidadorUI.mostrarError(this, "La descripción no puede exceder 200 caracteres");
            txtDescripcion.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public void setEvento(Evento evento) {
        // Cargar datos del evento para edición
        txtNombre.setText(evento.getNombreEvento());
        txtDescripcion.setText(evento.getDescripcion() != null ? evento.getDescripcion() : "");
        
        // Cambiar título del diálogo
        setTitle("Editar Evento: " + evento.getNombreEvento());
    }
    
    public Evento getEvento() {
        Evento evento = new Evento();
        
        evento.setNombreEvento(txtNombre.getText().trim());
        evento.setDescripcion(txtDescripcion.getText().trim().isEmpty() ? null : txtDescripcion.getText().trim());
        
        return evento;
    }
    
    public boolean isAceptado() {
        return aceptado;
    }
}