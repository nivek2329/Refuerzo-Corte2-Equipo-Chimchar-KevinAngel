# 08 · Manual de Identidad y UX/UI — SkyCampus MVP

Orden de trabajo: **primero la identidad (manual), después el mock con IA a partir del manual y por último la verificación con Nielsen.**

## Archivos

| Archivo | Contenido |
|---|---|
| `Manual_Identidad_SkyCampus.html` | Manual de identidad: marca, usuarios, valores, color (marca vs. estados vs. elegibilidad), tipografía, componentes, modos, heurísticas guía y voz |
| `Prompt_IA_Mock_Final.md` | Prompt vigente: tokens del manual, datos exactos, vistas y heurísticas; reproduce el mock actual |
| `Prompt_Figma_Panel_Monitoreo.md`, `Prompt_Figma_Redeseno_Navegacion.md` | Exploraciones iniciales en Figma (historial del proceso; usan la paleta anterior) |
| `Mockup_SkyCampus.html` | Mock interactivo del panel de monitoreo de la flota (abrir en el navegador), generado con IA a partir de `Prompt_IA_Mock_Final.md` |
| `Captura_Mockup_*.png` | Capturas usadas como evidencia de la verificación |

## 1. Identidad (resumen del manual)

| Elemento | Definición | Justificación |
|---|---|---|
| Color primario | Azul acción `#2563EB`, navegación `#1D4ED8`, profundo `#1E3A8A` | Confianza y precisión técnica, como en una consola de control |
| Superficies (modo oscuro) | Fondo `#0F172A`, tarjetas `#111C2E`, bordes `#25324A`, texto `#F8FAFC`, secundario `#94A3B8` | Uso prolongado en el centro de operación con alto contraste |
| Acento de marca | Violeta `#8B5CF6`, solo en el logo, el avatar y el brillo de fondo | Nunca comunica estados |
| Estados del drone | Disponible `#22C55E` · En vuelo `#38BDF8` · En carga `#FACC15` · Fallo `#EF4444` · No disponible `#64748B` | Cada color, un solo significado, siempre con etiqueta |
| Elegibilidad | Asignable `#14B8A6` · No asignable por batería < 30 % `#F97316` | Separa el dato del modelo (`disponible`) de la regla de negocio (batería ≥ 30 %) |
| Misión | PENDIENTE `#C4B5FD` | No se confunde con alertas |
| Tipografía | Inter 400 (texto) / 600 (etiquetas y secciones) / 700 (solo el título de página y el logotipo); JetBrains Mono para todo ID (`D-04`, `M-0001`); mínimo 11 px | Sans-serif legible y monoespaciada para distinguir identificadores |
| Voz | Dato + requisito + acción. Las aclaraciones sobre qué es simulado van en este README, no en pantalla | El operador decide rápido |

**Tipos de carga:** el mock muestra `SOBRE`, `CARPETA` y `LIBRO` porque así los define el enunciado Chimchar. `EQUIPO` aparece en el prompt general del revisor, pero no pertenece al MVP.

## 2. Mock con IA

El mock muestra los 5 drones del enunciado con su estado actual:

| Drone | Batería | Estado (modelo) | Elegibilidad | Ubicación |
|---|---|---|---|---|
| D-01 | 85 % | Disponible | Asignable | Bloque A |
| D-03 | 91 % | Disponible | Asignable | Bloque C |
| D-05 | 67 % | Disponible | Asignable | Bloque D |
| D-04 | 18 % | Disponible | **No asignable · batería** | Bloque B |
| D-02 | 42 % | No disponible | No asignable | Biblioteca |

Vistas: Monitoreo (flota completa arriba, resumen, avisos y mapa de ubicaciones), Flota, Detalle de drone, Planificación, Historial y Configuración.

**Datos de demostración:** la misión `M-0001` (PENDIENTE, Bloque A → Biblioteca, CARPETA) es simulada y el prototipo no persiste cambios al recargar. Su estado es una sola fuente que leen Planificación, Detalle, Historial y el botón de asignar. El mapa es esquemático: muestra en qué bloque está registrado cada drone, no coordenadas reales.

