# 01 · Analytics de eficiencia de la red

## Objetivo

Calcular por sede la tasa de éxito, el tiempo promedio de entrega, el drone más utilizado y el porcentaje de misiones urgentes. Las sedes sin misiones se representan con `Optional.empty()` y no aparecen en el ranking.

## Diseño

`AnalyticsEficienciaRed` recorre la lista de misiones una sola vez mediante `groupingBy` por sede. El collector usa `teeing` para combinar conteo total, datos de entregas y agrupación anidada por ID de drone con conteo de urgentes; no guarda ni vuelve a recorrer las misiones. El promedio considera únicamente entregas completadas. El porcentaje urgente usa todas las misiones de la sede. Los empates de uso de drone se resuelven por ID ascendente.

El ranking ordena tasa de éxito descendente, misiones entregadas descendentes y código de sede ascendente. Así, los resultados son deterministas aunque cambie el orden de entrada. `ResultadoAnalyticsRed` entrega copias inmutables de las métricas y del ranking.

## Escenarios y verificación

Las pruebas parametrizadas cubren sede vacía, una misión urgente entregada, empate entre sedes y red completa. También se prueban empates de uso de drones y validaciones de dominio.

Desde `Infernape/skycampus-enterprise`:

```powershell
mvn test
mvn verify
```

Cobertura verificada con JaCoCo: 100 % de líneas y 90,9 % de ramas (umbrales requeridos: 85 % y 75 %).

## Técnicas Streams

El resultado representa las sedes inactivas mediante `Optional.empty()`. El ranking usa un comparador compuesto; la agrupación anidada `sede → drone → cantidad` da el drone más utilizado y `teeing` combina las métricas en un único recorrido.
