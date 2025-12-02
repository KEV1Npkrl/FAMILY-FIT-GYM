package persistencia;

import dominio.Socio;

import java.util.List;
import java.util.Optional;

public interface SocioRepositorio {
    Optional<Socio> obtenerPorDocumento(String numDocumento);
    List<Socio> listarTodos();
    boolean insertar(Socio socio);
    boolean eliminar(String numDocumento);
}
