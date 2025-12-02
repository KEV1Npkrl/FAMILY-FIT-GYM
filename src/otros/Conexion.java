package otros;

import excepciones.ConexionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    private static final String URL =
        // Usar TCP/IP con puerto 1433 (puerto por defecto habilitado)
        "jdbc:sqlserver://localhost:1433;" +
        "databaseName=familyfitgym;" +
        "encrypt=false;" +
        "trustServerCertificate=true;" +
        "loginTimeout=30;";

    static {
        try {
            // Carga explícita del driver (útil si el auto-registro JDBC 4.0 no ocurre)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC de SQL Server no encontrado en el classpath. Coloque mssql-jdbc-*.jar en la carpeta lib/ y reinicie.");
            throw new ConexionException("Driver JDBC no encontrado", e);
        }
    }

    public static Connection iniciarConexion() throws ConexionException {
        try {
            // Usar credenciales SQL específicas
            Connection bd = DriverManager.getConnection(URL, "heidy", "fitgym@2025hei");
            System.out.println("¡Conexión establecida con familyfitgym!");
            return bd;
        } catch (SQLException e) {
            String mensaje = "Error al conectar con la base de datos: " + e.getMessage();
            System.err.println(mensaje);
            e.printStackTrace();
            throw new ConexionException(mensaje, "localhost:1433", "familyfitgym", e);
        }
    }
    
    /**
     * Método que intenta la conexión sin lanzar excepción (para compatibilidad)
     */
    @Deprecated
    public static Connection iniciarConexionLegacy() {
        try {
            return iniciarConexion();
        } catch (ConexionException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}