Historial de ajustes del 08/10/2026:
- Paleta alineada con el manual; la primera versión usaba otra paleta clara.
- D-04 se marca como no asignable en la lista, la tabla, el mapa y el detalle.
- El botón de asignar se habilita solo para drones elegibles.
- El detalle depende del drone seleccionado.
- Se corrigieron los enlaces y el tono de los textos.
- Se añadió la confirmación de cancelación.
- Se unificaron la tipografía y los colores con un solo significado.
- El estado de M-0001 quedó como una sola fuente: cancelar o asignar actualiza todas las vistas, y el botón de asignar exige drone asignable y misión PENDIENTE sin drone.
- Se aplicó la regla de voz a todos los textos y M-0001 tiene origen, destino y tipo de carga válidos.

## Decisiones de alcance (observaciones del revisor)

- **Asignación en Flota y Monitoreo:** en este MVP la asignación de un drone a M-0001 se refleja en las vistas de misión (Planificación, Detalle e Historial) y en el botón de asignar. Marcar el drone como "comprometido" en la flota requiere un estado que el modelo Chimchar no tiene (`EstadoDrone` llega en Monferno), así que no se inventa.
- **Cancelar una misión con drone:** la misión cancelada conserva el drone como registro histórico y el drone no cambia en la flota, porque el modelo no guarda misiones activas por drone.
- **Disponible (#22C55E) y Asignable (#14B8A6):** son tonos cercanos para personas con daltonismo verde-rojo. Por eso el manual exige que toda etiqueta lleve texto, y en Asignable el texto es la señal principal. Lo que el operador debe notar sin leer es el caso de riesgo, "No asignable", y ese usa naranja #F97316, que contrasta con ambos.

## 3. Verificación de heurísticas de Nielsen (7 de 10)

| # | Heurística | Cómo la cumple el mock | Evidencia |
|---|---|---|---|
| 1 | Visibilidad del estado del sistema | Cada fila muestra ID, batería (barra y %), estado, elegibilidad y ubicación sin clics. El resumen da 05 registrados, 03 asignables, 01 no asignable y 01 no disponible. El indicador "Sistema local" tiene un punto vivo | `Captura_Mockup_Monitoreo.png` |
| 3 | Control y libertad del usuario | La misión PENDIENTE se puede cancelar tras confirmar en un diálogo con salida segura "Mantener misión" (foco por defecto). **Resultado esperado y comprobado:** al cancelar, M-0001 pasa a CANCELADA en Planificación, Detalle e Historial; el botón de cancelar se deshabilita y "Asignar" queda deshabilitado con "M-0001 está cancelada" incluso para D-01. Si se asigna un drone antes, todas las vistas muestran el drone y el botón indica "M-0001 ya tiene D-01". Hay breadcrumbs y "← Volver a la flota" | `Captura_Mockup_Confirmar_Cancelacion.png`, `Captura_Mockup_Mision_Cancelada_No_Asignable.png` |
| 4 | Consistencia y estándares | El mismo estado usa el mismo color, etiqueta y forma en todas las vistas. Los IDs siempre van en monoespaciada. Cada enlace lleva a lo que dice | Todas las capturas |
| 5 | Prevención de errores | D-04 se ve no asignable antes de intentarlo: etiqueta naranja, borde naranja de fila y punto naranja en el mapa. En el detalle, el botón queda deshabilitado con el motivo; para D-01, D-03 y D-05 está habilitado | `Captura_Mockup_Monitoreo.png`, `Captura_Mockup_Detalle_D04.png` |
| 6 | Reconocer antes que recordar | El mínimo de 30 % está visible en Monitoreo y Flota. El detalle muestra la regla de asignación paso a paso (disponible, batería, resultado) | `Captura_Mockup_Detalle_D04.png` |
| 8 | Diseño estético y minimalista | La parte visible de Monitoreo prioriza la flota (ID, batería, estado y ubicación). El mapa queda debajo y solo muestra ubicaciones, sin elementos decorativos | `Captura_Mockup_Monitoreo.png` |
| 9 | Ayudar a reconocer y corregir errores | "D-04 tiene batería insuficiente (18%). Mínimo requerido: 30%. Elige otro drone de la flota." en lugar de "Error de asignación" | `Captura_Mockup_Detalle_D04.png` |

Accesibilidad complementaria: foco visible en azul `#3B82F6`; el estado nunca depende solo del color; la animación se desactiva con `prefers-reduced-motion`.
