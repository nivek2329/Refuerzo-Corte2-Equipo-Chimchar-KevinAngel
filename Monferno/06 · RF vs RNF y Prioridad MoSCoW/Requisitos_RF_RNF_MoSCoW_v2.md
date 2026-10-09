# Requisitos de SkyCampus v2 — Monferno

## Alcance usado

La v2 gestiona **20 drones de 3 tipos**:

| Tipo | Capacidad | Rasgo |
|---|---|---|
| MINI | hasta 500 g | ágil |
| CARGO | 100 a 2000 g | lento |
| EXPRESS | hasta 800 g | rápido, batería limitada |

Los paquetes tienen peso en gramos, tipo (`SOBRE`, `CARPETA`, `LIBRO`, `EQUIPO`) y prioridad (`URGENTE`, `NORMAL`, `BAJO`). La asignación es **automática**. Hay un actor nuevo, el **Técnico de mantenimiento**, y tres sistemas externos (ver reto 05): API Meteorológica, Control Aéreo ECI y Sistema de Alertas.

La numeración continúa la del MVP: Chimchar definió RF-01 a RF-03 y RNF-01 a RNF-03.

## Requisitos funcionales

| ID | Actor | Requisito (acción y resultado observable) | MoSCoW | Justificación |
|---|---|---|---|---|
| RF-04 | Sistema (para el Operador) | Antes de asignar cualquier misión, el sistema debe consultar la API Meteorológica. Si responde "no apto", no debe asignar drone y debe dejar la solicitud en `PENDIENTE` con el motivo "clima adverso" visible para el operador. | Must Have | Volar con viento fuerte o lluvia pone en riesgo el drone y la carga. Es una condición de seguridad, no una mejora. |
| RF-05 | Técnico de mantenimiento | Cuando un drone pase a estado `FALLO`, el sistema debe enviar al Sistema de Alertas una orden con el ID del drone, su tipo y la hora del fallo. El técnico debe recibir la notificación sin que el operador intervenga. | Should Have | Reduce el tiempo que un drone queda fuera de servicio. Sin la alerta automática, el operador todavía puede avisar al técnico manualmente, así que la v2 funciona aunque se retrase. |
| RF-06 | Técnico de mantenimiento | El técnico debe poder pasar un drone de `FALLO` a `MANTENIMIENTO` y de `MANTENIMIENTO` a `DISPONIBLE`. El sistema debe rechazar, con un mensaje en español, cualquier otra transición desde `FALLO`; por ejemplo, `FALLO → EN_VUELO`. | Should Have | Evita que un drone averiado vuelva a volar sin revisión. La v2 puede salir con este ciclo gestionado por el admin, pero el rol del técnico es el que da sentido al nuevo actor. |
| RF-07 | Sistema (para el Solicitante) | El sistema debe asignar automáticamente a cada misión `NORMAL` o `BAJO` el drone apto con **mayor batería**, y ponerlo `EN_VUELO`. Las misiones `URGENTE` se rigen por RF-08 (ver la tensión). Si ningún drone es apto, no debe asignar y debe dejar la solicitud en `PENDIENTE` con el motivo "sin drones aptos" visible para el operador. Un drone es apto si cumple las tres condiciones: está `DISPONIBLE`, tiene al menos 30 % de batería y su tipo admite el peso del paquete. | Must Have | Es la funcionalidad central de la v2: con 20 drones, la asignación manual del MVP ya no escala. |
| RF-08 | Sistema (para el Solicitante) | Las misiones `URGENTE` deben recibir el drone apto **más rápido** (EXPRESS antes que MINI, y MINI antes que CARGO), aunque otro drone apto tenga más batería. | Must Have | Una entrega urgente que llega tarde no cumple su propósito. La prioridad es parte del modelo de datos de la v2. |
| RF-09 | Técnico de mantenimiento | Cuando el drone asignado tenga **entre 30 % y 40 %** de batería, el sistema debe asignarlo igualmente y avisar al técnico (en su panel) para que programe la carga al terminar la misión. | Could Have | Es un aviso preventivo: evita que el drone quede por debajo del mínimo en la siguiente asignación, pero la v2 opera sin él, porque el filtro del 30 % ya impide volar con batería insuficiente. |

## Requisitos no funcionales

