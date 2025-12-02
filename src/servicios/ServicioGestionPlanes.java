package servicios;

import dominio.Plan;
import persistencia.PlanRepositorio;
import persistencia.jdbc.PlanRepositorioJdbc;

import java.util.List;
import java.util.Optional;

public class ServicioGestionPlanes {

    private final PlanRepositorio planRepositorio = new PlanRepositorioJdbc();

    public List<Plan> listar() { return planRepositorio.listarTodos(); }

    public Optional<Plan> obtener(int id) { return planRepositorio.obtenerPorId(id); }

    public boolean guardar(Plan plan) {
        // si existe -> actualizar, si no -> insertar
        return planRepositorio.obtenerPorId(plan.getIdPlan())
                .map(p -> planRepositorio.actualizar(plan))
                .orElseGet(() -> planRepositorio.insertar(plan));
    }

    public boolean eliminar(int id) { return planRepositorio.eliminar(id); }
}
