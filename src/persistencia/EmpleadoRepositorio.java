package persistencia;

import dominio.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepositorio {
    Optional<Empleado> obtenerPorDocumento(String numDocumento);
    List<Empleado> listarTodos();
    boolean insertar(Empleado empleado);
    boolean actualizar(Empleado empleado);
    boolean eliminar(String numDocumento);
}
