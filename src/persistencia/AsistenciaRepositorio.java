package persistencia;

import dominio.Asistencia;

import java.util.List;

public interface AsistenciaRepositorio {
    boolean insertar(Asistencia a);
    List<Asistencia> listarPorSocio(String numDocumento);
}
