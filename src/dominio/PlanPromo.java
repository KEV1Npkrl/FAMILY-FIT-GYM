package dominio;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class PlanPromo extends Plan {
    public PlanPromo() { }

    @Override
    public boolean incluyeFeriados() { return false; }

    @Override
    public boolean permiteTrotadoras() { return false; }

    @Override
    public boolean permiteCongelamiento() { return false; }

    @Override
    public boolean validarAcceso(LocalDateTime fechaHora, DayOfWeek diaSemana, boolean esFeriado) {
        // PROMO: L-V, 1 turno (validación de horario estándar), sin trotadoras, sin sábados/feriados
        if (esFeriado) return false;
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) return false;
        int hora = fechaHora.getHour();
        // L-V 05:00-22:00
        return hora >= 5 && hora < 22;
    }
}
