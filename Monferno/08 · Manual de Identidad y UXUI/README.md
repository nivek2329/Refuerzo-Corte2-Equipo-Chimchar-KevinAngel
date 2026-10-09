# 08 · Sistema de diseño y UX — Monferno

El manual navegable de identidad es [Manual_Identidad_SkyCampus_Monferno.html](Manual_Identidad_SkyCampus_Monferno.html). El prototipo de los tres pasos de asignación está en [`../11 · Mocks con IA/Flujo_Asignacion_Automatica_Monferno.html`](../11%20%C2%B7%20Mocks%20con%20IA/Flujo_Asignacion_Automatica_Monferno.html) y se apoya en los tokens de este manual.

## Entregables

- `Manual_Identidad_SkyCampus_Monferno.html`: marca, paleta de acción y estados, tipografía, etiquetas, navegación, voz, microcopy y principios de interacción. Abrir localmente en un navegador.
- `../11 · Mocks con IA/Flujo_Asignacion_Automatica_Monferno.html`: prototipo de referencia del flujo, con estados normales y casos de bloqueo.
- `../11 · Mocks con IA/Prompt_IA_Flujo_Monferno.md`: prompt trazable para regenerar o revisar el mock con IA.

## Tarjeta de drone

Cada tarjeta presenta ID, tipo, batería, estado con texto y acción disponible. El color nunca es la única señal.

| Estado | Color | Regla visual |
|---|---|---|
| DISPONIBLE | `#22C55E` verde | Puede ser candidato si también cumple batería y capacidad |
| EN VUELO | `#38BDF8` azul | Muestra misión activa; no ofrece asignación |
| EN CARGA | `#FACC15` amarillo | Muestra progreso/carga restante; no es elegible |
| FALLO | `#EF4444` rojo | Acción de emergencia y aviso al técnico |
| MANTENIMIENTO | `#64748B` gris | Indica indisponibilidad y opción de ver diagnóstico |

Tokens de interfaz heredados del manual Chimchar: fondo `#0F172A`, superficie `#111C2E`, borde `#25324A`, texto `#F8FAFC`, secundario `#94A3B8`; azul de acción `#2563EB` y violeta `#8B5CF6` solo como acento de marca. El violeta no codifica estados.

## Componentes y estados

- **Botón Asignar:** reposo, hover, procesando, éxito y deshabilitado. En el prototipo cambia a “Asignación confirmada” al completar el flujo; queda deshabilitado ante clima adverso o sin candidato apto.
- **Batería:** porcentaje siempre visible; verde desde 60 %, amarillo de 30–59 % y rojo bajo 30 %. La regla de negocio (mínimo 30 %) se presenta junto al estado, no se infiere del color.
- **Navegación:** paso actual resaltado, pasos previos marcados y regreso explícito a Flota.
- **Acción de emergencia:** cancelar misión en vuelo es una acción grande, persistente y separada de la acción primaria.

## Leyes UX aplicadas

- **Fitts:** la acción de emergencia tiene un área amplia (mínimo visual 48 px), contraste alto y posición persistente; no queda escondida en un menú. Los controles frecuentes tienen áreas cómodas para puntero o toque.
- **Hick:** los 20 drones se agrupan por estado y se filtran por tipo antes de elegir; el operador no enfrenta una lista plana de 20 decisiones. El prototipo muestra una muestra por grupo para que la relación entre organización y selección sea legible.

El HTML es un prototipo local de alta fidelidad con datos de demostración; no llama a una API ni persiste cambios.
