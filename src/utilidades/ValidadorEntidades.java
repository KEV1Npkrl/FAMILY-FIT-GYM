package utilidades;

import dominio.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Validador que garantiza que los datos cumplan con las restricciones de la BD GIMNASIO
 */
public class ValidadorEntidades {
    
    // Validaciones para PERSONA
    public static void validarPersona(Persona persona) {
        if (persona.getNumDocumento() == null || persona.getNumDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("NumDocumento es requerido");
        }
        if (persona.getNumDocumento().length() > 20) {
            throw new IllegalArgumentException("NumDocumento no puede exceder 20 caracteres");
        }
        
        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombres es requerido");
        }
        if (persona.getNombres().length() > 100) {
            throw new IllegalArgumentException("Nombres no puede exceder 100 caracteres");
        }
        
        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Apellidos es requerido");
        }
        if (persona.getApellidos().length() > 100) {
            throw new IllegalArgumentException("Apellidos no puede exceder 100 caracteres");
        }
        
        if (persona.getPasswordHass() == null || persona.getPasswordHass().trim().isEmpty()) {
            throw new IllegalArgumentException("PasswordHass es requerido");
        }
        if (persona.getPasswordHass().length() > 60) {
            throw new IllegalArgumentException("PasswordHass no puede exceder 60 caracteres");
        }
        
        if (persona.getFechaRegistro() == null) {
            throw new IllegalArgumentException("FechaRegistro es requerida");
        }
        
        if (persona.getCelular() != null && persona.getCelular().length() > 20) {
            throw new IllegalArgumentException("Celular no puede exceder 20 caracteres");
        }
        
        if (persona.getCorreo() != null && persona.getCorreo().length() > 100) {
            throw new IllegalArgumentException("Correo no puede exceder 100 caracteres");
        }
    }
    
    // Validaciones para EMPLEADO
    public static void validarEmpleado(Empleado empleado) {
        validarPersona(empleado); // Hereda validaciones de persona
        
        if (empleado.getTipoEmpleado() == null || empleado.getTipoEmpleado().trim().isEmpty()) {
            throw new IllegalArgumentException("TipoEmpleado es requerido");
        }
        if (empleado.getTipoEmpleado().length() > 20) {
            throw new IllegalArgumentException("TipoEmpleado no puede exceder 20 caracteres");
        }
    }
    
    // Validaciones para PLANES
    public static void validarPlan(Plan plan) {
        if (plan.getIdPlan() <= 0) {
            throw new IllegalArgumentException("IdPlan debe ser mayor a 0");
        }
        
        if (plan.getNombrePlan() == null || plan.getNombrePlan().trim().isEmpty()) {
            throw new IllegalArgumentException("NombrePlan es requerido");
        }
        if (plan.getNombrePlan().length() > 60) {
            throw new IllegalArgumentException("NombrePlan no puede exceder 60 caracteres");
        }
        
        if (plan.getDuracionDias() <= 0) {
            throw new IllegalArgumentException("DuracionDias debe ser mayor a 0");
        }
        
        if (plan.getCosto() == null || plan.getCosto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Costo debe ser mayor a 0");
        }
        if (plan.getCosto().scale() > 2) {
            throw new IllegalArgumentException("Costo no puede tener más de 2 decimales");
        }
        
        if (plan.getDescripcion() != null && plan.getDescripcion().length() > 200) {
            throw new IllegalArgumentException("Descripcion no puede exceder 200 caracteres");
        }
    }
    
    // Validaciones para METODOPAGO
    public static void validarMetodoPago(MetodoPago metodoPago) {
        if (metodoPago.getIdMetodoPago() == null || metodoPago.getIdMetodoPago().trim().isEmpty()) {
            throw new IllegalArgumentException("IdMetodoPago es requerido");
        }
        if (metodoPago.getIdMetodoPago().length() > 20) {
            throw new IllegalArgumentException("IdMetodoPago no puede exceder 20 caracteres");
        }
        
        if (metodoPago.getNombreMetodo() == null || metodoPago.getNombreMetodo().trim().isEmpty()) {
            throw new IllegalArgumentException("NombreMetodo es requerido");
        }
        if (metodoPago.getNombreMetodo().length() > 50) {
            throw new IllegalArgumentException("NombreMetodo no puede exceder 50 caracteres");
        }
    }
    
    // Validaciones para MEMBRESIA
    public static void validarMembresia(Membresia membresia) {
        if (membresia.getIdMembresia() <= 0) {
            throw new IllegalArgumentException("IdMembresia debe ser mayor a 0");
        }
        
        if (membresia.getFechaInicio() == null) {
            throw new IllegalArgumentException("FechaInicio es requerida");
        }
        
        if (membresia.getFechaFin() == null) {
            throw new IllegalArgumentException("FechaFin es requerida");
        }
        
        if (membresia.getFechaFin().isBefore(membresia.getFechaInicio())) {
            throw new IllegalArgumentException("FechaFin debe ser posterior a FechaInicio");
        }
        
        if (membresia.getEstado() == null || membresia.getEstado().trim().isEmpty()) {
            throw new IllegalArgumentException("Estado es requerido");
        }
        if (membresia.getEstado().length() > 50) {
            throw new IllegalArgumentException("Estado no puede exceder 50 caracteres");
        }
        
        if (membresia.getNumDocumento() == null || membresia.getNumDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("NumDocumento (socio) es requerido");
        }
        
        if (membresia.getIdPlan() <= 0) {
            throw new IllegalArgumentException("IdPlan debe ser mayor a 0");
        }
    }
    
    // Validaciones para PAGO
    public static void validarPago(Pago pago) {
        if (pago.getIdPago() <= 0) {
            throw new IllegalArgumentException("IdPago debe ser mayor a 0");
        }
        
        if (pago.getFechaPago() == null) {
            throw new IllegalArgumentException("FechaPago es requerida");
        }
        
        if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto debe ser mayor a 0");
        }
        if (pago.getMonto().scale() > 2) {
            throw new IllegalArgumentException("Monto no puede tener más de 2 decimales");
        }
        
        if (pago.getNumDocumento() == null || pago.getNumDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("NumDocumento (empleado) es requerido");
        }
        
        if (pago.getIdMetodoPago() == null || pago.getIdMetodoPago().trim().isEmpty()) {
            throw new IllegalArgumentException("IdMetodoPago es requerido");
        }
        
        if (pago.getIdMembresia() <= 0) {
            throw new IllegalArgumentException("IdMembresia debe ser mayor a 0");
        }
    }
    
    // Validaciones para EVENTOS
    public static void validarEvento(Evento evento) {
        if (evento.getIdEvento() <= 0) {
            throw new IllegalArgumentException("IdEvento debe ser mayor a 0");
        }
        
        if (evento.getNombreEvento() == null || evento.getNombreEvento().trim().isEmpty()) {
            throw new IllegalArgumentException("NombreEvento es requerido");
        }
        if (evento.getNombreEvento().length() > 100) {
            throw new IllegalArgumentException("NombreEvento no puede exceder 100 caracteres");
        }
        
        if (evento.getDescripcion() != null && evento.getDescripcion().length() > 200) {
            throw new IllegalArgumentException("Descripcion no puede exceder 200 caracteres");
        }
    }
    
    // Validaciones para ASISTENCIA
    public static void validarAsistencia(Asistencia asistencia) {
        if (asistencia.getIdAsistencia() <= 0) {
            throw new IllegalArgumentException("IdAsistencia debe ser mayor a 0");
        }
        
        if (asistencia.getNumDocumento() == null || asistencia.getNumDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("NumDocumento es requerido");
        }
        
        if (asistencia.getFechaHoraEntrada() == null) {
            throw new IllegalArgumentException("FechaHoraEntrada es requerida");
        }
    }
    
    // Validaciones para PROGRAMACION
    public static void validarProgramacion(Programacion programacion) {
        if (programacion.getIdProgramacion() <= 0) {
            throw new IllegalArgumentException("IdProgramacion debe ser mayor a 0");
        }
        
        if (programacion.getLugarEvento() == null || programacion.getLugarEvento().trim().isEmpty()) {
            throw new IllegalArgumentException("LugarEvento es requerido");
        }
        if (programacion.getLugarEvento().length() > 100) {
            throw new IllegalArgumentException("LugarEvento no puede exceder 100 caracteres");
        }
        
        if (programacion.getFechaHoraEvento() == null) {
            throw new IllegalArgumentException("FechaHoraEvento es requerida");
        }
        
        if (programacion.getNumDocumento() == null || programacion.getNumDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("NumDocumento (empleado) es requerido");
        }
        
        if (programacion.getIdEvento() <= 0) {
            throw new IllegalArgumentException("IdEvento debe ser mayor a 0");
        }
    }
    
    // Validaciones para RESERVA
    public static void validarReserva(Reserva reserva) {
        if (reserva.getIdProgramacion() <= 0) {
            throw new IllegalArgumentException("IdProgramacion debe ser mayor a 0");
        }
        
        if (reserva.getNumDocumento() == null || reserva.getNumDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("NumDocumento es requerido");
        }
        
        if (reserva.getFechaHoraReserva() == null) {
            throw new IllegalArgumentException("FechaHoraReserva es requerida");
        }
    }
    
    // Validaciones para ASISTENCIAEVENTO
    public static void validarAsistenciaEvento(AsistenciaEvento asistenciaEvento) {
        if (asistenciaEvento.getIdProgramacion() <= 0) {
            throw new IllegalArgumentException("IdProgramacion debe ser mayor a 0");
        }
        
        if (asistenciaEvento.getNumDocumento() == null || asistenciaEvento.getNumDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("NumDocumento es requerido");
        }
        
        if (asistenciaEvento.getFechaHoraAsis() == null) {
            throw new IllegalArgumentException("FechaHoraAsis es requerida");
        }
    }
}