# 08 · Sistema de diseño y UX — Monferno

El manual navegable de identidad es [Manual_Identidad_SkyCampus_Monferno.html](Manual_Identidad_SkyCampus_Monferno.html). El prototipo de los tres pasos de asignación está en [`../11 · Mocks con IA/Flujo_Asignacion_Automatica_Monferno.html`](../11%20%C2%B7%20Mocks%20con%20IA/Flujo_Asignacion_Automatica_Monferno.html) y se apoya en los tokens de este manual.

## Entregables

- `Manual_Identidad_SkyCampus_Monferno.html`: marca, paleta de acción y estados, tipografía, etiquetas, navegación, voz, microcopy y principios de interacción. Abrir localmente en un navegador.
- `../11 · Mocks con IA/Flujo_Asignacion_Automatica_Monferno.html`: prototipo de referencia del flujo, con estados normales y casos de bloqueo.
- `../11 · Mocks con IA/Prompt_IA_Flujo_Monferno.md`: prompt trazable para regenerar o revisar el mock con IA.

## Tarjeta de drone

El manual (sección 07) muestra el componente con sus 5 estados lado a lado: misma estructura HTML/CSS (ID, tipo y ubicación, estado con icono + texto + color, batería y una sola acción); solo cambian el color del borde superior y la acción. El color nunca es la única señal.

| Estado | Color | Regla visual |
|---|---|---|
| DISPONIBLE | `#46D39A` verde seguro | Puede ser candidato si también cumple batería y capacidad |
| EN VUELO | `#55C7E8` cian | Muestra misión activa; no ofrece asignación |
| EN CARGA | `#F5C451` ámbar | Muestra progreso/carga restante; no es elegible |
| FALLO | `#FF7079` coral | Acción de emergencia y aviso al técnico |
| MANTENIMIENTO | `#9AABC0` gris técnico | Indica indisponibilidad y opción de ver diagnóstico |

Tokens aplicados en el manual y el prototipo: fondo `#0B1422`, superficie `#14243A`, borde `#29415D`, texto `#EDF4FB`, secundario `#A8BBCF`; azul de acción `#58A6FF`. El violeta `#B49AFF` es solo acento de marca, no codifica estados.

## Componentes y estados

- **Botón Asignar** (manual, sección 08): default, hover, procesando (spinner, bloquea doble clic), éxito (“Asignada a D-04”) y deshabilitado (“Sin drone apto”).
- **Batería** (manual, sección 09): porcentaje siempre visible; verde desde 60 %, amarillo de 30–59 % y rojo bajo 30 % con advertencia “Bajo el mínimo de 30 %: no asignable”. El prototipo usa la misma regla en todas las tarjetas.
- **Navegación:** paso actual resaltado, pasos previos marcados y regreso explícito a Flota.
- **Acción de emergencia:** cancelar misión en vuelo es una acción grande, persistente y separada de la acción primaria.

## Leyes UX aplicadas

- **Fitts:** la acción de emergencia tiene un área amplia (mínimo visual 48 px), contraste alto y posición persistente; no queda escondida en un menú. Los controles frecuentes tienen áreas cómodas para puntero o toque.
- **Hick:** la flota se filtra por estado antes de elegir; el operador reduce las opciones visibles. El prototipo enseña 6 drones de muestra, no los 20 de una flota real.
- **Miller:** el panel de alertas no acumula más de 7 avisos sin confirmar (manual, sección 10).

El HTML es un prototipo local de alta fidelidad con datos de demostración; no llama a una API ni persiste cambios.
