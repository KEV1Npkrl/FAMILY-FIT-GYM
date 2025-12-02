package aplicacion;

import servicios.ServicioPlan;
import dominio.TipoPlan;
import utilidades.FeriadosUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BancoPruebas {
    public static void main(String[] args) {
        ServicioPlan servicioPlan = new ServicioPlan();

        LocalDateTime lv_9am = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atTime(9, 0);
        LocalDateTime sab_10am = LocalDate.now().with(java.time.DayOfWeek.SATURDAY).atTime(10, 0);
        LocalDateTime lv_10pm = LocalDate.now().with(java.time.DayOfWeek.FRIDAY).atTime(22, 0);

        boolean esFeriadoHoy = FeriadosUtil.esFeriado(LocalDate.now());

        System.out.println("PROMO L-V 9am: " + servicioPlan.validarAcceso(TipoPlan.PROMO, lv_9am, esFeriadoHoy));
        System.out.println("FULL Sábado 10am: " + servicioPlan.validarAcceso(TipoPlan.FULL, sab_10am, esFeriadoHoy));
        System.out.println("FITNESS Viernes 22:00 (borde): " + servicioPlan.validarAcceso(TipoPlan.FITNESS, lv_10pm, esFeriadoHoy));
        System.out.println("TRIO_3X Sábado: " + servicioPlan.validarAcceso(TipoPlan.TRIO_3X, sab_10am, esFeriadoHoy));
        System.out.println("BAILE L-V 9am: " + servicioPlan.validarAcceso(TipoPlan.BAILE, lv_9am, esFeriadoHoy));
    }
}
