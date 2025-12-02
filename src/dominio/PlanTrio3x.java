package dominio;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class PlanTrio3x extends Plan {
    public PlanTrio3x() { }

    @Override
    public boolean incluyeFeriados() { return false; }

    @Override
    public boolean permiteTrotadoras() { return false; }

    @Override
    public boolean permiteCongelamiento() { return false; }

    @Override
    public boolean validarAcceso(LocalDateTime fechaHora, DayOfWeek diaSemana, boolean esFeriado) {
        // 3X (Trío): 3 personas, L-V, sin trotadoras
        if (esFeriado) return false;
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) return false;
        int hora = fechaHora.getHour();
        return hora >= 5 && hora < 22; // L-V
    }
}
