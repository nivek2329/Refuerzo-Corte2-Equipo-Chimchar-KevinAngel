# 12 · TDD — AsignadorMision con Mockito (Monferno)

El código está en el proyecto compartido de la v2: [`skycampus-v2`](../skycampus-v2).

| Archivo | Rol |
|---|---|
| [`AsignadorMisionTest`](../skycampus-v2/src/test/java/edu/eci/skycampus/asignacion/AsignadorMisionTest.java) | Los 5 casos del enunciado, con `@Mock ApiMeteorologica` y `@Mock ObservadorDrone`. El caso 1 está separado en 3 pruebas: elección, estado y notificación |
| [`AsignadorMisionBordesTest`](../skycampus-v2/src/test/java/edu/eci/skycampus/asignacion/AsignadorMisionBordesTest.java) | Bordes: 2000 g y 2001 g, URGENTE sin EXPRESS, prioridad BAJO, flota vacía y OCP (estrategia propia para BAJO) |
| [`AsignadorMisionValidacionTest`](../skycampus-v2/src/test/java/edu/eci/skycampus/asignacion/AsignadorMisionValidacionTest.java) | Entradas inválidas: solicitud y flota `null`, drone `null` dentro de la flota, dependencias `null` y estrategia faltante para una prioridad |
| [`EstrategiasAsignacionTest`](../skycampus-v2/src/test/java/edu/eci/skycampus/asignacion/EstrategiasAsignacionTest.java) | Desempate de `AsignacionMasRapido`, y su contrato (flota vacía, paquete muy pesado) junto con las otras estrategias |
| [`AsignadorMision`](../skycampus-v2/src/main/java/edu/eci/skycampus/asignacion/AsignadorMision.java) | Valida, consulta el clima, aplica la estrategia de la prioridad y pone el drone EN_VUELO |
| [`ApiMeteorologica`](../skycampus-v2/src/main/java/edu/eci/skycampus/externo/ApiMeteorologica.java) | Interfaz del sistema externo; en las pruebas siempre es un mock |
| [`PoliticaAsignacion`](../skycampus-v2/src/main/java/edu/eci/skycampus/asignacion/PoliticaAsignacion.java) | **Política de producción**: URGENTE → `AsignacionMasRapido`; NORMAL y BAJO → `AsignacionMayorBateria`. Se puede inyectar otra sin tocar `AsignadorMision` |
| [`AsignacionMasRapido`](../skycampus-v2/src/main/java/edu/eci/skycampus/asignacion/AsignacionMasRapido.java) | Estrategia para URGENTE: el tipo más rápido entre los aptos (EXPRESS > MINI > CARGO); en empate, el de más batería |

## Flujo de `asignar(flota, solicitud)`

Cada paso ocurre antes que el siguiente, así que lo más barato y lo que no depende de nadie externo va primero.

1. Valida la flota: no puede ser `null` ni contener drones `null`.
2. Valida la solicitud: no puede ser `null`.
3. Valida el peso: rechaza paquetes de más de 2000 g, la capacidad del drone más grande (`TipoDrone.capacidadMaximaGramos()`). Si mañana se agrega un tipo más grande, el límite se ajusta solo.
4. **Solo entonces** consulta `ApiMeteorologica`. Si el clima no es apto, devuelve `Optional.empty()`.
5. Toma la estrategia configurada para la prioridad del paquete. La configuración por defecto es `PoliticaAsignacion.porDefecto()`.
6. Si elige un drone, `GestorFlota.cambiarEstado(drone, EN_VUELO)` lo pasa a EN_VUELO y avisa a los observadores.

Las pruebas verifican con `verifyNoInteractions(clima)` que los pasos 1 a 3 rechazan **sin gastar** una llamada al sistema externo.

## Por qué Mockito

`ApiMeteorologica` es un sistema externo. Con el clima real, las pruebas serían lentas y no se podrían repetir. El mock fija la respuesta (`when(clima.esApto()).thenReturn(false)`) y permite verificar la interacción:
- `verify`/`never` comprueban el aviso al notificador.
- `verifyNoInteractions` comprueba que no se consultó el clima.

El notificador es un `@Mock ObservadorDrone` suscrito a un `GestorFlota` **real**. Así, la prueba 1c comprueba la integración con el Observer.