| ID | Calidad | Requisito medible | Verificación | MoSCoW | Justificación |
|---|---|---|---|---|---|
| RNF-04 | Rendimiento | La asignación automática debe seleccionar el drone en **menos de 500 ms** con una flota de hasta **50 drones**. | Prueba JUnit 5 con `assertTimeout(Duration.ofMillis(500), ...)` sobre una flota generada de 50 drones, con el clima simulado. | Should Have | El operador percibe la asignación como inmediata. 50 drones deja margen sobre los 20 actuales. |
| RNF-05 | Tolerancia a fallos (falla segura) | Si la API Meteorológica no responde en **2 segundos** o devuelve un error, el sistema **no debe despegar ningún drone**, debe dejar la solicitud en `PENDIENTE` y avisar al operador en ese mismo intento. | Una prueba por caso, con `@Mock ApiMeteorologica`: (1) la API tarda más de 2 s; (2) la API lanza una excepción; (3) la API responde "no apto". En los tres casos se verifica que no hay asignación, que no se notifica `EN_VUELO` y que el operador recibe el aviso. Cada caso debe pasar. | Must Have | La v2 depende por primera vez de un sistema externo. Ante la duda sobre el clima, lo seguro es no volar. |
| RNF-06 | Oportunidad de las alertas | El técnico debe recibir la notificación de un drone en `FALLO` en **menos de 30 segundos** desde el cambio de estado, en al menos **95 de 100** eventos simulados. | Prueba de integración con el Sistema de Alertas en un entorno de pruebas: se mide el intervalo entre `cambiarEstado(FALLO)` y la recepción. | Should Have | Un drone averiado en el campus puede ser un riesgo físico. 30 s es suficiente para reaccionar y deja tolerancia al proveedor de alertas. |
| RNF-07 | Mantenibilidad (extensibilidad) | Agregar una estrategia de asignación nueva, o cambiar la estrategia de una prioridad, **no debe requerir modificar** `AsignadorMision`: debe bastar con crear una clase o cambiar la configuración. | Prueba de OCP `asignar_bajoConEstrategiaPropia_usaLaConfigurada`: inyecta otra estrategia para BAJO sin tocar la clase (reto 12). | Must Have | Las reglas de asignación van a cambiar (por ejemplo, "el más cercano"). Si cada cambio obliga a tocar el núcleo, el riesgo de romper la asignación crece con cada versión. |
| RNF-08 | Calidad verificable | El código debe mantener **≥ 80 % de cobertura de líneas y ≥ 70 % de ramas** (si no, el build falla), con **0 bugs y 0 vulnerabilidades** en SonarQube. | La regla `check` de JaCoCo en el `pom.xml` rompe el build, con evidencia en rojo en el reto 13 (`evidencia/jacoco_check_rojo.txt`); el análisis de SonarQube está en el reto 14. | Must Have | Es parte del Definition of Done del equipo (reto 09). La parte de cobertura es una **puerta automática** (rompe el build). La parte de SonarQube hoy es una **revisión en cada entrega**: "Sonar way" solo evalúa el código nuevo, así que un bug en código existente no rompería nada. Hacerla automática requiere un Quality Gate propio sobre el código total (reto 14). |

## Tensión entre RF-07 y RF-08

**Dónde está la tensión.** El RF-07 **original del enunciado** decía "el drone de **mayor batería** para **cualquier** misión" (la tabla de arriba ya muestra el RF-07 resuelto). RF-08 dice "el drone **más rápido** para las misiones URGENTE, **independientemente de la batería**". No se contradicen: ambos pueden cumplirse. Pero leídos literalmente se pisan:
1. Para una misión URGENTE, RF-07 elegiría el MINI con 91 % y RF-08 el EXPRESS con 60 %.
2. "Independientemente de la batería" podría leerse como "aunque el drone tenga menos del 30 %", y eso rompería la regla de seguridad.

**Cómo se resuelve:**

| Regla | Decisión |
|---|---|
| Alcance de cada RF | RF-07 se reescribe "para misiones `NORMAL` y `BAJO`". RF-08 es la **excepción explícita** para `URGENTE`. |
| Mínimo de batería | El mínimo del 30 % es un **filtro previo común a todas las prioridades**. "Independientemente de la batería" significa "aunque otro drone tenga más", **nunca** "aunque esté bajo el mínimo". |
| Empate en RF-08 | Si hay varios drones del tipo más rápido, se elige el de **mayor batería**. Así RF-07 sigue valiendo como desempate dentro de RF-08. |
| Sin drones del tipo más rápido | Se usa el siguiente más rápido (MINI antes que CARGO), no el de más batería. |

**Cómo quedó en el código** (`Monferno/skycampus-v2`, reto 12):
- `CriterioAptitud` aplica el filtro común: `DISPONIBLE`, batería ≥ 30 % y tipo compatible.
- `PoliticaAsignacion.porDefecto()` asigna `URGENTE` → `AsignacionMasRapido` y `NORMAL`/`BAJO` → `AsignacionMayorBateria`.
- `AsignacionMasRapido` desempata por batería.
- Cada regla tiene una prueba que la fija:

