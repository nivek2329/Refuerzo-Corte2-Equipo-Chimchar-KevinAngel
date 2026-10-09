# 04 · Principios SOLID — Monferno (GestorDrone del MVP vs. SkyCampus v2)

**Antes:** `GestorDrone` del MVP, analizado en [`../../04 · Principios SOLID`](../../04%20%C2%B7%20Principios%20SOLID/README.md).
En Chimchar ya lo rediseñamos en `RepositorioMision`, `AlertaOperador`, `GeneradorReporte`, `EstrategiaRuta` y `AsignadorMision`.

**Después:** la arquitectura v2 en [`skycampus-v2`](../skycampus-v2).

## ¿Qué pasó con cada responsabilidad del `GestorDrone`?

La v2 no reimplementa todo el MVP: se enfoca en la asignación automática y en las alertas. Por eso cada responsabilidad se resuelve en un lugar distinto.

| Responsabilidad en `GestorDrone` | En la v2 | Dónde quedó resuelta |
|---|---|---|
| `asignarMision` | **Reemplazada:** `GestorMisiones` (contexto) + `EstrategiaAsignacion` (3 implementaciones) + `CriterioAptitud` | v2 |
| `enviarAlertaEmail` | **Reemplazada:** Observer → `PanelOperador` (operador), `AlertaTecnico` (técnico), `SistemaLog` | v2. El canal email concreto era `AlertaOperadorEmail` en Chimchar 04 |
| `calcularRuta` (if/else por tipo) | Fuera del alcance de la v2 | Chimchar 04: `EstrategiaRuta` → `RutaDirecta`, `RutaEvitandoEdificios` |
| `guardarEnBD` (MySQL, `"root","1234"`) | Fuera del alcance de la v2: no hay persistencia | Chimchar 04: `RepositorioMision` (interfaz) con las credenciales inyectadas en `RepositorioMisionMySQL` |
| `generarReportePDF` | **Sustituida por métricas:** `EstadisticasMisiones` calcula los datos del dashboard (reto 01). El PDF queda fuera del alcance | Chimchar 04: `GeneradorReporte` |

## Principio por principio

| Principio | (a) Cómo estaba violado en `GestorDrone` | (b) Cómo lo corrige la v2 |
|---|---|---|
| **SRP** | Una clase tenía cinco razones para cambiar (tabla de arriba). | Cada clase de la v2 tiene una sola: `CriterioAptitud` decide qué drone es apto; cada `Asignacion*` aplica un criterio de elección; `GestorMisiones` crea la misión; `GestorFlota` cambia estados y avisa; `EstadoDrone` define las transiciones válidas; `PanelOperador`, `SistemaLog` y `AlertaTecnico` presentan, registran y alertan; `EstadisticasMisiones` calcula métricas. Ninguna clase de producción pasa de 80 líneas. |
| **OCP** | `calcularRuta` usaba `if (tipo.equals("DIRECTO")) … else if ("EVITAR")`: una ruta nueva exigía editar el método. Según el enunciado de Monferno, la asignación "hoy asigna el de mayor batería": un criterio fijo, sin punto de extensión. | Una forma nueva de asignar es una clase nueva que implementa `EstrategiaAsignacion`. Un aviso nuevo es una clase nueva que implementa `ObservadorDrone`. `GestorMisiones` y `GestorFlota` no se tocan; lo demuestran las pruebas de abajo. |
| **LSP** | No aplicaba: no había jerarquías. | Cualquier `EstrategiaAsignacion` sustituye a otra sin que `GestorMisiones` note la diferencia. Las cuatro estrategias cumplen el mismo contrato: devuelven un `Optional`, nunca `null`, y lo devuelven vacío si no hay drones. Dos pruebas parametrizadas lo verifican, una para el caso con drone y otra para la flota vacía. |
| **ISP** | Quien solo quería asignar dependía también de BD, email y PDF. | Las interfaces tienen un solo método (`@FunctionalInterface`). `EstrategiaAsignacion` recibe solo el `Paquete`, no la misión completa. |
| **DIP** | La lógica de negocio dependía de `DriverManager`/MySQL, de SMTP y de iText. | `GestorMisiones` depende de la abstracción `EstrategiaAsignacion` y `GestorFlota` de `ObservadorDrone`; ambas se inyectan. `SistemaLog` recibe un `Clock` en lugar de llamar a `LocalDateTime.now()`. La inyección de la conexión a BD y de sus credenciales quedó resuelta en Chimchar 04. |

## Patrones que implementan cada principio

| Patrón | Dónde | Principio | Evidencia |
|---|---|---|---|
| Strategy (`EstrategiaAsignacion`) | v2 | OCP | Tres estrategias más una lambda en las pruebas, sin cambios en `GestorMisiones` |
| Observer (`ObservadorDrone`) | v2 | DIP | `GestorFlota` solo conoce la interfaz; el 4.º observador es un mock |
| Servicios pequeños | v2 | SRP | Una clase por responsabilidad (tabla de arriba) |
| Chain of Responsibility (validadores) | **Chimchar 03**, no la v2 | OCP + SRP | Un validador nuevo es una clase nueva, con una sola regla cada uno |

## Las pruebas pedidas: `GestorMisiones` con cualquier estrategia

[`GestorMisionesTest`](../skycampus-v2/src/test/java/edu/eci/skycampus/asignacion/GestorMisionesTest.java) tiene 6 métodos de prueba. Tres de ellos demuestran OCP, LSP y DIP; los otros tres cubren la misión creada y las entradas `null`.

La flota de prueba está diseñada para que **cada criterio elija un drone distinto, sin empates**, así que el resultado no depende del orden de la lista:

| Estrategia | Drone elegido |
|---|---|
| Mayor batería | D-02 (95 %) |
| Menor uso | D-03 (5 min) |
| Tipo compatible | D-01 (MINI con más batería) |
| `siempreElUltimo` (lambda escrita en la prueba) | D-04 |

1. **`crearMision_cualquierEstrategia_usaElDroneQueEllaElige`** (`@ParameterizedTest` + `@MethodSource`): corre con las 3 estrategias de producción y con la lambda `siempreElUltimo`, que no existe en el código de producción. En los cuatro casos, `GestorMisiones` crea la misión con el drone que eligió la estrategia, sin cambiar una línea de su código (OCP).
2. **`crearMision_flotaVacia_vacio`**, parametrizada con las mismas cuatro: todas devuelven `Optional.empty()` y no se crea misión (LSP).
3. **`crearMision_estrategiaSimulada_recibeFlotaYPaquete`**: usa `@Mock EstrategiaAsignacion` de Mockito con `verify(estrategia).seleccionar(flota, solicitud.paquete())`. Demuestra que `GestorMisiones` le pasa a la abstracción exactamente la flota y el paquete de la solicitud (DIP).
