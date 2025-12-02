package dominio;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class PlanBaile extends Plan {
    public PlanBaile() { }

    @Override
    public boolean incluyeFeriados() { return false; }

    @Override
    public boolean permiteTrotadoras() { return false; }

    @Override
    public boolean permiteCongelamiento() { return false; }

    @Override
    public boolean validarAcceso(LocalDateTime fechaHora, DayOfWeek diaSemana, boolean esFeriado) {
        // BAILE: Solo baile, L-V horarios fijos. Aquí aplicamos horario base L-V.
        if (esFeriado) return false;
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) return false;
        int hora = fechaHora.getHour();
        return hora >= 5 && hora < 22; // L-V
    }
}
