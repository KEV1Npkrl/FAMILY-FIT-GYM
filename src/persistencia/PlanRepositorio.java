package persistencia;

import dominio.Plan;

import java.util.List;
import java.util.Optional;

public interface PlanRepositorio {
    Optional<Plan> obtenerPorId(int idPlan);
    List<Plan> listarTodos();
    boolean insertar(Plan plan);
    boolean actualizar(Plan plan);
    boolean eliminar(int idPlan);
}
