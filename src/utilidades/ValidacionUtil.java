package utilidades;

public class ValidacionUtil {
    public static boolean esEnteroPositivo(String s) {
        try { return Integer.parseInt(s) > 0; } catch (Exception e) { return false; }
    }
    public static boolean esEnteroNoNegativo(String s) {
        try { return Integer.parseInt(s) >= 0; } catch (Exception e) { return false; }
    }
    public static boolean esDecimalPositivo(String s) {
        try { return new java.math.BigDecimal(s).compareTo(java.math.BigDecimal.ZERO) > 0; } catch (Exception e) { return false; }
    }
}
