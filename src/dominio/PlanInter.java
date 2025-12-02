package dominio;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class PlanInter extends Plan {
    public PlanInter() { }

    @Override
    public boolean incluyeFeriados() { return false; }

    @Override
    public boolean permiteTrotadoras() { return false; }

    @Override
    public boolean permiteCongelamiento() { return false; }

    @Override
    public boolean validarAcceso(LocalDateTime fechaHora, DayOfWeek diaSemana, boolean esFeriado) {
        // INTER: Interdiario L-M-V o M-J-S, sin duchas/trotadoras. Aquí validamos horario base y feriados.
        if (esFeriado) return false;
        int hora = fechaHora.getHour();
        if (diaSemana == DayOfWeek.SUNDAY) return false;
        if (diaSemana == DayOfWeek.SATURDAY) {
            return hora >= 6 && hora < 18;
        }
        return hora >= 5 && hora < 22;
    }
}
