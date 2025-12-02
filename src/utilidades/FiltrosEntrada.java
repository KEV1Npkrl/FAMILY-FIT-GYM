package utilidades;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.Toolkit;
import java.util.regex.Pattern;

/**
 * Filtros de entrada que previenen escritura de caracteres incorrectos
 */
public class FiltrosEntrada {
    
    /**
     * Filtro para solo números (celular, códigos, etc.)
     */
    public static class SoloNumerosFilter extends DocumentFilter {
        private final int maxLength;
        private final Pattern patron = Pattern.compile("[0-9+\\-\\s()]*");
        
        public SoloNumerosFilter(int maxLength) {
            this.maxLength = maxLength;
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            
            Document doc = fb.getDocument();
            if (doc.getLength() + string.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            if (patron.matcher(string).matches()) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            
            Document doc = fb.getDocument();
            if (doc.getLength() - length + text.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            if (patron.matcher(text).matches()) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
    
    /**
     * Filtro para solo números enteros
     */
    public static class SoloEnterosFilter extends DocumentFilter {
        private final int maxLength;
        private final Pattern patron = Pattern.compile("[0-9]*");
        
        public SoloEnterosFilter(int maxLength) {
            this.maxLength = maxLength;
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            
            Document doc = fb.getDocument();
            if (doc.getLength() + string.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            if (patron.matcher(string).matches()) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            
            Document doc = fb.getDocument();
            if (doc.getLength() - length + text.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            if (patron.matcher(text).matches()) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
    
    /**
     * Filtro para números decimales (precios, costos)
     */
    public static class SoloDecimalesFilter extends DocumentFilter {
        private final int maxLength;
        private final Pattern patron = Pattern.compile("[0-9]*\\.?[0-9]{0,2}");
        
        public SoloDecimalesFilter(int maxLength) {
            this.maxLength = maxLength;
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            
            Document doc = fb.getDocument();
            String texto = doc.getText(0, doc.getLength());
            String nuevoTexto = texto.substring(0, offset) + string + texto.substring(offset);
            
            if (nuevoTexto.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            if (patron.matcher(nuevoTexto).matches()) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            
            Document doc = fb.getDocument();
            String textoActual = doc.getText(0, doc.getLength());
            String nuevoTexto = textoActual.substring(0, offset) + text + textoActual.substring(offset + length);
            
            if (nuevoTexto.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            if (patron.matcher(nuevoTexto).matches()) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
    
    /**
     * Filtro para alfanumérico (documentos)
     */
    public static class AlfanumericoFilter extends DocumentFilter {
        private final int maxLength;
        private final Pattern patron = Pattern.compile("[A-Za-z0-9]*");
        
        public AlfanumericoFilter(int maxLength) {
            this.maxLength = maxLength;
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            
            Document doc = fb.getDocument();
            if (doc.getLength() + string.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            if (patron.matcher(string).matches()) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            
            Document doc = fb.getDocument();
            if (doc.getLength() - length + text.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            if (patron.matcher(text).matches()) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
    
    /**
     * Filtro para texto general con longitud máxima
     */
    public static class TextoFilter extends DocumentFilter {
        private final int maxLength;
        
        public TextoFilter(int maxLength) {
            this.maxLength = maxLength;
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            
            Document doc = fb.getDocument();
            if (doc.getLength() + string.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            super.insertString(fb, offset, string, attr);
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            
            Document doc = fb.getDocument();
            if (doc.getLength() - length + text.length() > maxLength) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            super.replace(fb, offset, length, text, attrs);
        }
    }
    
    /**
     * Aplicar filtro a un JTextField
     */
    public static void aplicarFiltro(JTextField campo, DocumentFilter filtro) {
        PlainDocument doc = (PlainDocument) campo.getDocument();
        doc.setDocumentFilter(filtro);
    }
    
    /**
     * Crear campo de texto para documento
     */
    public static JTextField crearCampoDocumento() {
        JTextField campo = new JTextField();
        aplicarFiltro(campo, new AlfanumericoFilter(20));
        return campo;
    }
    
    /**
     * Crear campo de texto para celular
     */
    public static JTextField crearCampoCelular() {
        JTextField campo = new JTextField();
        aplicarFiltro(campo, new SoloNumerosFilter(20));
        return campo;
    }
    
    /**
     * Crear campo de texto para enteros
     */
    public static JTextField crearCampoEntero(int maxLength) {
        JTextField campo = new JTextField();
        aplicarFiltro(campo, new SoloEnterosFilter(maxLength));
        return campo;
    }
    
    /**
     * Crear campo de texto para decimales
     */
    public static JTextField crearCampoDecimal(int maxLength) {
        JTextField campo = new JTextField();
        aplicarFiltro(campo, new SoloDecimalesFilter(maxLength));
        return campo;
    }
    
    /**
     * Crear campo de texto general con longitud máxima
     */
    public static JTextField crearCampoTexto(int maxLength) {
        JTextField campo = new JTextField();
        aplicarFiltro(campo, new TextoFilter(maxLength));
        return campo;
    }
}