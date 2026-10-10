# Hotfix v3.0.2 · Deriva por viento cruzado en rutas inter-sede

## Incidente

En producción (v3.0.1) el tiempo estimado de una ruta inter-sede con viento de costado era **igual** al de una ruta sin viento. Ejemplo: tramo ECI → UNAL de 60 km, rumbo norte (0°), drone a 30 km/h y viento de 10 km/h desde el este (90°):

| Versión | Velocidad sobre el suelo | Tiempo estimado |
|---|---|---|
| v3.0.1 | 30,00 km/h | 2,000 h |
| real | √(30² − 10²) = 28,28 km/h | 2,121 h |

El drone tiene que girar la proa contra el viento para no salirse de la ruta, y eso le quita velocidad de avance. La v3.0.1 solo proyectaba la componente longitudinal (`cos`) y descartaba la lateral (`sin`), así que subestimaba la llegada (unos 7 min en este tramo) y además aceptaba vientos cruzados más fuertes que el propio drone, con los que no puede mantener el rumbo.

## Corrección

`CalculadorRutaInterSede` descompone el viento respecto al rumbo:

- componente en contra: `W · cos(vientoDesde − rumbo)`;
- componente cruzada: `W · sin(vientoDesde − rumbo)`;
- velocidad sobre el suelo: `√(V² − cruzada²) − enContra`.

Si `|cruzada| ≥ V`, el tramo se rechaza con "el viento cruzado impide mantener el rumbo". Los casos de viento de cola, de frente y sin viento dan el mismo resultado que antes.

## Ciclo

1. `test(routing)`: 3 pruebas nuevas (cruzado, diagonal y cruzado al pasar por el norte) y la prueba de cruzado corregida. Fallan 4 de 8: [`evidencia/hotfix_v3.0.2_red.txt`](evidencia/hotfix_v3.0.2_red.txt).
2. `fix(routing)`: la corrección; pasan todas: [`evidencia/hotfix_v3.0.2_green.txt`](evidencia/hotfix_v3.0.2_green.txt).
3. `chore(release)`: versión del `pom.xml` a 3.0.2.
4. Merge `--no-ff` a `main`, tag `v3.0.2`, merge `--no-ff` a `develop`, borrar la rama.
