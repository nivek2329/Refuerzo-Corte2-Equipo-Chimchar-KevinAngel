# SkyCampus Enterprise v3.0.0

## Alcance

Primera versión de la base Enterprise integrada en `develop`, con analítica de eficiencia para la red multi-sede. El cálculo recibe las sedes y misiones como datos de entrada y expone resultados por sede.

## Novedades

- Tasa de éxito: misiones entregadas sobre el total de misiones de la sede.
- Tiempo promedio de entrega, calculado solo sobre misiones entregadas.
- Drone con mayor número de misiones y porcentaje de misiones urgentes.
- Sedes inactivas representadas sin valores ficticios mediante `Optional`.
- Ranking determinista por tasa de éxito, entregas completadas y código de sede.

## Verificación

- Java 17 y Maven.
- 10 pruebas aprobadas: escenarios parametrizados de sede sin actividad, una misión, empates y red completa, además de validaciones del dominio.
- `mvn verify`: JaCoCo reporta 100 % de líneas y 90,9 % de ramas; los mínimos son 85 % y 75 %.

## Artefacto

`edu.eci.skycampus:skycampus-enterprise:3.0.0`
