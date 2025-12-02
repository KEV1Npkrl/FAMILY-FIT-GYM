package servicios;

import otros.Conexion;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Servicio para generar reportes estadísticos del gimnasio
 */
public class ServicioReportes {
    
    // ============ REPORTES DE SOCIOS ============
    
    /**
     * Obtiene estadísticas generales de socios
     */
    public Map<String, Object> obtenerEstadisticasSocios() {
        Map<String, Object> estadisticas = new HashMap<>();
        String sql = """
            SELECT 
                COUNT(*) as total_socios,
                COUNT(CASE WHEN m.Estado = 'ACTIVA' THEN 1 END) as socios_activos,
                COUNT(CASE WHEN m.Estado != 'ACTIVA' OR m.Estado IS NULL THEN 1 END) as socios_inactivos,
                COUNT(CASE WHEN DATEDIFF(day, p.FechaRegistro, GETDATE()) <= 30 THEN 1 END) as nuevos_registros
            FROM SOCIO s
            INNER JOIN PERSONA p ON s.NumDocumento = p.NumDocumento
            LEFT JOIN MEMBRESIA m ON s.NumDocumento = m.NumDocumento AND m.Estado = 'ACTIVA'
        """;
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                estadisticas.put("totalSocios", rs.getInt("total_socios"));
                estadisticas.put("sociosActivos", rs.getInt("socios_activos"));
                estadisticas.put("sociosInactivos", rs.getInt("socios_inactivos"));
                estadisticas.put("nuevosRegistros", rs.getInt("nuevos_registros"));
            } else {
                estadisticas.put("totalSocios", 0);
                estadisticas.put("sociosActivos", 0);
                estadisticas.put("sociosInactivos", 0);
                estadisticas.put("nuevosRegistros", 0);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas de socios: " + e.getMessage());
            estadisticas.put("totalSocios", 0);
            estadisticas.put("sociosActivos", 0);
            estadisticas.put("sociosInactivos", 0);
            estadisticas.put("nuevosRegistros", 0);
        }
        
