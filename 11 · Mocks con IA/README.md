# 11 · Mocks con IA — Panel de monitoreo

## Entregables

| Archivo | Contenido |
|---|---|
| `Prompt_Mocks_Panel_Monitoreo.md` | Los 4 pasos del curso: referencias con las decisiones tomadas, estilo, datos del RF SC-01 y prompt completo con acciones y heurísticas objetivo |
| `Referencia_Skydio_Fleet_Manager.png`, `Referencia_Skydio_Reports_Summary.png` | Referencias reales de monitoreo de flota (Skydio Cloud: módulo Fleet y resumen de flota) |
| `Referencia_DroneSense_DSR.png`, `Referencia_DJI_FlightHub_Cockpit.webp` | Cabinas de un solo drone; solo se usan como referencia de la vista de detalle |
| `11_Mock_Normal.png` | Estado normal (datos reales) |
| `11_Mock_Alerta.png` | Estado de alerta, D-02 en fallo (simulado) |
| `11_Mock_Vacio.png` | Estado vacío, los 5 drones en misión (simulado) |
| `Mock_Estados_Panel.html` | Fuente de las imágenes; se abre con `?estado=normal`, `?estado=alerta` o `?estado=vacio` |

## Cómo se generaron (trazabilidad)

1. Se le entregó el prompt a una IA (Claude).
2. La IA lo implementó como HTML **sobre los componentes del mock aprobado en el reto 08**, para que la identidad sea idéntica. El archivo contiene la app completa, pero los tres estados solo modifican la vista Monitoreo.
3. Las imágenes son capturas de esa pantalla a 1440 × 1024 px.
4. Cada imagen se revisó texto por texto contra los datos base (IDs, %, ubicaciones y el mínimo de 30 %).

Iteraciones:
- **v1:** primera generación.
- **v2:** se cambiaron las referencias por paneles de flota reales; se agregaron las acciones de la plantilla (selector, "Ver detalle" y "Asignar a M-0001"); el escenario Vacío se hizo coherente con la regla del 30 %; se aplicó la misma regla de elegibilidad a D-02 en los tres estados; y se quitaron el mapa cortado y el enlace redundante "Ver tabla completa".
- **v3:** más contraste en el selector deshabilitado. En Vacío, el detalle de cada drone sigue disponible al hacer clic en su fila (en la app cada fila abre su detalle); el selector y la barra de acción solo sirven para asignar.

**Simulación:** en Alerta, D-02 aparece "Fallo"; en Vacío, los cinco drones aparecen "En vuelo" con baterías de vuelo simuladas. Los datos reales del MVP son los del estado Normal. El manual del 08 documenta esta excepción en su regla de consistencia.

## Heurísticas de Nielsen

| # | Heurística | Normal | Alerta | Vacío |
|---|---|---|---|---|
| 1 | Visibilidad del estado | Cada fila muestra batería, estado, elegibilidad y ubicación; el resumen da 03 asignables | El fallo se ve en la fila (borde y etiqueta roja), en el resumen ("En fallo 01") y en el primer aviso | El resumen da 00 asignables y 05 en vuelo; un aviso confirma que todas las baterías en vuelo están ≥ 30 % |
| 5 | Prevención de errores | El selector de D-04 y el de D-02 están deshabilitados; "Asignar" solo actúa sobre D-01 seleccionado; una nota explica el motivo | Igual, con D-02 bloqueado por el fallo | Ningún selector activo; "Asignar" deshabilitado como "Sin drones asignables" |
| 8 | Minimalismo | Solo lo necesario para asignar: lista, resumen y avisos | El aviso crítico va primero; el de batería queda aparte | El panel de avisos se reemplaza por un único mensaje con una acción |
| 9 | Mensajes de error claros | "Mínimo para asignar: 30%. Elige otro drone o espera a que se recargue." | "Drone no asignable. Revisa su estado antes de planificar misiones." | "No hay drones disponibles para asignar… Actualiza la flota cuando alguno aterrice." |
| 4 | Consistencia | Misma retícula, colores y etiquetas que el mock del 08; misma regla de elegibilidad en los 3 estados | Igual | Igual |
