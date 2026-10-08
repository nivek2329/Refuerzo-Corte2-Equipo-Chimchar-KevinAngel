# 13 · JaCoCo — Cobertura de código

El plugin JaCoCo está configurado en `../12 · TDD/pom.xml`:

- `prepare-agent`: instrumenta las pruebas (datos en `${java.io.tmpdir}/skycampus-tdd-jacoco.exec`, ver nota técnica).
- `report` (fase `test`): genera `target/site/jacoco/index.html`.
- `check` (fase `verify`): exige mínimo 80 % de cobertura de líneas (`LINE COVEREDRATIO ≥ 0.80`).

## Cómo reproducirlo

```bash
cd "12 · TDD"
mvn clean verify
```

Abrir `target/site/jacoco/index.html`.

## Resultado (`mvn clean verify`, 08/10/2026)

| Métrica | Valor |
|---|---|
| Pruebas | 24 ejecutadas, 0 fallos, 0 errores (JUnit 5) |
| Cobertura de instrucciones | 100 % (89 de 89) |
| Cobertura de líneas | 100 % (19 de 19) |
| Cobertura de ramas | 100 % (8 de 8) |
| Regla `check` (≥ 80 % líneas) | Cumplida: "All coverage checks have been met" |

| Clase | Líneas | Ramas |
|---|---|---|
| `ValidadorMision` | 10/10 | 4/4 |
| `Drone` | 7/7 | 4/4 |
| `DestinoInvalidoException` | 2/2 | — |

Evidencia: `Reporte_JaCoCo.png` (captura de `target/site/jacoco/index.html`).

## Nota técnica

En Windows, la ruta del archivo de datos de JaCoCo se corrompe cuando contiene caracteres no ASCII, como el `·` de la carpeta `12 · TDD`: la consola muestra la ruta como `12 À TDD` y el archivo quedó como `jacoco.exe` en vez de `jacoco.exec`. En esa primera ejecución el `report` y el `check` se saltaron sin fallar ("missing execution data file").

Solución en el `pom.xml`:

- Los datos de ejecución se guardan en la carpeta temporal del sistema (`jacoco.execFile` = `${java.io.tmpdir}/skycampus-tdd-jacoco.exec`) con `append=false`.
- `maven-clean-plugin` borra ese archivo en `mvn clean`, así que no sobreviven datos de una ejecución anterior: con `mvn clean verify -DskipTests` no hay datos y JaCoCo no reporta cobertura.
- El reporte HTML/XML sigue generándose en `target/site/jacoco`.

Riesgo que queda: dos copias del módulo en el mismo equipo comparten ese archivo temporal; no deben ejecutarse a la vez. La solución de fondo sería una ruta sin caracteres no ASCII.
