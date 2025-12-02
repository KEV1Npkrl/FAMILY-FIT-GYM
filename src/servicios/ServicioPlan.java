package servicios;

import dominio.*;
import persistencia.PlanRepositorio;
import persistencia.jdbc.PlanRepositorioJdbc;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ServicioPlan {

    private final PlanRepositorio planRepo = new PlanRepositorioJdbc();

    public boolean validarAcceso(TipoPlan tipoPlan, LocalDateTime fechaHora, boolean esFeriado) {
        DayOfWeek dia = fechaHora.getDayOfWeek();
        Plan plan = crearPlan(tipoPlan);
        return plan.validarAcceso(fechaHora, dia, esFeriado);
    }

    public boolean permiteTrotadoras(TipoPlan tipoPlan) {
        return crearPlan(tipoPlan).permiteTrotadoras();
    }

    public boolean incluyeFeriados(TipoPlan tipoPlan) {
        return crearPlan(tipoPlan).incluyeFeriados();
    }

    public boolean permiteCongelamiento(TipoPlan tipoPlan) {
        return crearPlan(tipoPlan).permiteCongelamiento();
    }
    
    public List<Plan> listarTodos() {
        return planRepo.listarTodos();
    }
    
    public Optional<Plan> obtener(int id) {
        return planRepo.obtenerPorId(id);
    }
    
    public boolean crear(Plan plan) {
        return planRepo.insertar(plan);
    }
    
    public boolean actualizar(Plan plan) {
        return planRepo.actualizar(plan);
    }
    
    public boolean eliminar(int id) {
        return planRepo.eliminar(id);
    }

    private Plan crearPlan(TipoPlan tipoPlan) {
        switch (tipoPlan) {
            case PROMO: return new PlanPromo();
            case FULL: return new PlanFull();
            case FITNESS: return new PlanFitness();
            case INTER: return new PlanInter();
            case PAREJA_2X: return new PlanPareja2x();
            case TRIO_3X: return new PlanTrio3x();
            case BAILE: return new PlanBaile();
            default: throw new IllegalArgumentException("Tipo de plan no soportado: " + tipoPlan);
        }
    }
}
