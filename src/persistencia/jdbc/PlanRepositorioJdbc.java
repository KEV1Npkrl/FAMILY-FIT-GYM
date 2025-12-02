package persistencia.jdbc;

import dominio.*;
import otros.Conexion;
import persistencia.PlanRepositorio;
import utilidades.ValidadorEntidades;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlanRepositorioJdbc implements PlanRepositorio {

    private Plan mapear(ResultSet rs) throws SQLException {
        int idPlan = rs.getInt("IdPlan");
        String nombrePlan = rs.getString("NombrePlan");
        int duracionDias = rs.getInt("DuracionDias");
        BigDecimal costo = rs.getBigDecimal("Costo");
        String descripcion = rs.getString("Descripcion");
        // Deducción de TipoPlan por nombre (ajustar según tu data en PLANES)
        TipoPlan tipo = deducirTipoPorNombre(nombrePlan);
        Plan planBase = crearPlanPorTipo(tipo);
        planBase.setIdPlan(idPlan);
        planBase.setNombrePlan(nombrePlan);
        planBase.setDuracionDias(duracionDias);
        planBase.setCosto(costo);
        planBase.setDescripcion(descripcion);
        planBase.setTipo(tipo);
        return planBase;
    }

    private TipoPlan deducirTipoPorNombre(String nombrePlan) {
        String n = nombrePlan.toUpperCase();
        if (n.contains("PROMO")) return TipoPlan.PROMO;
        if (n.contains("FULL")) return TipoPlan.FULL;
        if (n.contains("FITNESS")) return TipoPlan.FITNESS;
        if (n.contains("INTER")) return TipoPlan.INTER;
        if (n.contains("2X") || n.contains("PAREJA")) return TipoPlan.PAREJA_2X;
        if (n.contains("3X") || n.contains("TRIO")) return TipoPlan.TRIO_3X;
        if (n.contains("BAILE")) return TipoPlan.BAILE;
        // Por defecto
        return TipoPlan.FULL;
    }

    private Plan crearPlanPorTipo(TipoPlan tipo) {
        switch (tipo) {
            case PROMO: return new PlanPromo();
            case FULL: return new PlanFull();
            case FITNESS: return new PlanFitness();
            case INTER: return new PlanInter();
            case PAREJA_2X: return new PlanPareja2x();
            case TRIO_3X: return new PlanTrio3x();
            case BAILE: return new PlanBaile();
            default: return new PlanFull();
        }
    }

    @Override
    public Optional<Plan> obtenerPorId(int idPlan) {
        String sql = "SELECT IdPlan, NombrePlan, DuracionDias, Costo, Descripcion FROM PLANES WHERE IdPlan = ?";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en obtenerPorId(Plan)"); return Optional.empty(); }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idPlan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapear(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Plan> listarTodos() {
        String sql = "SELECT IdPlan, NombrePlan, DuracionDias, Costo, Descripcion FROM PLANES";
        List<Plan> lista = new ArrayList<>();
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en listarTodos(Plan)"); return lista; }
            try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertar(Plan plan) {
        // Validar antes de insertar
        try {
            ValidadorEntidades.validarPlan(plan);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación en insertar(Plan): " + e.getMessage());
            return false;
        }
        
        String sql = "INSERT INTO PLANES (IdPlan, NombrePlan, DuracionDias, Costo, Descripcion) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en insertar(Plan)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, plan.getIdPlan());
                ps.setString(2, plan.getNombrePlan());
                ps.setInt(3, plan.getDuracionDias());
                ps.setBigDecimal(4, plan.getCosto());
                ps.setString(5, plan.getDescripcion());
                return ps.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error SQL en insertar(Plan): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Plan plan) {
        // Validar antes de actualizar
        try {
            ValidadorEntidades.validarPlan(plan);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación en actualizar(Plan): " + e.getMessage());
            return false;
        }
        
        String sql = "UPDATE PLANES SET NombrePlan=?, DuracionDias=?, Costo=?, Descripcion=? WHERE IdPlan=?";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en actualizar(Plan)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, plan.getNombrePlan());
                ps.setInt(2, plan.getDuracionDias());
                ps.setBigDecimal(3, plan.getCosto());
                ps.setString(4, plan.getDescripcion());
                ps.setInt(5, plan.getIdPlan());
                return ps.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error SQL en actualizar(Plan): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int idPlan) {
        String sql = "DELETE FROM PLANES WHERE IdPlan=?";
        try (Connection cn = Conexion.iniciarConexion()) {
            if (cn == null) { System.err.println("Sin conexión a BD en eliminar(Plan)"); return false; }
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idPlan);
                return ps.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
