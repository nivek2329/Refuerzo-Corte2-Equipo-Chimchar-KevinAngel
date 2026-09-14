# 03 · Patrones de Diseño — SkyCampus MVP

## Problema 1 — Builder
`Mision` tiene campos obligatorios (drone, origen, destino) y opcionales (prioridad, notas, hora máxima de entrega); un constructor por cada combinación sería telescópico e inmanejable, y Builder arma el objeto paso a paso validando solo lo obligatorio en `build()`.

## Problema 2 — Chain of Responsibility
Hay una secuencia de validaciones independientes donde cada una decide si la misión pasa o se rechaza (batería, destino, carga); Chain permite agregar, quitar o reordenar validadores sin tocar los demás.

## Problema 3 — Strategy
El criterio para asignar el drone óptimo puede cambiar en el futuro (mayor batería, más cercano, más rápido); Strategy permite intercambiar el algoritmo de asignación sin tocar el resto del sistema.