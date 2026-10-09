# Requisitos del SkyCampus MVP — Chimchar

## Alcance usado

El MVP gestiona cinco drones, destinos fijos (Bloque A, Bloque B, Bloque C, Bloque D y Biblioteca), solicitudes de reparto de documentos y asignación manual por un operador. Los tipos de carga de este reto siguen el HTML: `SOBRE`, `CARPETA` y `LIBRO`.

## Requisitos funcionales

| ID | Actor | Requisito (acción y resultado observable) | MoSCoW | Justificación |
|---|---|---|---|---|
| RF-01 | Solicitante | El sistema debe permitir registrar una solicitud indicando origen, destino y tipo de documento; al aceptarla, debe dejarla disponible para que el operador la convierta en misión. | Must Have | Sin registrar solicitudes no existe el flujo de reparto del MVP. |
| RF-02 | Operador de drones | El sistema debe mostrar los drones disponibles con su ID, batería actual y ubicación para que el operador pueda elegir uno. | Must Have | El operador necesita consultar la flota antes de hacer la asignación manual. |
| RF-03 | Operador de drones | El sistema debe permitir convertir manualmente una solicitud pendiente en misión al asignarle un drone disponible; debe generar y mostrar un código único de misión y el ID del drone asignado, con estado inicial `PENDIENTE`. | Must Have | La creación de la misión y la asignación manual del drone conforman el paso central de operación en Chimchar. |

## Requisitos no funcionales

| ID | Calidad | Requisito medible | Verificación | MoSCoW | Justificación |
|---|---|---|---|---|---|
| RNF-01 | Rendimiento | Después de un cambio de estado de un drone, el panel de flota debe reflejar el cambio en menos de 3 segundos. | Medir el intervalo entre registrar el cambio y verlo en el panel, para los cinco drones. | Should Have | Una actualización rápida ayuda a asignar con información reciente; un retraso breve no elimina la posibilidad de operar manualmente. |
| RNF-02 | Integridad de datos | En 100 de 100 solicitudes válidas de prueba, cada misión debe recibir un código único y conservar ese código y su estado al volver a consultarla. | Registrar 100 solicitudes válidas y comprobar unicidad y consistencia en la consulta posterior. | Must Have | El operador y el solicitante necesitan identificar la misma misión sin ambigüedad. |
| RNF-03 | Usabilidad | En una prueba con cinco drones, al menos 9 de cada 10 operadores deben completar una asignación manual en 60 segundos o menos, sin ayuda del facilitador. | Cronometrar diez intentos de operadores que recibieron una inducción breve y contar los que cumplen el criterio. | Could Have | Es una meta útil para mejorar la experiencia del operador; el MVP puede pilotearse aunque todavía tome más tiempo. |

## Fuera del alcance Chimchar

La asignación automática y la optimización de rutas quedan como **Won't Have** en este MVP; corresponden a niveles posteriores según el HTML. Exportar reportes y notificar por correo son ejemplos de **Could Have**, pero no forman parte de los seis requisitos evaluados aquí.

## Nota de consistencia

Este documento usa los tres tipos de carga definidos en el enunciado Chimchar (`SOBRE`, `CARPETA`, `LIBRO`). El prompt general del revisor menciona también `EQUIPO`; esa variante queda pendiente de reconciliación antes de integrar los módulos.
