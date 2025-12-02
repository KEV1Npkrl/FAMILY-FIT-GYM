package persistencia;

import dominio.Membresia;

import java.util.List;
import java.util.Optional;

public interface MembresiaRepositorio {
    Optional<Membresia> obtenerPorId(int idMembresia);
    List<Membresia> listarPorSocio(String numDocumento);
    List<Membresia> listarTodas();
    boolean insertar(Membresia m);
    boolean actualizar(Membresia m);
    boolean eliminar(int idMembresia);
}