| Regla | Prueba (en `skycampus-v2`) | Evidencia de que la prueba detecta el error |
|---|---|---|
| RF-08 gana a RF-07 en URGENTE | `asignar_misionUrgente_asignaExpress`: EXPRESS con 60 % antes que MINI con 91 % | Mutante 4 (URGENTE → mayor batería) muere: [`mutante_4_politica_urgente_mayor_bateria.txt`](../12%20%C2%B7%20TDD/evidencia/mutante_4_politica_urgente_mayor_bateria.txt) |
| El 30 % también vale para URGENTE | `asignar_urgenteConExpressBajoMinimo_asignaElSiguienteMasRapidoApto`: un EXPRESS con 25 % **no** vuela; sale el MINI | Prueba agregada al revisar este reto. Mutante "sin filtro del 30 %" en `CriterioAptitud`: esta prueba falla junto con otras 3. Ver [`evidencia/mutante_bateria_minima.txt`](evidencia/mutante_bateria_minima.txt) |
| Desempate por batería | `masRapido_variosExpressAptos_eligeMayorBateria` | Mutante 1 (desempate invertido) muere: [`mutante_1_desempate_invertido.txt`](../12%20%C2%B7%20TDD/evidencia/mutante_1_desempate_invertido.txt) |
| Siguiente más rápido | `asignar_urgenteSinExpress_asignaElSiguienteMasRapido` | Mutante 4 también la hace fallar |

## Trazabilidad con la v2 implementada

| Requisito | Estado en `skycampus-v2` |
|---|---|
| RF-04 | **Parcial.** `AsignadorMision` consulta `ApiMeteorologica.esApto()` antes de asignar y no despega si el clima no es apto. Falta el motivo "clima adverso" visible para el operador: hoy se devuelve `Optional.empty()`, igual que si no hubiera drones aptos. Tampoco existe el estado `PENDIENTE` de la solicitud: `SolicitudReparto` no guarda estado |
| RF-05 | **Parcial.** El observador `AlertaTecnico` genera la orden con el id y el tipo del drone. Faltan **la hora del fallo**, que el RF exige, y la integración con el Sistema de Alertas real |
| RF-06 | Las transiciones están implementadas en `EstadoDrone.puedePasarA` y `Drone.transicionarA`. Falta la interfaz del técnico |
| RF-07 y RF-08 | **Implementados y probados** en la elección del drone (ver la tensión). Faltan tres cosas: el motivo "sin drones aptos"; el estado `PENDIENTE` de la solicitud; y conectar la asignación con la creación de la misión. `GestorMisiones` genera el código `M-<id>`, pero `AsignadorMision` devuelve solo el drone |
| RF-09 | **No implementado.** `AlertaTecnico` solo reacciona al estado `FALLO`; no hay aviso por batería |
| RNF-04 | **Sin prueba aún.** Falta la prueba con `assertTimeout` y una flota de 50 drones |
| RNF-05 | **No implementado.** `AsignadorMision` llama a `clima.esApto()` de forma síncrona, sin límite de tiempo ni manejo de errores. Si la API se cuelga, `asignar` se bloquea; si lanza una excepción, esta sube al llamador sin avisar al operador. Falta: el límite de 2 s, la captura del fallo como "no apto" y el aviso al operador. Las pruebas de los tres casos vienen después |
| RNF-06 | Definido; requiere el Sistema de Alertas real |
| RNF-07 | **Se cumple.** La prueba de OCP pasa y la política por prioridad se inyecta (`PoliticaAsignacion`) |
| RNF-08 | **Se cumple.** JaCoCo: 212/212 líneas y 60/60 ramas (reto 13, `evidencia/jacoco.csv`); SonarQube: 0 bugs, 0 vulnerabilidades y Quality Gate *Passed* (reto 14) |

## Nota de consistencia

Los estados del drone del enunciado son `DISPONIBLE`, `EN_VUELO`, `EN_CARGA` y `FALLO`. La v2 los amplía con dos:
- **`ATERRIZANDO`**: el enunciado de Monferno describe el ciclo DISPONIBLE → EN_VUELO → ATERRIZANDO → DISPONIBLE, y el Observer debe notificar cada paso.
- **`MANTENIMIENTO`**: RF-06 necesita distinguir un drone que ya está en revisión de uno recién averiado, para que el técnico no reciba dos veces la misma orden y un drone en FALLO no vuelva a volar sin revisión.

Las transiciones válidas están en `EstadoDrone.puedePasarA`.

## Fuera del alcance Monferno

- **Won't Have en v2:** optimización de rutas multi-etapa, transferencia de drones entre sedes y autorización de la Aerocivil. Corresponden a Infernape.
- **Could Have:** priorizar por el drone "más cercano", que el diseño permite agregar como otra estrategia sin tocar `AsignadorMision` (RNF-07).
