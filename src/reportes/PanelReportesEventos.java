package reportes;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

/**
 * Panel para mostrar reportes del sistema de eventos
 * Incluye múltiples tipos de reportes con filtros de fecha
 */
public class PanelReportesEventos extends JPanel {
    private Connection conexion;
    private JComboBox<String> comboTipoReporte;
    private JTextField fechaInicio;
    private JTextField fechaFin;
    private JButton btnGenerar;
    private JButton btnExportar;
    private JTable tablaReporte;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollPane;
    private JLabel lblTotalRegistros;
    
    // Tipos de reportes disponibles
    private final String[] TIPOS_REPORTE = {
        "Eventos Programados y Estado",
        "Asistencia por Socio", 
        "Eventos Más Populares",
        "Desempeño de Instructores",
        "Eventos por Período",
        "Socios Sin Participación",
        "Métricas Generales"
    };

    public PanelReportesEventos(Connection conexion) {
        this.conexion = conexion;
        initComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Reportes de Eventos"));

        // Panel superior con controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panelControles.add(new JLabel("Tipo de Reporte:"));
        comboTipoReporte = new JComboBox<>(TIPOS_REPORTE);
        comboTipoReporte.setPreferredSize(new Dimension(200, 25));
        panelControles.add(comboTipoReporte);
        
        panelControles.add(Box.createHorizontalStrut(20));
        
        panelControles.add(new JLabel("Fecha Inicio:"));
        fechaInicio = new JTextField(10);
        fechaInicio.setPreferredSize(new Dimension(120, 25));
        fechaInicio.setText(LocalDate.now().minusMonths(1).toString());
        panelControles.add(fechaInicio);
        
        panelControles.add(new JLabel("Fecha Fin:"));
        fechaFin = new JTextField(10);
        fechaFin.setPreferredSize(new Dimension(120, 25));
        fechaFin.setText(LocalDate.now().toString());
        panelControles.add(fechaFin);
        
        panelControles.add(Box.createHorizontalStrut(20));
        
        btnGenerar = new JButton("Generar Reporte");
        btnGenerar.setBackground(new Color(76, 175, 80));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        panelControles.add(btnGenerar);
        
        btnExportar = new JButton("Exportar a Excel");
        btnExportar.setBackground(new Color(33, 150, 243));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setEnabled(false);
        panelControles.add(btnExportar);

        add(panelControles, BorderLayout.NORTH);

        // Panel central con tabla
        modeloTabla = new DefaultTableModel();
        tablaReporte = new JTable(modeloTabla);
        tablaReporte.setRowHeight(25);
        tablaReporte.setGridColor(new Color(230, 230, 230));
        tablaReporte.setSelectionBackground(new Color(184, 207, 229));
        
        scrollPane = new JScrollPane(tablaReporte);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotalRegistros = new JLabel("Total de registros: 0");
        lblTotalRegistros.setFont(new Font("Arial", Font.BOLD, 12));
        panelInfo.add(lblTotalRegistros);
        add(panelInfo, BorderLayout.SOUTH);
    }

    private void setupLayout() {
        setPreferredSize(new Dimension(1000, 600));
    }

    private void setupEventListeners() {
        btnGenerar.addActionListener(e -> generarReporte());
        btnExportar.addActionListener(e -> exportarReporte());
        
        // Habilitar/deshabilitar filtros de fecha según el tipo de reporte
        comboTipoReporte.addActionListener(e -> {
            String tipoSeleccionado = (String) comboTipoReporte.getSelectedItem();
            boolean usaFechas = "Eventos por Período".equals(tipoSeleccionado);
            fechaInicio.setEnabled(usaFechas);
            fechaFin.setEnabled(usaFechas);
        });
    }