**Por qué no `@InjectMocks`:** el objeto se construye a mano en `@BeforeEach`, por dos razones.
1. Las dependencias mezclan objetos simulados (el clima) y reales (el `GestorFlota` y las estrategias de producción). `@InjectMocks` podría inyectar un `GestorFlota` real si se declara como `@Spy`, pero si le falta un argumento pasa `null` en silencio. Construirlo a mano deja explícito qué es simulado y qué es real.
2. Antes de inyectar el mapa, el constructor recibía **dos parámetros del mismo tipo** (`EstrategiaAsignacion` normal y urgente). `@InjectMocks` resuelve el constructor por tipo, así que no habría podido distinguir cuál era cuál.

Desde el commit `eaf98b9`, el agente de Byte Buddy que usa Mockito se carga con `-javaagent` en el `argLine` de surefire. Antes se adjuntaba en caliente, y el JDK 21 advertía que eso dejará de permitirse. La advertencia desaparece desde [c3_2_build.txt](evidencia/c3_2_build.txt). `mockito-core` 5.11 no trae `Premain-Class`, por eso no se usa su propio jar.

## Casos

| Prueba | Arrange | Resultado esperado |
|---|---|---|
| 1a. Asignación exitosa | Clima apto; flota MINI 91 %, EXPRESS 60 %, CARGO 85 %, MINI 20 % | D-01 (mayor batería) |
| 1b. Estado | Igual | El drone devuelto está EN_VUELO |
| 1c. Observer | Igual | `verify(notificador).onEstadoCambiado(asignado, EN_VUELO)` |
| 2. Clima adverso | `esApto() = false` | Vacío, sin aviso |
| 3. Sin drones aptos | Uno EN_VUELO y otro con 15 % | Vacío, sin aviso |
| 4. Paquete muy pesado | 2500 g | `IllegalArgumentException` "el paquete pesa 2500 g y supera la capacidad del drone más grande (2000 g)", sin consultar el clima |
| 5. URGENTE → EXPRESS | 300 g URGENTE | D-02 (EXPRESS), aunque el MINI tiene más batería |
| Borde: 2000 g | NORMAL | D-03 (CARGO): el límite es inclusivo |
| Borde: 2001 g | NORMAL | Rechazado, sin consultar el clima: el primer valor inválido |
| Borde: URGENTE sin EXPRESS | CARGO 99 %, MINI 50 % | D-01: el siguiente más rápido, no el de más batería |
| Borde: varios EXPRESS | EXPRESS 50 %, 90 %, 70 % | D-21 (90 %): desempate por batería |
| Borde: BAJO | `PoliticaAsignacion.porDefecto()`; D-01 tiene 300 min de vuelo y D-03 solo 5 | Usa mayor batería y elige D-01, distinguiéndose de `AsignacionMenorUso` |
| OCP | BAJO → `AsignacionMenorUso` | D-03 (5 min de vuelo). `AsignadorMision` no cambió |
| Flota vacía | Clima apto | Vacío |
| Entradas inválidas | Flota o solicitud `null`, drone `null` en la flota, dependencias `null`, falta la estrategia de BAJO, prioridad `null` en el mapa | Excepción con mensaje en español. En la flota, antes de consultar el clima |

El proyecto pasa de 90 pruebas (antes del reto) a 115. La ejecución `mvn clean verify` del 09/10/2026 terminó correctamente: 115 pruebas, sin fallos ni errores; JaCoCo registró 212/212 líneas y 60/60 ramas, y el nuevo mínimo de 85 % se cumplió. La salida completa está guardada en el reto 13.

## Historial TDD

La salida real de Maven de cada fase está en [`evidencia/`](evidencia).

### Ciclo 1: los 5 casos del enunciado

| Commit | Fase | Contenido | Maven |
|---|---|---|---|
| `ad63715` | RED RED | Solo las pruebas | No compila: `AsignadorMision`, `ApiMeteorologica` y `AsignacionMasRapido` no existen → [red.txt](evidencia/red.txt) |
| `6bd4e66` | GREEN GREEN | `ApiMeteorologica`, `AsignacionMasRapido`, `TipoDrone.velocidadRelativa` y `AsignadorMision` | 99 OK → [green.txt](evidencia/green.txt) |
| `ebf9ad3` | REFACTOR REFACTOR | Capacidad derivada de `TipoDrone`; se extraen `validarPeso` y `estrategiaPara` | Las mismas 99 OK → [refactor.txt](evidencia/refactor.txt) |

### Ciclo 2: validación de dependencias

