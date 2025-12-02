# Family Fit Gym - Proyecto Java SE (Swing + JDBC)

## Requisitos
- JDK 17 (o compatible)
- SQL Server en localhost
- Usuario `sa` y contraseña configurada en `src/otros/Conexion.java`

## Estructura inicial
```
src/
  aplicacion/AplicacionPrincipal.java
  ui/VentanaPrincipal.java
  otros/Conexion.java
  (paquetes vacíos para configuracion, dominio, excepciones, persistencia/jdbc, servicios, ui/*, utilidades, hilos, cache)
```

## Cómo ejecutar
Compila y ejecuta la clase `aplicacion.AplicacionPrincipal`. La ventana principal mostrará la barra de menús.

## Notas
- No se usan frameworks (Spring, Hibernate). Solo Java SE + Swing + JDBC.
- Los nombres de clases y paquetes están en español.