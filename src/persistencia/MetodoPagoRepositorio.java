package persistencia;

import dominio.MetodoPago;

import java.util.List;
import java.util.Optional;

public interface MetodoPagoRepositorio {
    Optional<MetodoPago> obtenerPorId(String idMetodoPago);
    List<MetodoPago> listarTodos();
    boolean insertar(MetodoPago metodoPago);
    boolean actualizar(MetodoPago metodoPago);
    boolean eliminar(String idMetodoPago);
}
