package utilidades;

import javax.swing.*;
import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Validador para campos de UI con mensajes de error específicos
 */
public class ValidadorUI {
    
    // Patrones de validación
    private static final Pattern PATRON_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PATRON_CELULAR = Pattern.compile("^[0-9+\\-\\s()]+$");
    private static final Pattern PATRON_DOCUMENTO = Pattern.compile("^[A-Za-z0-9]+$");
    private static final Pattern PATRON_SOLO_NUMEROS = Pattern.compile("^[0-9]+$");
    private static final Pattern PATRON_DECIMAL = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");
    
    /**
     * Valida campo de documento
     */
    public static boolean validarDocumento(Component padre, String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            mostrarError(padre, "El campo " + nombreCampo + " es obligatorio");
            return false;
        }
        
        valor = valor.trim();
        
        if (valor.length() > 20) {
            mostrarError(padre, nombreCampo + " no puede tener más de 20 caracteres");
            return false;
        }
        
        if (!PATRON_DOCUMENTO.matcher(valor).matches()) {
            mostrarError(padre, nombreCampo + " solo puede contener letras y números");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida campo de texto con longitud máxima
     */
    public static boolean validarTexto(Component padre, String valor, String nombreCampo, int maxLength, boolean obligatorio) {
        if (obligatorio && (valor == null || valor.trim().isEmpty())) {
            mostrarError(padre, "El campo " + nombreCampo + " es obligatorio");
            return false;
        }
        
        if (valor != null && valor.length() > maxLength) {
            mostrarError(padre, nombreCampo + " no puede tener más de " + maxLength + " caracteres");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida número entero
     */
    public static boolean validarEntero(Component padre, String valor, String nombreCampo, int minimo) {
        if (valor == null || valor.trim().isEmpty()) {
            mostrarError(padre, "El campo " + nombreCampo + " es obligatorio");
            return false;
        }
        
        valor = valor.trim();
        
        if (!PATRON_SOLO_NUMEROS.matcher(valor).matches()) {
            mostrarError(padre, nombreCampo + " debe contener solo números");
            return false;
        }
        
        try {
            int numero = Integer.parseInt(valor);
            if (numero < minimo) {
                mostrarError(padre, nombreCampo + " debe ser mayor o igual a " + minimo);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError(padre, nombreCampo + " no es un número válido");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida número decimal (precio, costo, etc.)
     */
    public static boolean validarDecimal(Component padre, String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            mostrarError(padre, "El campo " + nombreCampo + " es obligatorio");
            return false;
        }
        
        valor = valor.trim();
        
        if (!PATRON_DECIMAL.matcher(valor).matches()) {
            mostrarError(padre, nombreCampo + " debe ser un número decimal válido (máximo 2 decimales)");
            return false;
        }
        
        try {
            BigDecimal numero = new BigDecimal(valor);
            if (numero.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarError(padre, nombreCampo + " debe ser mayor a 0");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError(padre, nombreCampo + " no es un número válido");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida email
     */
    public static boolean validarEmail(Component padre, String valor, String nombreCampo, boolean obligatorio) {
        if (!obligatorio && (valor == null || valor.trim().isEmpty())) {
            return true; // Email es opcional
        }
        
        if (obligatorio && (valor == null || valor.trim().isEmpty())) {
            mostrarError(padre, "El campo " + nombreCampo + " es obligatorio");
            return false;
        }
        
        valor = valor.trim();
        
        if (valor.length() > 100) {
            mostrarError(padre, nombreCampo + " no puede tener más de 100 caracteres");
            return false;
        }
        
        if (!PATRON_EMAIL.matcher(valor).matches()) {
            mostrarError(padre, "Ingrese un " + nombreCampo + " válido (ejemplo: usuario@dominio.com)");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida teléfono/celular
     */
    public static boolean validarCelular(Component padre, String valor, String nombreCampo, boolean obligatorio) {
        if (!obligatorio && (valor == null || valor.trim().isEmpty())) {
            return true; // Celular es opcional
        }
        
        if (obligatorio && (valor == null || valor.trim().isEmpty())) {
            mostrarError(padre, "El campo " + nombreCampo + " es obligatorio");
            return false;
        }
        
        valor = valor.trim();
        
        if (valor.length() > 20) {
            mostrarError(padre, nombreCampo + " no puede tener más de 20 caracteres");
            return false;
        }
        
        if (!PATRON_CELULAR.matcher(valor).matches()) {
            mostrarError(padre, nombreCampo + " solo puede contener números, espacios, guiones, paréntesis y el signo +");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida fecha
     */
    public static boolean validarFecha(Component padre, String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            mostrarError(padre, "El campo " + nombreCampo + " es obligatorio");
            return false;
        }
        
        valor = valor.trim();
        
        try {
            LocalDate.parse(valor);
            // Validaciones adicionales de fecha si es necesario
            return true;
        } catch (DateTimeParseException e) {
            mostrarError(padre, nombreCampo + " debe tener formato YYYY-MM-DD (ejemplo: 2025-12-02)");
            return false;
        }
    }
    
    /**
     * Valida que una fecha sea posterior a otra
     */
    public static boolean validarFechaPosterior(Component padre, String fechaInicio, String fechaFin, String nombreCampoInicio, String nombreCampoFin) {
        try {
            LocalDate inicio = LocalDate.parse(fechaInicio.trim());
            LocalDate fin = LocalDate.parse(fechaFin.trim());
            
            if (fin.isBefore(inicio) || fin.isEqual(inicio)) {
                mostrarError(padre, nombreCampoFin + " debe ser posterior a " + nombreCampoInicio);
                return false;
            }
            
            return true;
        } catch (DateTimeParseException e) {
            mostrarError(padre, "Las fechas deben tener formato YYYY-MM-DD");
            return false;
        }
    }
    
    /**
     * Valida combo box (no vacío)
     */
    public static boolean validarComboBox(Component padre, Object valorSeleccionado, String nombreCampo) {
        if (valorSeleccionado == null) {
            mostrarError(padre, "Debe seleccionar un valor para " + nombreCampo);
            return false;
        }
        
        String texto = valorSeleccionado.toString().trim();
        if (texto.isEmpty() || texto.equals("-- Seleccionar --")) {
            mostrarError(padre, "Debe seleccionar un valor para " + nombreCampo);
            return false;
        }
        
        return true;
    }
    
    /**
     * Muestra mensaje de error en ventana emergente
     */
    public static void mostrarError(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Muestra mensaje de éxito
     */
    public static void mostrarExito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra mensaje de advertencia
     */
    public static void mostrarAdvertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Muestra confirmación
     */
    public static boolean confirmar(Component padre, String mensaje) {
        int resultado = JOptionPane.showConfirmDialog(padre, mensaje, "Confirmación", JOptionPane.YES_NO_OPTION);
        return resultado == JOptionPane.YES_OPTION;
    }
}