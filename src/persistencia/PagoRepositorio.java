package persistencia;

import dominio.Pago;

import java.util.List;
import java.util.Optional;

public interface PagoRepositorio {
    Optional<Pago> obtenerPorId(int idPago);
    List<Pago> listarPorMembresia(int idMembresia);
    boolean insertar(Pago p);
}
