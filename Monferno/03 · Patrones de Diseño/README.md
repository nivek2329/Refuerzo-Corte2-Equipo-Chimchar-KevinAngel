# 03 · Patrones de Diseño — Monferno (SkyCampus v2)

El código está en [`skycampus-v2`](../skycampus-v2), en los paquetes `asignacion` (Strategy) y `notificacion` (Observer).

## 1. Strategy — asignación de drones intercambiable

```java
@FunctionalInterface
public interface EstrategiaAsignacion {
    Optional<Drone> seleccionar(List<Drone> flota, Paquete paquete);
}
```

| Implementación | Criterio |
|---|---|
| [`AsignacionMayorBateria`](../skycampus-v2/src/main/java/edu/eci/skycampus/asignacion/AsignacionMayorBateria.java) | De los aptos, el de mayor batería |
| [`AsignacionMenorUso`](../skycampus-v2/src/main/java/edu/eci/skycampus/asignacion/AsignacionMenorUso.java) | De los aptos, el de menos minutos de vuelo acumulados |
| [`AsignacionTipoCompatible`](../skycampus-v2/src/main/java/edu/eci/skycampus/asignacion/AsignacionTipoCompatible.java) | De los aptos, el de **menor capacidad que aún soporta el paquete**, para no gastar un CARGO en un sobre. En empate, el de mayor batería |

**Qué significa "apto"** ([`CriterioAptitud`](../skycampus-v2/src/main/java/edu/eci/skycampus/asignacion/CriterioAptitud.java)): un drone es apto si cumple las tres condiciones siguientes.

- Está en estado `DISPONIBLE`.
- Tiene batería ≥ 30%.
- Su `TipoDrone` admite el peso del paquete:
  - MINI: 1–500 g;
  - EXPRESS: 1–800 g;
  - CARGO: 100–2000 g.

Las tres estrategias reutilizan este filtro, así que cada una solo define su criterio de orden. No se repite el filtro (DRY).

[`GestorMisiones`](../skycampus-v2/src/main/java/edu/eci/skycampus/asignacion/GestorMisiones.java) recibe la estrategia por constructor y crea la misión con el drone que ella elija. Si no hay drone, devuelve `Optional.empty()`.

**Diferencia con el enunciado:** el enunciado pasa una `Mision` a `seleccionar`. Aquí se pasa el `Paquete`, porque la misión todavía no existe: se crea *después* de elegir el drone, y una `Mision` sin drone obligaría a admitir `null`. Además, las estrategias solo leen el peso del paquete, así que recibir el `Paquete` en lugar de la `Mision` completa cumple ISP. Está planeado que la prioridad se use más arriba, en el reto 12, para elegir *qué* estrategia aplicar.

## 2. Observer — avisos automáticos de cambio de estado

```java
@FunctionalInterface
public interface ObservadorDrone {
    void onEstadoCambiado(Drone drone, EstadoDrone nuevo);
}
```

[`GestorFlota`](../skycampus-v2/src/main/java/edu/eci/skycampus/notificacion/GestorFlota.java) gestiona los observadores:

- `suscribir` rechaza `null` e ignora duplicados.
- `desuscribir` retira al observador.
- `cambiarEstado(drone, nuevo)` maneja tres casos:
  - **Si el estado no cambia** (DISPONIBLE → DISPONIBLE), devuelve el mismo drone y **no notifica**. El enunciado pide avisar cuando el drone *cambia* de estado.
  - **Si la transición no está permitida**, la rechaza el dominio y no se notifica a nadie. `EstadoDrone.puedePasarA` define el ciclo (DISPONIBLE → EN_VUELO → ATERRIZANDO → DISPONIBLE; FALLO → MANTENIMIENTO → DISPONIBLE), y `Drone.transicionarA` lanza `IllegalStateException("transición no permitida para D-07: FALLO → EN_VUELO")`. Así, un drone en FALLO no vuelve a volar sin pasar por el técnico.
  - **Si el cambio es válido**, devuelve el drone actualizado (el record es inmutable) y notifica a una copia (`List.copyOf`) de los observadores. Así, si un observador se desuscribe mientras se notifica, no hay `ConcurrentModificationException`. `GestorFlota` no guarda la flota: quien lo llama usa el drone devuelto.
- `suscribir`, `desuscribir` y `cambiarEstado` rechazan `null` con mensajes en español.

| Suscriptor | Reacciona a | Produce |
|---|---|---|
| [`PanelOperador`](../skycampus-v2/src/main/java/edu/eci/skycampus/notificacion/PanelOperador.java) | Todos los cambios (historial en orden de llegada) | `"D-07 · EN_VUELO"` |
| [`SistemaLog`](../skycampus-v2/src/main/java/edu/eci/skycampus/notificacion/SistemaLog.java) | Todos los cambios (con `Clock` inyectado) | `"2026-10-08T10:00 \| D-07 \| EN_VUELO"` |
| [`AlertaTecnico`](../skycampus-v2/src/main/java/edu/eci/skycampus/notificacion/AlertaTecnico.java) | Solo `FALLO` | `"Revisar D-07 (EXPRESS): entró en FALLO"` |

La clase `Drone` no sabe nada de observadores. Su único comportamiento propio es validar sus transiciones (`transicionarA`).

## 3. Prueba: un 4.º observador sin tocar `GestorFlota`

En [`GestorFlotaTest.suscribir_cuartoObservador_recibeNotificacionSinCambiarGestorFlota`](../skycampus-v2/src/test/java/edu/eci/skycampus/notificacion/GestorFlotaTest.java), el 4.º observador es un **mock de Mockito** (`@Mock ObservadorDrone`). Se suscribe con la misma API pública y se verifica con `verify(cuartoObservador).onEstadoCambiado(actualizado, EN_VUELO)`. Se verifica contra `actualizado`, el drone que devuelve `cambiarEstado`, porque `Drone` es inmutable: el observador recibe la copia nueva y no el `drone` original. `GestorFlota` no se modificó para admitirlo: solo conoce la interfaz.

Otras pruebas del patrón:

- **Sujeto** ([`GestorFlotaTest`](../skycampus-v2/src/test/java/edu/eci/skycampus/notificacion/GestorFlotaTest.java)):
  - sin cambio real no hay aviso (`never()`);
  - una transición inválida lanza la excepción sin avisar a nadie;
  - `desuscribir` corta los avisos;
  - una doble suscripción produce un solo aviso (`times(1)`);
  - las entradas `null` dan el mensaje correcto.
- **Suscriptores** ([`ObservadoresTest`](../skycampus-v2/src/test/java/edu/eci/skycampus/notificacion/ObservadoresTest.java)):
  - un despegue llega al panel y al log, pero no al técnico;
  - un FALLO llega a los tres a la vez;
  - `avisos()`, `registros()` y `ordenes()` son copias inmodificables, con una prueba para cada una;
  - `SistemaLog` exige un reloj.
- **Estrategias** ([`EstrategiasAsignacionTest`](../skycampus-v2/src/test/java/edu/eci/skycampus/asignacion/EstrategiasAsignacionTest.java)):
  - cada estrategia elige un drone distinto sobre la **misma flota**, lo que demuestra que el algoritmo cambia sin cambiar los datos;
  - con un paquete demasiado pesado o con la flota vacía, las pruebas parametrizadas verifican que ninguna estrategia elige.
