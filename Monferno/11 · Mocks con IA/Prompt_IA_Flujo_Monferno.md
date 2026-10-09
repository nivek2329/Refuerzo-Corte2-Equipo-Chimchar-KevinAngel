# Prompt — mock de asignación automática Monferno

Diseña e implementa un prototipo web navegable, en español, para **SkyCampus v2 / Monferno**, un sistema de operación de veinte drones para reparto dentro del campus. Sigue la identidad y tokens de `Monferno/08 · Manual de Identidad y UXUI/Manual_Identidad_SkyCampus_Monferno.html`. Mantén la consola sobria, técnica y humana; evita paneles genéricos de plantilla, degradados decorativos, texto de relleno y métricas sin relación con la operación.

## Flujo que debe representar

1. **Flota:** mostrar agrupación o filtros por estado y tipo; usar datos con ID, tipo, batería y capacidad. Estados: `DISPONIBLE`, `EN_VUELO`, `EN_CARGA`, `FALLO` y `MANTENIMIENTO`. La tarjeta recomendada explica por qué es apta. La regla de negocio es batería mínima del 30 % y capacidad suficiente para la carga.
2. **Detalle de misión:** solicitud `MIS-1042`, urgente, paquete de 640 g, origen Edificio de Ingeniería y destino Biblioteca. Mostrar estrategia de asignación, clima y candidato, con datos visibles antes de confirmar.
3. **Confirmación:** presentar drone asignado, destino/ruta y notificación al panel y al log. Mantener una acción explícita para volver y una acción de cancelación separada.

## Estados alternos

- Sin candidatos aptos: explicar si falló disponibilidad, batería o capacidad y ofrecer programar más tarde.
- Clima adverso: bloquear la autorización con causa concreta; no mostrar un botón que permita eludir el bloqueo.
- Peso superior a toda capacidad: indicar peso recibido y límite de la flota, con una acción segura para ajustar la solicitud.

## Identidad visual y accesibilidad

- Fondo `#0B1422`, superficies `#14243A`, borde `#29415D`, texto `#EDF4FB`, texto secundario `#A8BBCF` y azul de acción `#58A6FF`.
- Colores de estado: disponible `#46D39A`, en vuelo `#55C7E8`, en carga `#F5C451`, fallo `#FF7079`, mantenimiento `#9AABC0`. Siempre acompañar color con icono y etiqueta legible.
- Inter/Segoe UI para interfaz; Consolas para IDs, porcentajes y cantidades. Controles con foco visible, áreas cómodas y contraste suficiente.
- Aplicar Fitts a acciones principales y de emergencia; aplicar Hick agrupando/filtrando la flota antes de seleccionar; aplicar heurísticas de Nielsen para estado visible, prevención y recuperación de errores.

Entrega el HTML completo y ejecutable localmente, responsive, sin dependencias externas ni conexión real a servicios. No afirmes que guarda datos. Revisa que todos los botones de navegación, confirmación, retorno, cancelación y estados de error funcionen y que el texto no contradiga los datos del dominio Monferno.
