package servicios;

import dominio.*;
import otros.Conexion;
import utilidades.ValidadorEntidades;

import java.sql.*;
import java.time.LocalDate;

/**
 * Servicio para operaciones transaccionales que involucran múltiples tablas
 */
public class ServicioTransaccional {

    /**
     * Inserta una nueva persona y empleado en una sola transacción
     * Primero valida, luego inserta en PERSONA y después en EMPLEADO
     */
    public boolean insertarEmpleadoCompleto(Empleado empleado) {
        // Validar antes de iniciar transacción
        try {
            ValidadorEntidades.validarEmpleado(empleado);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación en insertarEmpleadoCompleto: " + e.getMessage());
            return false;
        }

        Connection cn = null;
        try {
            cn = Conexion.iniciarConexion();
            if (cn == null) {
                System.err.println("Sin conexión a BD en insertarEmpleadoCompleto");
                return false;
            }

            cn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar en PERSONA
            String sqlPersona = "INSERT INTO PERSONA (NumDocumento, Nombres, Apellidos, PasswordHass, FechaRegistro, Celular, Correo) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psPersona = cn.prepareStatement(sqlPersona)) {
                psPersona.setString(1, empleado.getNumDocumento());
                psPersona.setString(2, empleado.getNombres());
                psPersona.setString(3, empleado.getApellidos());
                psPersona.setString(4, empleado.getPasswordHass());
                psPersona.setDate(5, Date.valueOf(empleado.getFechaRegistro()));
                psPersona.setString(6, empleado.getCelular());
                psPersona.setString(7, empleado.getCorreo());
                
                if (psPersona.executeUpdate() != 1) {
                    throw new SQLException("Error al insertar en tabla PERSONA");
                }
            }

            // 2. Insertar en EMPLEADO
            String sqlEmpleado = "INSERT INTO EMPLEADO (NumDocumento, TipoEmpleado) VALUES (?, ?)";
            try (PreparedStatement psEmpleado = cn.prepareStatement(sqlEmpleado)) {
                psEmpleado.setString(1, empleado.getNumDocumento());
                psEmpleado.setString(2, empleado.getTipoEmpleado());
                
                if (psEmpleado.executeUpdate() != 1) {
                    throw new SQLException("Error al insertar en tabla EMPLEADO");
                }
            }

            cn.commit(); // Confirmar transacción
            System.out.println("Empleado insertado correctamente: " + empleado.getNumDocumento());
            return true;

        } catch (SQLException e) {
            System.err.println("Error SQL en insertarEmpleadoCompleto: " + e.getMessage());
            e.printStackTrace();
            if (cn != null) {
                try {
                    cn.rollback(); // Revertir transacción
                    System.out.println("Transacción revertida");
                } catch (SQLException rollbackEx) {
                    System.err.println("Error al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (cn != null) {
                try {
                    cn.setAutoCommit(true); // Restaurar auto-commit
                    cn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error al cerrar conexión: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Inserta una nueva persona y socio en una sola transacción
     */
    public boolean insertarSocioCompleto(Socio socio) {
        // Validar antes de iniciar transacción
        try {
            ValidadorEntidades.validarPersona(socio);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación en insertarSocioCompleto: " + e.getMessage());
            return false;
        }

        Connection cn = null;
        try {
            cn = Conexion.iniciarConexion();
            if (cn == null) {
                System.err.println("Sin conexión a BD en insertarSocioCompleto");
                return false;
            }

            cn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar en PERSONA
            String sqlPersona = "INSERT INTO PERSONA (NumDocumento, Nombres, Apellidos, PasswordHass, FechaRegistro, Celular, Correo) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psPersona = cn.prepareStatement(sqlPersona)) {
                psPersona.setString(1, socio.getNumDocumento());
                psPersona.setString(2, socio.getNombres());
                psPersona.setString(3, socio.getApellidos());
                psPersona.setString(4, socio.getPasswordHass());
                psPersona.setDate(5, Date.valueOf(socio.getFechaRegistro()));
                psPersona.setString(6, socio.getCelular());
                psPersona.setString(7, socio.getCorreo());
                
                if (psPersona.executeUpdate() != 1) {
                    throw new SQLException("Error al insertar en tabla PERSONA");
                }
            }

            // 2. Insertar en SOCIO
            String sqlSocio = "INSERT INTO SOCIO (NumDocumento) VALUES (?)";
            try (PreparedStatement psSocio = cn.prepareStatement(sqlSocio)) {
                psSocio.setString(1, socio.getNumDocumento());
                
                if (psSocio.executeUpdate() != 1) {
                    throw new SQLException("Error al insertar en tabla SOCIO");
                }
            }

            cn.commit(); // Confirmar transacción
            System.out.println("Socio insertado correctamente: " + socio.getNumDocumento());
            return true;

        } catch (SQLException e) {
            System.err.println("Error SQL en insertarSocioCompleto: " + e.getMessage());
            e.printStackTrace();
            if (cn != null) {
                try {
                    cn.rollback(); // Revertir transacción
                    System.out.println("Transacción revertida");
                } catch (SQLException rollbackEx) {
                    System.err.println("Error al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (cn != null) {
                try {
                    cn.setAutoCommit(true); // Restaurar auto-commit
                    cn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error al cerrar conexión: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Elimina un empleado (de EMPLEADO y PERSONA) en una sola transacción
     */
    public boolean eliminarEmpleadoCompleto(String numDocumento) {
        if (numDocumento == null || numDocumento.trim().isEmpty()) {
            System.err.println("NumDocumento no puede estar vacío");
            return false;
        }

        Connection cn = null;
        try {
            cn = Conexion.iniciarConexion();
            if (cn == null) {
                System.err.println("Sin conexión a BD en eliminarEmpleadoCompleto");
                return false;
            }

            cn.setAutoCommit(false); // Iniciar transacción

            // 1. Eliminar de EMPLEADO (FK primero)
            String sqlEmpleado = "DELETE FROM EMPLEADO WHERE NumDocumento = ?";
            try (PreparedStatement psEmpleado = cn.prepareStatement(sqlEmpleado)) {
                psEmpleado.setString(1, numDocumento);
                psEmpleado.executeUpdate(); // Puede ser 0 si no existe
            }

            // 2. Eliminar de PERSONA
            String sqlPersona = "DELETE FROM PERSONA WHERE NumDocumento = ?";
            try (PreparedStatement psPersona = cn.prepareStatement(sqlPersona)) {
                psPersona.setString(1, numDocumento);
                int filasAfectadas = psPersona.executeUpdate();
                
                if (filasAfectadas == 0) {
                    System.out.println("No se encontró persona con documento: " + numDocumento);
                }
            }

            cn.commit(); // Confirmar transacción
            System.out.println("Empleado eliminado correctamente: " + numDocumento);
            return true;

        } catch (SQLException e) {
            System.err.println("Error SQL en eliminarEmpleadoCompleto: " + e.getMessage());
            e.printStackTrace();
            if (cn != null) {
                try {
                    cn.rollback(); // Revertir transacción
                    System.out.println("Transacción revertida");
                } catch (SQLException rollbackEx) {
                    System.err.println("Error al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (cn != null) {
                try {
                    cn.setAutoCommit(true); // Restaurar auto-commit
                    cn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error al cerrar conexión: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Elimina un socio (de SOCIO y PERSONA) en una sola transacción
     */
    public boolean eliminarSocioCompleto(String numDocumento) {
        if (numDocumento == null || numDocumento.trim().isEmpty()) {
            System.err.println("NumDocumento no puede estar vacío");
            return false;
        }

        Connection cn = null;
        try {
            cn = Conexion.iniciarConexion();
            if (cn == null) {
                System.err.println("Sin conexión a BD en eliminarSocioCompleto");
                return false;
            }

            cn.setAutoCommit(false); // Iniciar transacción

            // 1. Eliminar de SOCIO (FK primero)
            String sqlSocio = "DELETE FROM SOCIO WHERE NumDocumento = ?";
            try (PreparedStatement psSocio = cn.prepareStatement(sqlSocio)) {
                psSocio.setString(1, numDocumento);
                psSocio.executeUpdate(); // Puede ser 0 si no existe
            }

            // 2. Eliminar de PERSONA
            String sqlPersona = "DELETE FROM PERSONA WHERE NumDocumento = ?";
            try (PreparedStatement psPersona = cn.prepareStatement(sqlPersona)) {
                psPersona.setString(1, numDocumento);
                int filasAfectadas = psPersona.executeUpdate();
                
                if (filasAfectadas == 0) {
                    System.out.println("No se encontró persona con documento: " + numDocumento);
                }
            }

            cn.commit(); // Confirmar transacción
            System.out.println("Socio eliminado correctamente: " + numDocumento);
            return true;

        } catch (SQLException e) {
            System.err.println("Error SQL en eliminarSocioCompleto: " + e.getMessage());
            e.printStackTrace();
            if (cn != null) {
                try {
                    cn.rollback(); // Revertir transacción
                    System.out.println("Transacción revertida");
                } catch (SQLException rollbackEx) {
                    System.err.println("Error al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (cn != null) {
                try {
                    cn.setAutoCommit(true); // Restaurar auto-commit
                    cn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error al cerrar conexión: " + closeEx.getMessage());
                }
            }
        }
    }
}