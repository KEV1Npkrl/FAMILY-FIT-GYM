package servicios;

import dominio.Membresia;
import dominio.Plan;
import otros.Conexion;
import persistencia.MembresiaRepositorio;
import persistencia.PlanRepositorio;
import persistencia.jdbc.MembresiaRepositorioJdbc;
import persistencia.jdbc.PlanRepositorioJdbc;
import utilidades.FeriadosUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class ServicioMembresia {
    private final MembresiaRepositorio repo = new MembresiaRepositorioJdbc();
    private final PlanRepositorio planRepo = new PlanRepositorioJdbc();
    private final ServicioPlan servicioPlan = new ServicioPlan();

    public Optional<Membresia> obtener(int id) { return repo.obtenerPorId(id); }
    public List<Membresia> listarPorSocio(String doc) { return repo.listarPorSocio(doc); }
    public List<Membresia> obtenerTodas() { return repo.listarTodas(); }

    public boolean crearNueva(String numDocumento, int idPlan, LocalDate fechaInicio, String estado) {
        Optional<Plan> plan = planRepo.obtenerPorId(idPlan);
        if (!plan.isPresent()) return false;
        LocalDate fin = fechaInicio.plusDays(plan.get().getDuracionDias());
        int nuevoId = siguienteId("MEMBRESIA", "IdMembresia");
        Membresia m = new Membresia(nuevoId, fechaInicio, fin, estado, numDocumento, idPlan);
        return repo.insertar(m);
    }

    public Optional<Membresia> obtenerActiva(String numDocumento) {
        return listarPorSocio(numDocumento).stream()
                .filter(m -> m.getEstado()!=null && m.getEstado().toUpperCase().contains("ACT"))
                .filter(m -> !LocalDate.now().isBefore(m.getFechaInicio()) && !LocalDate.now().isAfter(m.getFechaFin()))
                .max(Comparator.comparing(Membresia::getFechaFin));
    }

    public boolean registrarAsistenciaSiCorresponde(String numDocumento) {
        Optional<Membresia> om = obtenerActiva(numDocumento);
        if (!om.isPresent()) return false;
        Membresia m = om.get();
        Optional<Plan> op = planRepo.obtenerPorId(m.getIdPlan());
        if (!op.isPresent()) return false;
        LocalDateTime ahora = LocalDateTime.now();
        boolean esFeriado = FeriadosUtil.esFeriado(ahora.toLocalDate());
        boolean puede = servicioPlan.validarAcceso(op.get().getTipo(), ahora, esFeriado);
        return puede;
    }

    private int siguienteId(String tabla, String campoId) {
        String sql = "SELECT ISNULL(MAX("+campoId+"),0)+1 AS NextId FROM "+tabla;
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt("NextId"); }
        } catch (SQLException e) { e.printStackTrace(); }
        return (int)(System.currentTimeMillis()/1000); // fallback
    }

    /**
     * Busca membresías según criterios específicos
     */
    public List<Membresia> buscarPorCriterios(String docSocio, String nombreSocio, 
                                              Boolean activa, LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Membresia> resultados = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.IdMembresia, m.FechaInicio, m.FechaFin, m.Estado, ");
        sql.append("m.NumDocumentoSocio, m.IdPlan, m.Precio ");
        sql.append("FROM MEMBRESIA m ");
        
        if (nombreSocio != null && !nombreSocio.trim().isEmpty()) {
            sql.append("INNER JOIN PERSONA p ON m.NumDocumentoSocio = p.NumDocumento ");
        }
        
        sql.append("WHERE 1=1 ");
        
        List<Object> parametros = new ArrayList<>();
        
        if (docSocio != null && !docSocio.trim().isEmpty()) {
            sql.append("AND m.NumDocumentoSocio LIKE ? ");
            parametros.add("%" + docSocio.trim() + "%");
        }
        
        if (nombreSocio != null && !nombreSocio.trim().isEmpty()) {
            sql.append("AND (p.Nombres LIKE ? OR p.Apellidos LIKE ?) ");
            parametros.add("%" + nombreSocio.trim() + "%");
            parametros.add("%" + nombreSocio.trim() + "%");
        }
        
        if (activa != null) {
            if (activa) {
                sql.append("AND m.Estado = 'ACTIVO' ");
            } else {
                sql.append("AND m.Estado = 'VENCIDO' ");
            }
        }
        
        if (fechaDesde != null) {
            sql.append("AND m.FechaInicio >= ? ");
            parametros.add(fechaDesde);
        }
        
        if (fechaHasta != null) {
            sql.append("AND m.FechaFin <= ? ");
            parametros.add(fechaHasta);
        }
        
        sql.append("ORDER BY m.FechaInicio DESC");
        
        try (Connection cn = Conexion.iniciarConexion();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            
            // Establecer parámetros
            for (int i = 0; i < parametros.size(); i++) {
                if (parametros.get(i) instanceof LocalDate) {
                    ps.setDate(i + 1, java.sql.Date.valueOf((LocalDate) parametros.get(i)));
                } else {
                    ps.setString(i + 1, parametros.get(i).toString());
                }
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Membresia membresia = new Membresia();
                    membresia.setId(rs.getInt("IdMembresia"));
                    membresia.setFechaInicio(rs.getDate("FechaInicio").toLocalDate());
                    membresia.setFechaFin(rs.getDate("FechaFin").toLocalDate());
                    membresia.setEstado(rs.getString("Estado"));
                    membresia.setNumDocumentoSocio(rs.getString("NumDocumentoSocio"));
                    membresia.setIdPlan(rs.getInt("IdPlan"));
                    membresia.setPrecio(rs.getBigDecimal("Precio"));
                    
                    resultados.add(membresia);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar membresías por criterios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultados;
    }
}
