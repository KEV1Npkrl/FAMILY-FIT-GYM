package servicios;

import dominio.MetodoPago;
import persistencia.MetodoPagoRepositorio;
import persistencia.jdbc.MetodoPagoRepositorioJdbc;

import java.util.List;
import java.util.Optional;

public class ServicioGestionMetodosPago {
    private final MetodoPagoRepositorio repo = new MetodoPagoRepositorioJdbc();

    public List<MetodoPago> listar() { return repo.listarTodos(); }

    public Optional<MetodoPago> obtener(String id) { return repo.obtenerPorId(id); }

    public boolean guardar(MetodoPago obj) {
        return repo.obtenerPorId(obj.getIdMetodoPago())
                .map(x -> repo.actualizar(obj))
                .orElseGet(() -> repo.insertar(obj));
    }

    public boolean eliminar(String id) { return repo.eliminar(id); }
}
