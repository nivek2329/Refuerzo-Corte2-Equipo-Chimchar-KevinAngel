# 01 · Streams & Lambdas — Monferno (SkyCampus v2)

El código vive en el proyecto Maven compartido de la v2: [`skycampus-v2`](../skycampus-v2).

- Implementación: [`EstadisticasMisiones.java`](../skycampus-v2/src/main/java/edu/eci/skycampus/estadisticas/EstadisticasMisiones.java)
- Pruebas: [`EstadisticasMisionesTest.java`](../skycampus-v2/src/test/java/edu/eci/skycampus/estadisticas/EstadisticasMisionesTest.java), con casos normales, de borde y de entrada `null`.

## Las cuatro consultas

Cada consulta recibe la `List<Mision>` del día y recorre la colección solo con Streams, sin `for`. El único `if` es la guarda de lista vacía en `porcentajeFallidas`, que evita dividir por cero.

| # | Pregunta | Método | Operaciones clave |
|---|---|---|---|
| 1 | Tipo de drone → misiones completadas hoy | `completadasPorTipo` | `filter(ENTREGADA)` + `groupingBy(tipo, counting())` |
| 2 | Drone con más misiones completadas | `droneConMasCompletadas` → `Optional<String>` | `groupingBy(id, counting())` + `max(comparingByValue)` |
| 3 | % de misiones fallidas sobre el total | `porcentajeFallidas` | `partitioningBy(FALLIDA, counting())` |
| 4 | ¿Alguna URGENTE en PENDIENTE hace más de 10 min? | `hasUrgentePendienteDemorada(misiones, ahora)` | `filter` + `anyMatch`, que corta en cuanto encuentra una |

## Decisiones

- **Supuesto: la lista recibida ya es la del día.** La filtra quien llama a estos métodos, que no filtran por fecha. Está documentado en el Javadoc de la clase.
- **Una misión "completada" es `EstadoMision.ENTREGADA`.** Los estados de misión son PENDIENTE, EN_VUELO, ENTREGADA y FALLIDA.
- **Empate en la consulta 2:** si dos drones tienen el mismo número de entregas, gana el ID menor (`thenComparing(comparingByKey(reverseOrder()))`). Así el resultado no depende del orden del `HashMap`. Lo prueba `droneConMasCompletadas_empate_ganaIdMenor`.
- **Sin entregas, la consulta 2 devuelve `Optional.empty()`**, nunca `null`.
- **Lista vacía en la consulta 3:** devuelve `0.0` en lugar de dividir por cero.
- **"Más de 10 minutos" es estricto:** a los 10 minutos exactos da `false` y a los 11 da `true`. Hay una prueba para cada lado del límite, y otra que comprueba que una misión con `creadaEn` posterior a `ahora` no cuenta como demorada.
- **El reloj se recibe como parámetro** (`LocalDateTime ahora`). Por eso la consulta 4 se prueba sin depender de la hora real.
- **Entradas null:** los cuatro métodos lanzan `NullPointerException` con un mensaje en español ("misiones no puede ser null" o "ahora no puede ser null"). Una prueba parametrizada cubre cada método.

## Diferencia con el modelo del enunciado

El enunciado sugiere `record Drone(..., boolean disponible, EstadoDrone estado)`. En la v2, `disponible` **no es un campo**: se deriva del estado con `Drone.isDisponible()`, que es verdadero solo en `DISPONIBLE`. Con dos campos podría existir un drone "disponible" en estado `FALLO`; derivarlo deja el estado como única fuente de verdad.

`Mision` guarda el `Paquete` dentro de `SolicitudReparto` y expone `pesoPaqueteGramos()` y `prioridad()`, así que las consultas del enunciado funcionan igual.

## Cómo ejecutar

```powershell
cd Monferno\skycampus-v2
mvn clean verify
```