| Commit | Fase | Contenido | Maven |
|---|---|---|---|
| `5fe3031` | RED RED 2 | Flota `null` y dependencias `null` del constructor | 103, **4 fallan** por aserción ("nothing was thrown") → [red2.txt](evidencia/red2.txt) |
| `b5fbb01` | GREEN GREEN 2 | `requireNonNull` con mensaje en español | 103 OK → [green2.txt](evidencia/green2.txt) |

### Ciclo 3: respuesta a la revisión

| Commit | Fase | Contenido | Maven |
|---|---|---|---|
| `1ecfb0d` | CARACTERIZACION Caracterización | Pruebas de comportamiento que **ya existía** sin prueba: desempate de `AsignacionMasRapido`, 2001 g, BAJO y flota vacía. `AsignacionMasRapido` entra en las pruebas de contrato. La prueba 1 se separa en tres | 111 OK → [c3_1](evidencia/c3_1_caracterizacion.txt) |
| — | MUTANTES Mutantes | Ver la sección siguiente | 3 de 3 mutantes muertos |
| `eaf98b9` | BUILD Build | Byte Buddy como `-javaagent` | 111 OK, sin la advertencia → [c3_2](evidencia/c3_2_build.txt) |
| `14b0d3d` | REFACTOR REFACTOR | La relación prioridad → estrategia se inyecta como `EnumMap` (sin condicional); la constante pasa a llamarse `CAPACIDAD_TIPO_MAS_GRANDE_GRAMOS`; se documenta el contrato de inmutabilidad; prueba de OCP | 111 OK → [c3_3](evidencia/c3_3_refactor.txt) |
| `caf538b` | RED RED 3 | Drone `null` en la flota, estrategia faltante para una prioridad, mensaje "capacidad del drone más grande" | 113, **3 fallan** por aserción → [c3_4](evidencia/c3_4_red3.txt) |
| `6ce2ccd` | GREEN GREEN 3 | `validarFlota` y `validarQueCubreTodasLasPrioridades`, y el mensaje nuevo | 113 OK → [c3_5](evidencia/c3_5_green3.txt) |

**Nota sobre `14b0d3d`.** Además de reestructurar, ese commit cambió la firma del constructor (de dos estrategias a un mapa) y agregó la prueba de OCP. No fue un refactor puro: lo correcto habría sido separar el cambio de API y la prueba nueva en su propio commit.

### Ciclo 4: la política vive en producción

En el ciclo 3, la relación prioridad → estrategia por defecto había quedado definida solo en el código de pruebas (`Datos`). La regla "URGENTE → el más rápido" la garantizaba un helper de pruebas, no el sistema.

| Commit | Fase | Contenido | Maven |
|---|---|---|---|
| `d7707df` | REFACTOR REFACTOR | La política se mueve de `Datos` a `PoliticaAsignacion` (`src/main`), y las pruebas la usan desde ahí; `validarQueCubreTodasLasPrioridades` pasa a stream. Además, la prueba de 2001 g ahora también verifica el mensaje | 113 OK → [c4_1](evidencia/c4_1_refactor.txt) |
| — | MUTANTES Mutantes 4 y 5 | Sobre la política de producción (ver la tabla de mutantes) | 2 de 2 muertos |
| `3ff4d74` | RED RED 4 | Un mapa con prioridad `null` debe dar un mensaje claro | 114, **1 falla** por aserción (esperaba `IllegalArgumentException`, recibió un NPE sin mensaje de `EnumMap.putAll`) → [c4_2](evidencia/c4_2_red4.txt) |
| `899bd5b` | GREEN GREEN 4 | `copiarEstrategias` valida la clave antes de copiar | 114 OK → [c4_3](evidencia/c4_3_green4.txt) |

### Pruebas de mutación: las pruebas de caracterización sí detectan errores

Una prueba escrita para código que ya existe nace en verde, así que por sí sola no demuestra nada. Para comprobar que sirven, los scripts [`mutantes_12.ps1`](mutantes_12.ps1) (sobre `1ecfb0d`) y [`mutantes_12_politica.ps1`](mutantes_12_politica.ps1) (sobre `d7707df`) hicieron lo siguiente en el PC del estudiante:
1. Cambió una línea de producción, introduciendo un error deliberado.
2. Corrió `mvn clean test`.
3. Restauró el archivo original.

