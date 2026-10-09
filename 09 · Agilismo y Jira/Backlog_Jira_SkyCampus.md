# Backlog Jira · SkyCampus MVP

Backlog creado en el espacio `DOSW_LAB3_KGR` (clave `SCRUM`) siguiendo el punto 9 de `DOSW_Equipo_Chimchar_fixed.html`.

## Jerarquía visible en Jira

```text
Épica SCRUM-1: Digitalizar el reparto interno de la ECI mediante una flota de drones supervisada
├── Feature SCRUM-2 (tipo Jira: Tarea): Gestión de flota de drones del campus
├── HU SCRUM-3: Ver flota de drones del campus [relacionada con SCRUM-2]
├── HU SCRUM-4: Asignar drone disponible a una misión [relacionada con SCRUM-2]
│   ├── Subtarea SCRUM-6: Filtrar drones disponibles con batería mínima
│   ├── Subtarea SCRUM-7: Validar drone y datos de misión antes de asignar
│   └── Subtarea SCRUM-8: Registrar la asignación y comunicar el resultado
└── HU SCRUM-5: Cancelar una misión pendiente [relacionada con SCRUM-2]
```

## Observación sobre la jerarquía de Jira

El selector de tipos de este espacio solo ofrece `Epic`, `Tarea`, `Historia` y `Error`; no tiene tipo `Feature`. Además, Jira solo permite elegir una Epic como padre de una Historia, no una Tarea. Para respetar la jerarquía configurada, `SCRUM-2` se creó como Tarea con “FEATURE” explicado en su descripción, hija de `SCRUM-1`. Las tres historias son hijas de `SCRUM-1` y se vincularon a `SCRUM-2` con la relación “está relacionado con”. De esta forma queda trazable la relación entre Feature e historias sin cambiar la configuración del proyecto.

## Épica

**Clave:** SCRUM-1  
**Tipo:** Epic  
**Nombre:** Digitalizar el reparto interno de la ECI mediante una flota de drones supervisada

**Objetivo:** Permitir que la comunidad de la ECI gestione repartos internos con drones y que un operador pueda supervisar la flota y sus misiones.

**Criterio de valor:** El operador puede consultar el estado de los drones y gestionar la asignación o cancelación de misiones con las reglas del MVP.

## Feature

**Clave:** SCRUM-2  
**Tipo creado:** Tarea (el espacio no ofrece tipo Feature)  
**Nombre:** Gestión de flota de drones del campus  
**Épica padre:** SCRUM-1

**Descripción:** Agrupa las capacidades del MVP para consultar la flota, asignar drones elegibles a misiones y cancelar misiones pendientes.

## Historias de usuario

### HU SCRUM-3 · Ver flota de drones del campus

**Tipo:** Historia  
**Padre:** SCRUM-1  
**Relación:** “está relacionado con” SCRUM-2

**Como** operador de drones, **quiero** ver los drones con su identificador, batería, estado y ubicación, **para** elegir un drone adecuado para la operación.

**Criterio de aceptación:** La vista presenta los drones con ID, batería, estado y ubicación, y diferencia claramente los drones no disponibles.

### HU SCRUM-4 · Asignar drone disponible a una misión

**Tipo:** Historia  
**Padre:** SCRUM-1  
**Relación:** “está relacionado con” SCRUM-2

**Como** operador de drones, **quiero** asignar a una misión un drone disponible que cumpla el mínimo de batería, **para** ejecutar el reparto sin asignar un drone no elegible.

**Criterios de aceptación:**

1. Dado un drone disponible con batería igual o superior al 30% y una misión válida, cuando el operador confirma la asignación, entonces el sistema vincula el drone a la misión y comunica el resultado.
2. Dado un drone no disponible o con batería inferior al 30%, cuando se intenta asignarlo, entonces el sistema rechaza la asignación e informa la causa; para batería insuficiente muestra el nivel observado y el mínimo requerido.

#### Subtareas

1. **SCRUM-6 · Filtrar drones disponibles con batería mínima** — Implementar la consulta que selecciona únicamente drones disponibles con batería igual o superior al 30%.
2. **SCRUM-7 · Validar drone y datos de misión antes de asignar** — Verificar disponibilidad, batería mínima y datos obligatorios de la misión antes de continuar.
3. **SCRUM-8 · Registrar la asignación y comunicar el resultado** — Persistir la relación entre misión y drone asignado y emitir el resultado por el mecanismo definido para el MVP.

### HU SCRUM-5 · Cancelar una misión pendiente

**Tipo:** Historia  
**Padre:** SCRUM-1  
**Relación:** “está relacionado con” SCRUM-2

**Como** operador de drones, **quiero** cancelar una misión que todavía está pendiente, **para** corregir una solicitud antes de que comience su ejecución.

**Criterio de aceptación:** La acción Cancelar está disponible para una misión PENDIENTE; al confirmar, la misión queda cancelada y deja de ofrecerse la acción. Para otros estados, no se permite cancelar desde este flujo.

## Estado de los tickets

- El espacio estaba vacío antes de este trabajo.
- Épica SCRUM-1 y Feature SCRUM-2 quedaron en `Por hacer`.
- Historias SCRUM-3, SCRUM-4 y SCRUM-5 quedaron en `Por hacer`.
- Subtareas SCRUM-6, SCRUM-7 y SCRUM-8 quedaron en `Por hacer`.
- Sin asignar responsables, sprint ni puntos de historia porque no están definidos en las instrucciones del reto.
- Evidencia visual capturada (ver sección "Evidencia").

## Reglas de dominio reflejadas

- Umbral mínimo de asignación: 30% de batería.
- No se inventa un subestado para un drone que solo figura como no disponible.

## Evidencia

| Captura | Qué muestra |
|---|---|
| `Captura_Backlog_Jira.png` | Backlog del espacio `DOSW_LAB3_KGR`: Feature SCRUM-2 y las HU SCRUM-3, SCRUM-4 y SCRUM-5, todas con la épica "Digitalizar el reparto…" (SCRUM-1) como padre y en estado *Por hacer* |
| `Captura_HU_SCRUM-4_Subtareas.png` | Detalle de la HU SCRUM-4: criterio de aceptación 2 (batería < 30 % o drone no disponible), feature relacionada SCRUM-2 y sus subtareas SCRUM-6, SCRUM-7 y SCRUM-8 |
