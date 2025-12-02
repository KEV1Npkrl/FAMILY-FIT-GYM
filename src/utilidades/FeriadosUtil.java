package utilidades;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilidad simple para marcar algunos feriados. En un caso real, podría consultarse de BD.
 */
public class FeriadosUtil {
    private static final Set<LocalDate> FERIADOS = new HashSet<>();

    static {
        // Ejemplos (personaliza según país/año)
        // Año actual
        int year = LocalDate.now().getYear();
        FERIADOS.add(LocalDate.of(year, 1, 1));   // Año Nuevo
        FERIADOS.add(LocalDate.of(year, 12, 25)); // Navidad
    }

    public static boolean esFeriado(LocalDate fecha) {
        return FERIADOS.contains(fecha);
    }
}