        return estadisticas;
    }
    
    /**
     * Obtiene socios registrados por mes en el último año
     */
    public List<Map<String, Object>> obtenerSociosPorMes() {
        List<Map<String, Object>> datos = new ArrayList<>();
        String sql = """
            SELECT 
                YEAR(p.FechaRegistro) as año,
                MONTH(p.FechaRegistro) as mes,
                DATENAME(month, p.FechaRegistro) as nombreMes,
                COUNT(*) as cantidad
            FROM SOCIO s 
            INNER JOIN PERSONA p ON s.NumDocumento = p.NumDocumento
            WHERE p.FechaRegistro >= DATEADD(month, -12, GETDATE())
            GROUP BY YEAR(p.FechaRegistro), MONTH(p.FechaRegistro), DATENAME(month, p.FechaRegistro)
            ORDER BY año, mes
        """;
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("año", rs.getInt("año"));
                fila.put("mes", rs.getInt("mes"));
                fila.put("nombreMes", rs.getString("nombreMes"));
                fila.put("cantidad", rs.getInt("cantidad"));
                datos.add(fila);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener socios por mes: " + e.getMessage());
        }
        
        return datos;
    }
    
    // ============ REPORTES DE INGRESOS ============
    
    /**
     * Obtiene ingresos por período
     */
    public Map<String, Object> obtenerIngresosPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> ingresos = new HashMap<>();
        String sql = """
            SELECT 
                COUNT(*) as total_pagos,
                ISNULL(SUM(Monto), 0) as monto_total,
                ISNULL(AVG(Monto), 0) as promedio_pago
            FROM PAGO 
            WHERE CAST(FechaPago AS DATE) BETWEEN ? AND ?
        """;
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
            ps.setDate(2, java.sql.Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ingresos.put("totalPagos", rs.getInt("total_pagos"));
                    ingresos.put("montoTotal", rs.getDouble("monto_total"));
                    ingresos.put("promedioPago", rs.getDouble("promedio_pago"));
                } else {
                    ingresos.put("totalPagos", 0);
                    ingresos.put("montoTotal", 0.0);
                    ingresos.put("promedioPago", 0.0);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ingresos por período: " + e.getMessage());
            ingresos.put("totalPagos", 0);
            ingresos.put("montoTotal", 0.0);
            ingresos.put("promedioPago", 0.0);
        }
        
        return ingresos;
    }
    
    /**
     * Obtiene ingresos por método de pago
     */
    public List<Map<String, Object>> obtenerIngresosPorMetodoPago(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Map<String, Object>> datos = new ArrayList<>();
        String sql = """
            SELECT 
                mp.NombreMetodo as metodo,
                COUNT(*) as cantidad_pagos,
                SUM(p.Monto) as monto_total
            FROM PAGO p
            INNER JOIN METODOPAGO mp ON p.IdMetodoPago = mp.IdMetodoPago
            WHERE CAST(p.FechaPago AS DATE) BETWEEN ? AND ?
            GROUP BY mp.IdMetodoPago, mp.NombreMetodo
            ORDER BY monto_total DESC
        """;
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
            ps.setDate(2, java.sql.Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("metodo", rs.getString("metodo"));
                    fila.put("cantidadPagos", rs.getInt("cantidad_pagos"));
                    fila.put("montoTotal", rs.getDouble("monto_total"));
                    datos.add(fila);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ingresos por método de pago: " + e.getMessage());
        }
        
        return datos;
    }
    
    // ============ REPORTES DE ASISTENCIAS ============
    
    /**
     * Obtiene estadísticas de asistencias
     */
    public Map<String, Object> obtenerEstadisticasAsistencias(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> estadisticas = new HashMap<>();
        String sql = """
            SELECT 
                COUNT(*) as total_asistencias,
                COUNT(DISTINCT NumDocumento) as socios_distintos,
                CAST(COUNT(*) as FLOAT) / NULLIF(DATEDIFF(day, ?, ?), 0) as promedio_diario
            FROM ASISTENCIA
            WHERE CAST(FechaHoraEntrada AS DATE) BETWEEN ? AND ?
        """;
        
        // SQL para obtener hora pico
        String sqlHoraPico = """
            SELECT TOP 1 
                DATEPART(hour, FechaHoraEntrada) as hora,
                COUNT(*) as cantidad
            FROM ASISTENCIA
            WHERE CAST(FechaHoraEntrada AS DATE) BETWEEN ? AND ?
            GROUP BY DATEPART(hour, FechaHoraEntrada)
            ORDER BY cantidad DESC
        """;
        
        try (Connection cn = Conexion.iniciarConexion()) {
            // Obtener estadísticas generales
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
                ps.setDate(2, java.sql.Date.valueOf(fechaFin));
                ps.setDate(3, java.sql.Date.valueOf(fechaInicio));
                ps.setDate(4, java.sql.Date.valueOf(fechaFin));
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        estadisticas.put("totalAsistencias", rs.getInt("total_asistencias"));
                        estadisticas.put("sociosDistintos", rs.getInt("socios_distintos"));
                        estadisticas.put("promedioDiario", rs.getDouble("promedio_diario"));
                    }
                }
            }
            
            // Obtener hora pico
            try (PreparedStatement ps = cn.prepareStatement(sqlHoraPico)) {
                ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
                ps.setDate(2, java.sql.Date.valueOf(fechaFin));
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int hora = rs.getInt("hora");
                        estadisticas.put("horaPico", String.format("%02d:00", hora));
                    } else {
                        estadisticas.put("horaPico", "--:--");
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas de asistencias: " + e.getMessage());
            estadisticas.put("totalAsistencias", 0);
            estadisticas.put("sociosDistintos", 0);
            estadisticas.put("promedioDiario", 0.0);
            estadisticas.put("horaPico", "--:--");
        }
        
        return estadisticas;
    }
    
    /**
     * Obtiene asistencias por día de la semana
     */
    public List<Map<String, Object>> obtenerAsistenciasPorDiaSemana(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Map<String, Object>> datos = new ArrayList<>();
        String sql = """
            SELECT 
                DATENAME(weekday, FechaHoraEntrada) as dia_semana,
                COUNT(*) as total_asistencias,
                CAST(COUNT(*) as FLOAT) / NULLIF((DATEDIFF(day, ?, ?) + 1) / 7, 0) as promedio
            FROM ASISTENCIA
            WHERE CAST(FechaHoraEntrada AS DATE) BETWEEN ? AND ?
            GROUP BY DATENAME(weekday, FechaHoraEntrada), DATEPART(weekday, FechaHoraEntrada)
            ORDER BY DATEPART(weekday, FechaHoraEntrada)
        """;
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
            ps.setDate(2, java.sql.Date.valueOf(fechaFin));
            ps.setDate(3, java.sql.Date.valueOf(fechaInicio));
            ps.setDate(4, java.sql.Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("diaSemana", rs.getString("dia_semana"));
                    fila.put("totalAsistencias", rs.getInt("total_asistencias"));
                    fila.put("promedio", rs.getDouble("promedio"));
                    datos.add(fila);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener asistencias por día: " + e.getMessage());
        }
        
        return datos;
    }
    
    /**
     * Obtiene horarios pico de asistencias
     */
    public List<Map<String, Object>> obtenerHorariosPico(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Map<String, Object>> datos = new ArrayList<>();
        String sql = """
            WITH TotalAsistencias AS (
                SELECT COUNT(*) as total
                FROM ASISTENCIA
                WHERE CAST(FechaHoraEntrada AS DATE) BETWEEN ? AND ?
            )
            SELECT TOP 10
                DATEPART(hour, FechaHoraEntrada) as hora,
                COUNT(*) as asistencias,
                CAST(COUNT(*) * 100.0 / ta.total as DECIMAL(5,1)) as porcentaje
            FROM ASISTENCIA a
            CROSS JOIN TotalAsistencias ta
            WHERE CAST(FechaHoraEntrada AS DATE) BETWEEN ? AND ?
            GROUP BY DATEPART(hour, FechaHoraEntrada), ta.total
            ORDER BY asistencias DESC
        """;
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
            ps.setDate(2, java.sql.Date.valueOf(fechaFin));
            ps.setDate(3, java.sql.Date.valueOf(fechaInicio));
            ps.setDate(4, java.sql.Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    int hora = rs.getInt("hora");
                    fila.put("hora", String.format("%02d:00", hora));
                    fila.put("asistencias", rs.getInt("asistencias"));
                    fila.put("porcentaje", rs.getDouble("porcentaje"));
                    datos.add(fila);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener horarios pico: " + e.getMessage());
        }
        
        return datos;
    }
    
    // ============ REPORTES DE MEMBRESÍAS ============
    
    /**
     * Obtiene estadísticas de membresías
     */
    public Map<String, Object> obtenerEstadisticasMembresias() {
        Map<String, Object> estadisticas = new HashMap<>();
        String sql = """
            SELECT 
                COUNT(*) as total_membresias,
                COUNT(CASE WHEN Estado = 'ACTIVA' THEN 1 END) as membresias_activas,
                COUNT(CASE WHEN Estado = 'VENCIDA' OR FechaFin < GETDATE() THEN 1 END) as membresias_vencidas,
                COUNT(CASE WHEN Estado = 'ACTIVA' AND DATEDIFF(day, GETDATE(), FechaFin) BETWEEN 0 AND 30 THEN 1 END) as proximas_vencer
            FROM MEMBRESIA
        """;
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                estadisticas.put("totalMembresias", rs.getInt("total_membresias"));
                estadisticas.put("membresiasActivas", rs.getInt("membresias_activas"));
                estadisticas.put("membresiasVencidas", rs.getInt("membresias_vencidas"));
                estadisticas.put("proximasVencer", rs.getInt("proximas_vencer"));
            } else {
                estadisticas.put("totalMembresias", 0);
                estadisticas.put("membresiasActivas", 0);
                estadisticas.put("membresiasVencidas", 0);
                estadisticas.put("proximasVencer", 0);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas de membresías: " + e.getMessage());
            estadisticas.put("totalMembresias", 0);
            estadisticas.put("membresiasActivas", 0);
            estadisticas.put("membresiasVencidas", 0);
            estadisticas.put("proximasVencer", 0);
        }
        
        return estadisticas;
    }
    
    /**
     * Obtiene membresías por plan
     */
    public List<Map<String, Object>> obtenerMembresiasPorPlan() {
        List<Map<String, Object>> datos = new ArrayList<>();
        String sql = """
            SELECT 
                p.NombrePlan as planMembresia,
                COUNT(*) as cantidad,
                COUNT(CASE WHEN m.Estado = 'ACTIVA' THEN 1 END) as activas
            FROM MEMBRESIA m
            INNER JOIN PLANES p ON m.IdPlan = p.IdPlan
            GROUP BY p.IdPlan, p.NombrePlan
            ORDER BY cantidad DESC
        """;
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("planMembresia", rs.getString("planMembresia"));
                fila.put("cantidad", rs.getInt("cantidad"));
                fila.put("activas", rs.getInt("activas"));
                datos.add(fila);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener membresías por plan: " + e.getMessage());
        }
        
        return datos;
    }
    
    /**
     * Obtiene próximos vencimientos de membresías (30 días)
     */
    public List<Map<String, Object>> obtenerProximosVencimientos() {
        List<Map<String, Object>> datos = new ArrayList<>();
        String sql = """
            SELECT 
                p.Nombres + ' ' + p.Apellidos as nombreSocio,
                pl.NombrePlan as planMembresia,
                FORMAT(m.FechaFin, 'dd/MM/yyyy') as fechaVencimiento,
                DATEDIFF(day, GETDATE(), m.FechaFin) as diasRestantes
            FROM MEMBRESIA m
            INNER JOIN SOCIO s ON m.NumDocumento = s.NumDocumento
            INNER JOIN PERSONA p ON s.NumDocumento = p.NumDocumento
            INNER JOIN PLANES pl ON m.IdPlan = pl.IdPlan
            WHERE m.Estado = 'ACTIVA' 
                AND DATEDIFF(day, GETDATE(), m.FechaFin) BETWEEN 0 AND 30
            ORDER BY m.FechaFin ASC
        """;
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("nombreSocio", rs.getString("nombreSocio"));
                fila.put("planMembresia", rs.getString("planMembresia"));
                fila.put("fechaVencimiento", rs.getString("fechaVencimiento"));
                fila.put("diasRestantes", rs.getInt("diasRestantes"));
                datos.add(fila);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener próximos vencimientos: " + e.getMessage());
        }
        
        return datos;
    }
}