| Mutante | Cambio | Prueba que lo detecta | Evidencia |
|---|---|---|---|
| 1 | `.thenComparingInt(Drone::bateria)` → `-drone.bateria()` (desempate invertido) | `masRapido_variosExpressAptos_eligeMayorBateria` | [mutante_1](evidencia/mutante_1_desempate_invertido.txt): 111, 1 falla |
| 2 | `peso > CAPACIDAD` → `peso > CAPACIDAD + 1` | `asignar_pesoUnGramoSobreElMaximo_lanzaExcepcion` | [mutante_2](evidencia/mutante_2_limite_peso_mas_uno.txt): 111, 1 falla |
| 3 | `== URGENTE` → `!= NORMAL` (BAJO usaría la estrategia urgente) | `asignar_misionBaja_usaEstrategiaNormal` | [mutante_3](evidencia/mutante_3_bajo_usa_urgente.txt): 111, 1 falla |
| 4 | En `PoliticaAsignacion`: URGENTE → `AsignacionMayorBateria` | `asignar_misionUrgente_asignaExpress` y `asignar_urgenteSinExpress_asignaElSiguienteMasRapido` | [mutante_4](evidencia/mutante_4_politica_urgente_mayor_bateria.txt): 113, 2 fallan |
| 5 | En `PoliticaAsignacion`: BAJO → `AsignacionMasRapido` | `asignar_misionBaja_usaEstrategiaNormal` | [mutante_5](evidencia/mutante_5_politica_bajo_mas_rapido.txt): 113, 1 falla |

Con las 103 pruebas del ciclo 2, los mutantes 1 a 3 **sobrevivían**: eso fue lo que detectó la revisión. El mutante 3 atacaba el ternario de `AsignadorMision`, que desapareció en `14b0d3d`. Desde el ciclo 4, esa regla vive en `PoliticaAsignacion`, y por eso los mutantes 4 y 5 se aplican ahí.

### Lecciones y nota sobre el proceso

- **El desempate entró sin prueba.** En GREEN (`6bd4e66`), `AsignacionMasRapido` incluyó un desempate por batería que ninguna prueba pedía. Era justo lo que se quería evitar después de Chimchar. Se corrigió con una prueba de caracterización y un mutante que lo demuestra. Desde entonces, cada regla nueva entra con una prueba capaz de fallar.
- **Caracterización BAJO añadida después de la evidencia histórica.** Ahora D-01 tiene mayor batería y 300 minutos de vuelo, mientras D-03 tiene menos batería y solo 5 minutos. La política predeterminada debe elegir D-01; si BAJO se enruta por error a `AsignacionMenorUso`, la prueba falla. La evidencia de mutación listada arriba corresponde al historial anterior a este refuerzo y no se volvió a generar.
- **RED de compilación frente a RED de aserción.** El RED 1 solo demuestra que el código no compila: ninguna prueba llegó a ejecutarse en rojo. Los RED 2, 3 y 4 fallan por aserción, que es lo que demuestra que cada prueba puede fallar.
- **Cómo se trabajó.** En los cuatro ciclos, el código de cada fase se preparó por adelantado y se verificó localmente en orden: las pruebas, en rojo, antes que la implementación. Luego se aplicó en el repositorio fase por fase, en una sola sesión por ciclo, y por eso hay pocos segundos entre commits.
  - Ciclos 1 y 2: el commit se hizo antes de correr Maven. La evidencia de RED 1 se regeneró después, haciendo checkout de ese commit.
  - Ciclos 3 y 4: en cada fase, Maven se ejecutó **antes** del commit. Lo muestra la hora `Finished at` de cada evidencia, segundos antes de su commit.
  - El historial demuestra el orden de las fases y que cada RED falla. No demuestra que el código se escribiera en el momento del commit.

### Decisiones de diseño

- **`GestorFlota` sigue siendo una clase concreta.** Se inyecta por constructor, y es un servicio propio del dominio, no un sistema externo. Crearle una interfaz solo para poder simularlo agregaría una abstracción sin un segundo uso (YAGNI). Las pruebas lo usan real y simulan a los observadores.
- **Un `Optional.empty()` no dice por qué no se asignó.** El resultado vacío no distingue entre clima adverso, ningún drone apto, o ningún drone de la flota capaz de llevar el peso, aunque exista un tipo que sí podría. Por ahora se acepta, porque el reto no pide un motivo. Si el panel del operador lo necesita, el retorno pasaría a ser un resultado con motivo.
- **Inmutabilidad:** `asignar` no modifica la lista recibida. Quien llama debe reemplazar en su flota el drone devuelto. Está documentado en el Javadoc de `AsignadorMision`.

## Ejecutar

```
cd Monferno\skycampus-v2
mvn clean verify
```

`mvn clean verify` corre las 115 pruebas. JaCoCo exige al menos 85 % de líneas y 70 % de ramas (reto 13).
