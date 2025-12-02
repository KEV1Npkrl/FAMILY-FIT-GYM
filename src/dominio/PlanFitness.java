package dominio;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class PlanFitness extends Plan {
    public PlanFitness() { }

    @Override
    public boolean incluyeFeriados() { return false; }

    @Override
    public boolean permiteTrotadoras() { return false; }

    @Override
    public boolean permiteCongelamiento() { return false; }

    @Override
    public boolean validarAcceso(LocalDateTime fechaHora, DayOfWeek diaSemana, boolean esFeriado) {
        // FITNESS: L-S, 1 turno, sin feriados/congelamiento
        if (esFeriado) return false;
        int hora = fechaHora.getHour();
        if (diaSemana == DayOfWeek.SUNDAY) return false;
        if (diaSemana == DayOfWeek.SATURDAY) {
            return hora >= 6 && hora < 18; // Sáb 06:00-18:00
        }
        return hora >= 5 && hora < 22; // L-V 05:00-22:00
    }
}
