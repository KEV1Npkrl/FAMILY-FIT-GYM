package dominio;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class PlanFull extends Plan {
    public PlanFull() { }

    @Override
    public boolean incluyeFeriados() { return true; }

    @Override
    public boolean permiteTrotadoras() { return true; }

    @Override
    public boolean permiteCongelamiento() { return true; }

    @Override
    public boolean validarAcceso(LocalDateTime fechaHora, DayOfWeek diaSemana, boolean esFeriado) {
        // FULL: L-S + feriados, trotadoras y todo incluido
        int hora = fechaHora.getHour();
        if (diaSemana == DayOfWeek.SUNDAY) return false; // Domingo no indicado
        if (diaSemana == DayOfWeek.SATURDAY) {
            return hora >= 6 && hora < 18; // Sáb 06:00-18:00
        }
        return hora >= 5 && hora < 22; // L-V 05:00-22:00
    }
}
