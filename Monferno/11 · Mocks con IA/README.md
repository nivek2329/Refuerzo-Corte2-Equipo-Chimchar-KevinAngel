# 11 · Mock del flujo de asignación automática — Monferno

Prototipo navegable: [`Flujo_Asignacion_Automatica_Monferno.html`](Flujo_Asignacion_Automatica_Monferno.html). Integra los tres pasos de la v2 y permite activar los estados de error y cancelación para revisar los mensajes y las salidas. Los datos de la misión (M-1042, Bloque A → Bloque D, 640 g) son los mismos en el HTML y en el prompt.

Prompt para recrear o revisar visualmente el flujo con otra IA: [`Prompt_IA_Flujo_Monferno.md`](Prompt_IA_Flujo_Monferno.md). La identidad que debe conservar está en el [manual de Monferno](../08%20%C2%B7%20Manual%20de%20Identidad%20y%20UXUI/Manual_Identidad_SkyCampus_Monferno.html). El HTML de esta carpeta es el prototipo navegable y el prompt es el insumo para su generación/revisión; no se conserva una captura ni un registro verificable de qué herramienta generó el HTML.

## Vistas y estados

1. **Panel de flota:** muestra 6 de los 20 drones y ofrece un filtro funcional por estado; el operador inicia una misión y selecciona el candidato apto.
2. **Detalle de misión:** solicitud urgente de 640 g, destino, clima, estrategia y capacidad del candidato visibles antes de confirmar.
3. **Confirmación:** drone asignado, ruta y notificaciones emitidas; acción grande para cancelar.

Estados alternos accesibles desde los botones de demostración del prototipo:

- **Sin drones aptos:** explica que no existe un candidato con disponibilidad, batería mínima y capacidad; permite programar para más tarde.
- **Clima adverso:** bloquea la asignación y explica la condición; no ofrece un bypass que permita despegar.
- **Paquete demasiado pesado:** indica el peso recibido y el máximo de la flota (2.000 g); ofrece solicitar una carga menor.

## Heurísticas de Nielsen verificadas

| # | Heurística | Aplicación visible |
|---|---|---|
| 1 | Visibilidad del estado del sistema | Stepper, estado de flota, batería, clima y confirmación de envío |
| 2 | Correspondencia entre sistema y mundo real | Bloques del campus, gramos, minutos, misión, técnico y términos de operación |
| 3 | Control y libertad | Volver a Flota, revisar antes de confirmar, cancelar y programar más tarde |
| 4 | Consistencia y estándares | Mismos nombres, colores y etiquetas para cada estado en las tres pantallas |
| 5 | Prevención de errores | Candidato solo si está disponible, tiene ≥30 % y soporta el peso; clima adverso bloquea |
| 6 | Reconocer antes que recordar | Reglas de aptitud, datos de misión y motivo de bloqueo aparecen en pantalla |
| 7 | Flexibilidad y eficiencia | Filtros por tipo/estado y selección directa del drone recomendado |
| 8 | Diseño estético y minimalista | Una decisión principal por pantalla; detalle secundario queda agrupado |
| 9 | Ayudar a reconocer, diagnosticar y recuperarse de errores | Cada error explica la causa y da una salida segura |

Los casos son datos de demostración y la interfaz no persiste estado al recargar. No se conecta con el backend de SkyCampus.
