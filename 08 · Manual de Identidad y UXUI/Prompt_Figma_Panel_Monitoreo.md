# Prompt para Figma AI

```text
Actúa como diseñador UX/UI senior de sistemas de control de flotas. Crea un mock de alta fidelidad, editable en Figma, para una sola pantalla desktop (1440 × 1024 px): “SkyCampus — Panel de monitoreo de la flota”, utilizado por un operador de drones de la Escuela Colombiana de Ingeniería.

IDENTIDAD VISUAL
- Inter para la interfaz (pesos 400 y 600); JetBrains Mono para IDs de drone y códigos de misión.
- Fondo azul noche #0F172A; superficies #111C2E; bordes #25324A; texto #F8FAFC; texto secundario #94A3B8.
- Azul de acción #2563EB; violeta #8B5CF6 solo como acento secundario.
- Estados con etiqueta e indicador visual: Disponible verde #22C55E, En vuelo azul #38BDF8, En carga amarillo #FACC15, Fallo rojo #EF4444. Para “No disponible” sin subestado conocido, usa gris #64748B. Batería crítica usa ámbar #F59E0B.
- Estilo técnico, confiable, sereno y ordenado; alto contraste, tarjetas de bordes suaves, jerarquía clara y espacios generosos. No uses estética de comida, personajes, ilustraciones decorativas ni logos de terceros. Usa el wordmark “SkyCampus” y un monograma simple “SC”.

ESTRUCTURA DE LA PANTALLA
- Barra lateral compacta con marca SkyCampus y navegación: Resumen, Flota, Misiones, Configuración. “Flota” aparece activa.
- Barra superior con título de página “Monitoreo de la flota”, texto “Operación de flota · ECI” y un indicador discreto “Datos de demostración”.
- Tres tarjetas de resumen: “05 drones en flota”, “03 disponibles para asignar” y “01 requiere atención”.
- Alerta visible: “D-04 tiene 18% de batería; mínimo para asignación: 30%.”
- Tabla principal con columnas Drone, Batería, Estado, Ubicación y Acción. Mostrar exactamente estos cinco registros, sin cambiar ni agregar datos:
  • D-01 | DJI Mini 3 | 85% | Disponible | Bloque A | acción Seleccionar habilitada
  • D-02 | DJI Mini 3 | 42% | No disponible | Biblioteca | acción No asignable deshabilitada
  • D-03 | DJI Mini 3 | 91% | Disponible | Bloque C | acción Seleccionar habilitada
  • D-04 | DJI Mini 3 | 18% | Disponible | Bloque B | acción deshabilitada, motivo “Batería <30%”
  • D-05 | DJI Mini 3 | 67% | Disponible | Bloque D | acción Seleccionar habilitada
- No infieras si D-02 está en vuelo, en carga o en fallo: el dato del dominio solo dice disponible=false.
- Añade un panel compacto de control de misión con un ejemplo claramente rotulado “M-0001 · PENDIENTE (muestra)” y la acción “Cancelar misión”. Aclara visualmente que es un dato demostrativo, no un registro real.
- Mantén visible una leyenda de estados; cada estado debe incluir texto, no solo color.

HEURÍSTICAS DE NIELSEN QUE DEBE MOSTRAR EL MOCK
- #1 Visibilidad del estado: estados, batería y ubicación visibles en cada fila.
- #3 Control y libertad del usuario: acción Cancelar misión para la misión demostrativa en estado PENDIENTE.
- #5 Prevención de errores: D-02 y D-04 no se pueden asignar; D-04 explica que no alcanza el 30% mínimo.
- #8 Diseño estético y minimalista: la vista principal prioriza ID, batería, estado, ubicación y acción.
- #9 Ayudar a reconocer y recuperarse de errores: la alerta nombra el drone, el 18% observado y el mínimo de 30%.

Usa Auto Layout y componentes reutilizables para tarjetas, badges, barras de batería y botones. Mantén legibles todos los textos en el frame. Entrega una única vista desktop en modo oscuro; no agregues pantallas, drones, métricas ni funciones fuera del alcance indicado.
```
