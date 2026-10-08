# 03 · Patrones de Diseño — SkyCampus MVP

## Problema 1 — Builder
`Mision` tiene campos obligatorios (drone, origen, destino) y opcionales (prioridad, notas, hora máxima de entrega); un constructor por cada combinación sería telescópico e inmanejable, y Builder arma el objeto paso a paso validando lo obligatorio en `build()`.

- `build()` exige drone, origen, destino y **tipo de carga** (no se asume un tipo por defecto para no validar una carga falsa) y lista todos los campos faltantes en un solo mensaje.
- `Mision` es una clase inmutable con constructor de paquete: solo se crea desde el builder, y aun así el constructor protege sus invariantes (textos no vacíos, prioridad entre 1 y 5).
- Toda misión nace en `PENDIENTE`; los cambios de estado pertenecen a otros flujos del sistema.
- La hora máxima de entrega se guarda como `LocalTime` y se expone como `Optional<LocalTime>`: nunca retorna `null`.
- Prioridad por defecto: 3 (`PRIORIDAD_POR_DEFECTO`).

## Problema 2 — Chain of Responsibility
Hay una secuencia de validaciones independientes donde cada una decide si la misión pasa o se rechaza (batería, destino, carga); Chain permite agregar, quitar o reordenar validadores sin tocar los demás.

- `CadenaValidacionMision.crear()` es el único punto donde se arma la cadena, en el orden del enunciado: `ValidadorBateria` → `ValidadorDestino` → `ValidadorCarga`.
- La cadena retorna el primer motivo de rechazo o `Optional.empty()` si todos pasan (caso en que ningún eslabón rechaza).
- `siguiente(...)` rechaza enlazar un validador consigo mismo y reemplazar un enlace existente.
- `ValidadorDestino` compara el texto exacto del catálogo (`"Bloque A"` … `"Biblioteca"`); `"biblioteca"` o con espacios se rechaza a propósito.

## Problema 3 — Strategy
El criterio para asignar el drone óptimo puede cambiar (mayor batería, más cercano, más rápido); Strategy permite intercambiar el algoritmo sin tocar el resto del sistema.
`AsignadorDeMision` depende de la interfaz `EstrategiaAsignacion`; `AsignacionPorMayorBateria` implementa el criterio del MVP y lanza `IllegalStateException` si no hay drones disponibles (no retorna `null`).

## Desviaciones conscientes del modelo Chimchar
- **`Drone.capacidadMaxima` (`CapacidadCarga`)**: el modelo Chimchar no la tiene, pero el Problema 2 pide que `ValidadorCarga` rechace la carga que *supera la capacidad del drone*. Se modela como dato del drone para que la regla sea verificable; en el MVP los 5 drones pueden tener la misma capacidad.
- **`TipoCarga.capacidadRequerida`**: cada tipo de carga indica la capacidad mínima que necesita.
- **`EQUIPO`**: el prompt general del revisor lo menciona, pero el enunciado Chimchar define solo `SOBRE`, `CARPETA` y `LIBRO`; no se agrega.
