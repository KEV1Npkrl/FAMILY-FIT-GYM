package seguridad;

import dominio.SesionUsuario;
import dominio.TipoUsuario;

/**
 * Controlador de permisos del sistema
 * Define qué puede hacer cada tipo de usuario
 */
public class ControladorPermisos {
    
    private static SesionUsuario sesion = SesionUsuario.getInstance();
    
    /**
     * Verifica si el usuario actual puede acceder a funciones administrativas
     */
    public static boolean puedeAdministrar() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede crear/editar/eliminar datos
     */
    public static boolean puedeModificar() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede ver reportes completos
     */
    public static boolean puedeVerReportes() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede gestionar otros usuarios
     */
    public static boolean puedeGestionarUsuarios() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede marcar asistencia
     */
    public static boolean puedeMarcarAsistencia() {
        return true; // Tanto socios como empleados pueden marcar asistencia
    }
    
    /**
     * Verifica si el usuario actual puede ver sus propios datos
     */
    public static boolean puedeVerDatosPersonales() {
        return true; // Todos pueden ver sus propios datos
    }
    
    /**
     * Verifica si el usuario actual puede cambiar su contraseña
     */
    public static boolean puedeCambiarPassword() {
        return true; // Todos pueden cambiar su contraseña
    }
    
    /**
     * Verifica si el usuario actual puede registrarse en eventos
     */
    public static boolean puedeRegistrarseEnEventos() {
        return true; // Todos pueden intentar registrarse (se valida con plan)
    }
    
    /**
     * Verifica si el usuario actual puede acceder al módulo de maestros
     */
    public static boolean puedeAccederMaestros() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede acceder al módulo de transacciones
     */
    public static boolean puedeAccederTransacciones() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede acceder a consultas avanzadas
     */
    public static boolean puedeAccederConsultas() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede ver datos de otros usuarios
     */
    public static boolean puedeVerDatosDeOtros() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede acceder a gestión de socios
     */
    public static boolean puedeGestionarSocios() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede acceder a gestión de empleados
     */
    public static boolean puedeGestionarEmpleados() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede acceder a gestión de membresías
     */
    public static boolean puedeGestionarMembresias() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede acceder a gestión de pagos
     */
    public static boolean puedeGestionarPagos() {
        return esEmpleado();
    }
    
    /**
     * Verifica si el usuario actual puede acceder a gestión de planes
     */
    public static boolean puedeGestionarPlanes() {
        return esEmpleado();
    }
    
    /**
     * Obtiene el documento del usuario logueado (útil para filtros)
     */
    public static String getDocumentoUsuarioActual() {
        return sesion.esSesionActiva() ? sesion.getDocumentoUsuario() : null;
    }
    
    /**
     * Verifica si el usuario actual es empleado
     */
    private static boolean esEmpleado() {
        return sesion.esSesionActiva() && 
               sesion.getTipoUsuario() == TipoUsuario.EMPLEADO;
    }
    
    /**
     * Verifica si el usuario actual es socio
     */
    public static boolean esSocio() {
        return sesion.esSesionActiva() && 
               sesion.getTipoUsuario() == TipoUsuario.SOCIO;
    }
    
    /**
     * Mensaje de error estándar para acceso denegado
     */
    public static String getMensajeAccesoDenegado() {
        if (esSocio()) {
            return "Como socio, no tienes permisos para acceder a esta funcionalidad.\n" +
                   "Solo puedes: marcar asistencia, ver tus datos personales, " +
                   "cambiar contraseña y registrarte en eventos.";
        } else {
            return "No tienes permisos suficientes para realizar esta acción.";
        }
    }
    
    /**
     * Verifica permisos usando función lambda y muestra mensaje si no tiene acceso
     */
    public static boolean verificarYMostrarError(java.util.function.Supplier<Boolean> validadorPermiso, java.awt.Component parent) {
        boolean tienePermiso = validadorPermiso.get();
        if (!tienePermiso) {
            javax.swing.JOptionPane.showMessageDialog(parent, 
                getMensajeAccesoDenegado(),
                "Acceso Denegado", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    /**
     * Verifica permisos y muestra mensaje si no tiene acceso (versión simple)
     */
    public static boolean verificarYMostrarError(boolean tienePermiso) {
        if (!tienePermiso) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                getMensajeAccesoDenegado(),
                "Acceso Denegado", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}