    private void generarReporte() {
        String tipoReporte = (String) comboTipoReporte.getSelectedItem();
        
        try {
            switch (tipoReporte) {
                case "Eventos Programados y Estado":
                    generarReporteEventosEstado();
                    break;
                case "Asistencia por Socio":
                    generarReporteAsistenciaSocio();
                    break;
                case "Eventos Más Populares":
                    generarReporteEventosPopulares();
                    break;
                case "Desempeño de Instructores":
                    generarReporteDesempenoInstructores();
                    break;
                case "Eventos por Período":
                    generarReporteEventosPeriodo();
                    break;
                case "Socios Sin Participación":
                    generarReporteSociosSinParticipacion();
                    break;
                case "Métricas Generales":
                    generarReporteMetricasGenerales();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Tipo de reporte no implementado", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }
            
            btnExportar.setEnabled(modeloTabla.getRowCount() > 0);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReporteEventosEstado() throws SQLException {
        String query = """
            SELECT 
                E.NombreEvento,
                P.LugarEvento,
                P.FechaHoraEvento,
                CONCAT(Em.Nombres, ' ', Em.Apellidos) AS Instructor,
                COUNT(DISTINCT R.NumDocumento) AS TotalReservados,
                COUNT(DISTINCT A.NumDocumento) AS TotalAsistieron,
                CASE 
                    WHEN COUNT(DISTINCT R.NumDocumento) = 0 THEN 0
                    ELSE ROUND(
                        (CAST(COUNT(DISTINCT A.NumDocumento) AS FLOAT) / 
                         CAST(COUNT(DISTINCT R.NumDocumento) AS FLOAT)) * 100, 2
                    )
                END AS PorcentajeAsistencia,
                CASE 
                    WHEN P.FechaHoraEvento > GETDATE() THEN 'PROGRAMADO'
                    WHEN P.FechaHoraEvento <= GETDATE() AND COUNT(DISTINCT A.NumDocumento) > 0 THEN 'REALIZADO'
                    WHEN P.FechaHoraEvento <= GETDATE() AND COUNT(DISTINCT A.NumDocumento) = 0 THEN 'NO_REALIZADO'
                    ELSE 'PENDIENTE'
                END AS EstadoEvento
            FROM EVENTOS E
            INNER JOIN PROGRAMACION P ON E.IdEvento = P.IdEvento
            LEFT JOIN EMPLEADO Em ON P.NumDocumento = Em.NumDocumento
            LEFT JOIN RESERVA R ON P.IdProgramacion = R.IdProgramacion
            LEFT JOIN ASISTENCIAEVENTO A ON P.IdProgramacion = A.IdProgramacion 
                AND R.NumDocumento = A.NumDocumento
            GROUP BY 
                E.IdEvento, E.NombreEvento, P.IdProgramacion, P.LugarEvento, 
                P.FechaHoraEvento, Em.Nombres, Em.Apellidos
            ORDER BY P.FechaHoraEvento DESC
            """;

        String[] columnas = {"Evento", "Lugar", "Fecha/Hora", "Instructor", "Reservados", "Asistieron", "% Asistencia", "Estado"};
        ejecutarConsultaReporte(query, columnas);
    }

    private void generarReporteAsistenciaSocio() throws SQLException {
        String query = """
            SELECT 
                S.NumDocumento,
                CONCAT(S.Nombres, ' ', S.Apellidos) AS NombreSocio,
                COUNT(DISTINCT R.IdProgramacion) AS TotalReservas,
                COUNT(DISTINCT A.IdProgramacion) AS TotalAsistencias,
                CASE 
                    WHEN COUNT(DISTINCT R.IdProgramacion) = 0 THEN 0
                    ELSE ROUND(
                        (CAST(COUNT(DISTINCT A.IdProgramacion) AS FLOAT) / 
                         CAST(COUNT(DISTINCT R.IdProgramacion) AS FLOAT)) * 100, 2
                    )
                END AS PorcentajeAsistencia
            FROM SOCIO S
            LEFT JOIN RESERVA R ON S.NumDocumento = R.NumDocumento
            LEFT JOIN ASISTENCIAEVENTO A ON R.IdProgramacion = A.IdProgramacion 
                AND R.NumDocumento = A.NumDocumento
            GROUP BY S.NumDocumento, S.Nombres, S.Apellidos
            HAVING COUNT(DISTINCT R.IdProgramacion) > 0
            ORDER BY TotalAsistencias DESC, PorcentajeAsistencia DESC
            """;

        String[] columnas = {"Documento", "Socio", "Total Reservas", "Total Asistencias", "% Asistencia"};
        ejecutarConsultaReporte(query, columnas);
    }

    private void generarReporteEventosPopulares() throws SQLException {
        String query = """
            SELECT 
                E.NombreEvento,
                COUNT(DISTINCT P.IdProgramacion) AS VecesProgramado,
                COUNT(DISTINCT CONCAT(R.IdProgramacion, '-', R.NumDocumento)) AS TotalReservas,
                COUNT(DISTINCT CONCAT(A.IdProgramacion, '-', A.NumDocumento)) AS TotalAsistencias,
                CASE 
                    WHEN COUNT(DISTINCT P.IdProgramacion) = 0 THEN 0
                    ELSE ROUND(
                        CAST(COUNT(DISTINCT CONCAT(R.IdProgramacion, '-', R.NumDocumento)) AS FLOAT) / 
                        CAST(COUNT(DISTINCT P.IdProgramacion) AS FLOAT), 1
                    )
                END AS PromedioReservas,
                CASE 
                    WHEN COUNT(DISTINCT CONCAT(R.IdProgramacion, '-', R.NumDocumento)) = 0 THEN 0
                    ELSE ROUND(
                        (CAST(COUNT(DISTINCT CONCAT(A.IdProgramacion, '-', A.NumDocumento)) AS FLOAT) / 
                         CAST(COUNT(DISTINCT CONCAT(R.IdProgramacion, '-', R.NumDocumento)) AS FLOAT)) * 100, 2
                    )
                END AS TasaAsistencia
            FROM EVENTOS E
            LEFT JOIN PROGRAMACION P ON E.IdEvento = P.IdEvento
            LEFT JOIN RESERVA R ON P.IdProgramacion = R.IdProgramacion
            LEFT JOIN ASISTENCIAEVENTO A ON R.IdProgramacion = A.IdProgramacion 
                AND R.NumDocumento = A.NumDocumento
            GROUP BY E.IdEvento, E.NombreEvento
            ORDER BY TotalAsistencias DESC, TasaAsistencia DESC
            """;

        String[] columnas = {"Evento", "Veces Programado", "Total Reservas", "Total Asistencias", "Promedio Reservas", "% Asistencia"};
        ejecutarConsultaReporte(query, columnas);
    }

    private void generarReporteDesempenoInstructores() throws SQLException {
        String query = """
            SELECT 
                CONCAT(Em.Nombres, ' ', Em.Apellidos) AS NombreInstructor,
                Em.TipoEmpleado,
                COUNT(DISTINCT P.IdProgramacion) AS EventosProgramados,
                COUNT(DISTINCT CONCAT(R.IdProgramacion, '-', R.NumDocumento)) AS TotalReservas,
                COUNT(DISTINCT CONCAT(A.IdProgramacion, '-', A.NumDocumento)) AS TotalAsistencias,
                CASE 
                    WHEN COUNT(DISTINCT P.IdProgramacion) = 0 THEN 0
                    ELSE ROUND(
                        CAST(COUNT(DISTINCT CONCAT(A.IdProgramacion, '-', A.NumDocumento)) AS FLOAT) / 
                        CAST(COUNT(DISTINCT P.IdProgramacion) AS FLOAT), 1
                    )
                END AS PromedioAsistentes,
                CASE 
                    WHEN COUNT(DISTINCT CONCAT(R.IdProgramacion, '-', R.NumDocumento)) = 0 THEN 0
                    ELSE ROUND(
                        (CAST(COUNT(DISTINCT CONCAT(A.IdProgramacion, '-', A.NumDocumento)) AS FLOAT) / 
                         CAST(COUNT(DISTINCT CONCAT(R.IdProgramacion, '-', R.NumDocumento)) AS FLOAT)) * 100, 2
                    )
                END AS TasaAsistencia
            FROM EMPLEADO Em
            INNER JOIN PROGRAMACION P ON Em.NumDocumento = P.NumDocumento
            LEFT JOIN RESERVA R ON P.IdProgramacion = R.IdProgramacion
            LEFT JOIN ASISTENCIAEVENTO A ON R.IdProgramacion = A.IdProgramacion 
                AND R.NumDocumento = A.NumDocumento
            WHERE Em.TipoEmpleado IN ('ENTRENADOR', 'ADMIN')
            GROUP BY Em.NumDocumento, Em.Nombres, Em.Apellidos, Em.TipoEmpleado
            ORDER BY TasaAsistencia DESC, PromedioAsistentes DESC
            """;

        String[] columnas = {"Instructor", "Tipo", "Eventos Programados", "Total Reservas", "Total Asistencias", "Promedio Asistentes", "% Asistencia"};
        ejecutarConsultaReporte(query, columnas);
    }

    private void generarReporteEventosPeriodo() throws SQLException {
        if (fechaInicio.getText().trim().isEmpty() || fechaFin.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar fechas de inicio y fin", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = """
            SELECT 
                CONVERT(VARCHAR(10), P.FechaHoraEvento, 103) AS Fecha,
                COUNT(DISTINCT P.IdProgramacion) AS EventosProgramados,
                COUNT(DISTINCT CONCAT(R.IdProgramacion, '-', R.NumDocumento)) AS TotalReservas,
                COUNT(DISTINCT CONCAT(A.IdProgramacion, '-', A.NumDocumento)) AS TotalAsistencias
            FROM PROGRAMACION P
            LEFT JOIN RESERVA R ON P.IdProgramacion = R.IdProgramacion
            LEFT JOIN ASISTENCIAEVENTO A ON R.IdProgramacion = A.IdProgramacion 
                AND R.NumDocumento = A.NumDocumento
            WHERE P.FechaHoraEvento BETWEEN ? AND ?
            GROUP BY CONVERT(VARCHAR(10), P.FechaHoraEvento, 103)
            ORDER BY Fecha DESC
            """;

        try (PreparedStatement stmt = conexion.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(fechaInicio.getText()));
            stmt.setDate(2, java.sql.Date.valueOf(fechaFin.getText()));
            
            String[] columnas = {"Fecha", "Eventos Programados", "Total Reservas", "Total Asistencias"};
            ejecutarConsultaReporte(stmt, columnas);
        }
    }

    private void generarReporteSociosSinParticipacion() throws SQLException {
        String query = """
            SELECT 
                S.NumDocumento,
                CONCAT(S.Nombres, ' ', S.Apellidos) AS NombreSocio,
                S.Email,
                S.FechaInscripcion,
                DATEDIFF(DAY, S.FechaInscripcion, GETDATE()) AS DiasComoSocio,
                CASE 
                    WHEN R.NumDocumento IS NOT NULL THEN 'RESERVA_SIN_ASISTIR'
                    ELSE 'SIN_PARTICIPACION'
                END AS TipoInactividad
            FROM SOCIO S
            LEFT JOIN RESERVA R ON S.NumDocumento = R.NumDocumento
            LEFT JOIN ASISTENCIAEVENTO A ON R.IdProgramacion = A.IdProgramacion 
                AND R.NumDocumento = A.NumDocumento
            WHERE A.NumDocumento IS NULL
            GROUP BY 
                S.NumDocumento, S.Nombres, S.Apellidos, S.Email, 
                S.FechaInscripcion, R.NumDocumento
            ORDER BY S.FechaInscripcion DESC
            """;

        String[] columnas = {"Documento", "Socio", "Email", "Fecha Inscripción", "Días como Socio", "Tipo Inactividad"};
        ejecutarConsultaReporte(query, columnas);
    }

    private void generarReporteMetricasGenerales() throws SQLException {
        String query = """
            SELECT 
                'EVENTOS_TOTALES' AS Metrica,
                COUNT(*) AS Valor
            FROM EVENTOS
            UNION ALL
            SELECT 
                'PROGRAMACIONES_ACTIVAS' AS Metrica,
                COUNT(*) AS Valor
            FROM PROGRAMACION 
            WHERE FechaHoraEvento > GETDATE()
            UNION ALL
            SELECT 
                'RESERVAS_PENDIENTES' AS Metrica,
                COUNT(DISTINCT CONCAT(R.IdProgramacion, '-', R.NumDocumento)) AS Valor
            FROM RESERVA R
            INNER JOIN PROGRAMACION P ON R.IdProgramacion = P.IdProgramacion
            WHERE P.FechaHoraEvento > GETDATE()
            UNION ALL
            SELECT 
                'SOCIOS_ACTIVOS_EN_EVENTOS' AS Metrica,
                COUNT(DISTINCT R.NumDocumento) AS Valor
            FROM RESERVA R
            INNER JOIN PROGRAMACION P ON R.IdProgramacion = P.IdProgramacion
            WHERE P.FechaHoraEvento >= DATEADD(MONTH, -1, GETDATE())
            """;

        String[] columnas = {"Métrica", "Valor"};
        ejecutarConsultaReporte(query, columnas);
    }

    private void ejecutarConsultaReporte(String query, String[] columnas) throws SQLException {
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            cargarDatosEnTabla(rs, columnas);
        }
    }

    private void ejecutarConsultaReporte(PreparedStatement stmt, String[] columnas) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            cargarDatosEnTabla(rs, columnas);
        }
    }

    private void cargarDatosEnTabla(ResultSet rs, String[] columnas) throws SQLException {
        // Configurar columnas
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);

        // Cargar datos
        int contador = 0;
        while (rs.next()) {
            Object[] fila = new Object[columnas.length];
            for (int i = 0; i < columnas.length; i++) {
                fila[i] = rs.getObject(i + 1);
            }
            modeloTabla.addRow(fila);
            contador++;
        }

        lblTotalRegistros.setText("Total de registros: " + contador);
        
        // Ajustar ancho de columnas
        tablaReporte.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private void exportarReporte() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File f) {
                return f.getName().toLowerCase().endsWith(".csv") || f.isDirectory();
            }
            public String getDescription() {
                return "Archivos CSV (*.csv)";
            }
        });

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String archivo = fileChooser.getSelectedFile().getAbsolutePath();
            if (!archivo.toLowerCase().endsWith(".csv")) {
                archivo += ".csv";
            }
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(archivo, "UTF-8")) {
                // Escribir encabezados
                for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
                    writer.print(modeloTabla.getColumnName(i));
                    if (i < modeloTabla.getColumnCount() - 1) writer.print(",");
                }
                writer.println();
                
                // Escribir datos
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
                        Object valor = modeloTabla.getValueAt(i, j);
                        writer.print(valor != null ? valor.toString() : "");
                        if (j < modeloTabla.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }
                
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente a: " + archivo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}