# 12 · TDD — ValidadorMision

Módulo Maven autónomo (Java 17, JUnit 5) para aplicar Red → Green → Refactor al requisito SC-01.
El código está en el paquete `edu.eci.skycampus`.

## Estructura

| Archivo | Rol |
|---|---|
| `src/test/java/edu/eci/skycampus/ValidadorMisionTest.java` | Pruebas de los 3 métodos del validador (patrón AAA, `@Nested` por método) |
| `src/test/java/edu/eci/skycampus/DroneTest.java` | Pruebas de las validaciones del constructor de `Drone` |
| `src/main/java/edu/eci/skycampus/ValidadorMision.java` | Batería mínima (30 %), catálogo de destinos y disponibilidad |
| `src/main/java/edu/eci/skycampus/Drone.java` | Record mínimo del módulo; batería entre 0 y 100 |
| `src/main/java/edu/eci/skycampus/DestinoInvalidoException.java` | Excepción específica para destinos fuera del catálogo |

`Drone` es propio de este módulo porque cada carpeta del repositorio compila de forma independiente.

## Ciclo TDD

1. **Red** — se escribieron primero 9 pruebas (3 por método). `mvn test` falló en compilación porque `ValidadorMision`, `Drone` y `DestinoInvalidoException` no existían.
2. **Green** — implementación mínima:
   - `tieneBateriaSuficiente(Drone)`: acepta desde 30 % inclusive.
   - `validarDestino(String)`: acepta Bloque A, Bloque B, Bloque C, Bloque D y Biblioteca; lanza `DestinoInvalidoException` en otro caso.
   - `droneEstaDisponible(Drone)`: retorna la disponibilidad del drone.
   - Los tres rechazan `null` con `NullPointerException` y mensaje descriptivo.
   - Nota: las validaciones del constructor de `Drone` (`id`, `modelo`, `ubicacion` no nulos y batería entre 0 y 100) se escribieron en esta fase **sin una prueba previa** que las pidiera; se cubrieron después con `DroneTest` (fase 4). En los próximos retos toda validación nueva entra con su prueba en rojo primero.
3. **Refactor** — sin cambiar comportamiento (las 9 pruebas siguen en verde): código al paquete `edu.eci.skycampus`, constantes `private` con nombres que distinguen conceptos (`BATERIA_MINIMA_VALIDA`/`BATERIA_MAXIMA_VALIDA` del rango físico del drone frente a `BATERIA_MINIMA_PARA_MISION`), mensaje de rango construido desde las constantes y `serialVersionUID` en la excepción.
4. **Casos límite y endurecimiento de pruebas** — en el mismo commit se renombraron las 9 pruebas originales al formato `metodo_condicion_resultadoEsperado`, se añadió la verificación del mensaje en las pruebas de `null` y la prueba de un solo destino pasó a ser parametrizada. Además se añadieron pruebas que documentan comportamiento ya existente, por eso pasaron desde el inicio (no son un nuevo ciclo Red): batería 100 %, los 5 destinos del catálogo (`@ParameterizedTest`), variantes no exactas (`"biblioteca"`, `" Biblioteca "`, vacío), mensajes en las tres pruebas de `null` y `DroneTest` (límites 0 y 100, −1 y 101, `id`/`modelo`/`ubicacion` null).

### Nota sobre el historial de commits

El trabajo se hizo antes de versionar el módulo, así que los commits se organizaron después, reproduciendo el estado real de cada fase: `test(12): RED` contiene solo las 9 pruebas originales, `feat(12): GREEN` la implementación original, `refactor(12)` el paquete y las constantes, y `test(12)` los casos límite. El historial muestra el orden de las fases, pero por sí solo no prueba que se hicieron en ese momento. En los próximos retos se hará commit al cerrar cada fase.

## Casos cubiertos

| Método | Válido | Límite / inválido | Null |
|---|---|---|---|
| `tieneBateriaSuficiente` | 30 % (mínimo), 100 % (máximo) | 29 % | excepción con mensaje |
| `validarDestino` | los 5 destinos del catálogo | fuera del catálogo (con mensaje), minúsculas, con espacios, vacío | excepción con mensaje |
| `droneEstaDisponible` | `true` | `false` | excepción con mensaje |
| `Drone` (constructor) | batería 0 y 100 | batería −1 y 101 (con mensaje) | `id`, `modelo`, `ubicacion` con mensaje |

Total: 24 ejecuciones de prueba (contando cada valor de las pruebas parametrizadas).

## Ejecutar

Desde esta carpeta:

```bash
mvn clean verify
```

Corre las pruebas, genera el reporte JaCoCo en `target/site/jacoco/index.html` y falla si la cobertura de líneas baja del 80 %. JaCoCo se documenta en la carpeta 13 y SonarQube en la 14.
