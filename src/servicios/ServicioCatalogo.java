package servicios;

import dominio.MetodoPago;
import dominio.Plan;
import persistencia.MetodoPagoRepositorio;
import persistencia.PlanRepositorio;
import persistencia.jdbc.MetodoPagoRepositorioJdbc;
import persistencia.jdbc.PlanRepositorioJdbc;

import java.util.List;

public class ServicioCatalogo {

    private final PlanRepositorio planRepo;
    private final MetodoPagoRepositorio metodoPagoRepo;

    public ServicioCatalogo() {
        this.planRepo = new PlanRepositorioJdbc();
        this.metodoPagoRepo = new MetodoPagoRepositorioJdbc();
    }

    public List<Plan> listarPlanes() {
        return planRepo.listarTodos();
    }

    public List<MetodoPago> listarMetodosPago() {
        return metodoPagoRepo.listarTodos();
    }
}
