package persistencia;

import dominio.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaRepositorio {
    Optional<Persona> obtenerPorDocumento(String numDocumento);
    List<Persona> listarTodos();
    boolean insertar(Persona persona);
    boolean actualizar(Persona persona);
    boolean eliminar(String numDocumento);
}
