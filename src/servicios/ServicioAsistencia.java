package servicios;

import dominio.Asistencia;
import dominio.Membresia;
import dominio.Plan;
import otros.Conexion;
import persistencia.AsistenciaRepositorio;
import persistencia.MembresiaRepositorio;
import persistencia.PlanRepositorio;
import persistencia.jdbc.AsistenciaRepositorioJdbc;
import persistencia.jdbc.MembresiaRepositorioJdbc;
import persistencia.jdbc.PlanRepositorioJdbc;
import utilidades.FeriadosUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ServicioAsistencia {
    private final AsistenciaRepositorio repo = new AsistenciaRepositorioJdbc();
    private final MembresiaRepositorio memRepo = new MembresiaRepositorioJdbc();
    private final PlanRepositorio planRepo = new PlanRepositorioJdbc();
    private final ServicioPlan servicioPlan = new ServicioPlan();

    public boolean registrarEntrada(String numDocumento) {
        Optional<Membresia> om = memRepo.listarPorSocio(numDocumento).stream()
                .filter(m -> m.getEstado()!=null && m.getEstado().toUpperCase().contains("ACT"))
                .findFirst();
        if (!om.isPresent()) return false;
        Membresia m = om.get();
        Optional<Plan> op = planRepo.obtenerPorId(m.getIdPlan());
        if (!op.isPresent()) return false;
        LocalDateTime ahora = LocalDateTime.now();
        boolean esFeriado = FeriadosUtil.esFeriado(ahora.toLocalDate());
        boolean ok = servicioPlan.validarAcceso(op.get().getTipo(), ahora, esFeriado);
        if (!ok) return false;
        int nuevoId = siguienteId("ASISTENCIA", "IdAsistencia");
        Asistencia a = new Asistencia(nuevoId, numDocumento, ahora);
        return repo.insertar(a);
    }

    public List<Asistencia> listarPorSocio(String numDocumento) { return repo.listarPorSocio(numDocumento); }

    private int siguienteId(String tabla, String campoId) {
        String sql = "SELECT ISNULL(MAX("+campoId+"),0)+1 AS NextId FROM "+tabla;
        try (Connection cn = Conexion.iniciarConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt("NextId"); }
        } catch (SQLException e) { e.printStackTrace(); }
        return (int)(System.currentTimeMillis()/1000);
    }
}
