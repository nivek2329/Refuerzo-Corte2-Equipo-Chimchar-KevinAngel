# 11 · Mocks con IA — Panel de monitoreo SkyCampus

## Estado de la entrega

El prompt queda preparado para generar **tres vistas coherentes** del panel: normal, alerta y flota vacía. Cada estado debe guardarse como imagen separada. Los estados de alerta `FALLO` y flota vacía son escenarios visuales simulados para explicar la interfaz; no reemplazan ni modifican los datos reales del MVP.

## Referencias de producto

Revisar las capturas de pantalla de monitoreo/operaciones de estas dos interfaces reales antes de generar el mock. Tomar como referencias de organización y jerarquía, sin copiar marca ni pantalla:

1. **DroneSense DSR — User Interface Overview:** captura guardada como `Referencia_DroneSense_DSR.png`; muestra mapa, controles de operación y feed del drone. [Abrir referencia oficial](https://support.dronesense.com/hc/en-us/articles/9566471478541-DSR-User-Interface-Overview)
2. **DJI FlightHub 2 — Virtual Cockpit:** captura guardada como `Referencia_DJI_FlightHub_Cockpit.webp`; muestra mapa, visualización principal y controles de operación. [Abrir manual oficial](https://fh.dji.com/user-manual/en/real-time-project-information/virtual-cockpit.html)

En la entrega final, adjuntar las dos capturas revisadas junto a las tres imágenes generadas. Evitar incluir nombres, números de serie u otra información privada que pudiera aparecer en las referencias.

## Prompt para la IA


```text
Actúa como diseñador/a senior de producto UX/UI especializado/a en interfaces operativas. Diseña un mockup de interfaz de escritorio realista y legible para SkyCampus, el sistema de operación de una flota de drones de la Escuela Colombiana de Ingeniería. No generes una lámina explicativa, póster, wireframe ni imagen de marketing: genera una pantalla de software que un operador podría usar.

BASE COMÚN PARA LAS TRES VISTAS
- Formato: una pantalla desktop 1440 × 1024 px, relación 4:3 aproximada, vista frontal, sin perspectiva ni marco de dispositivo.
- Las tres imágenes deben parecer estados de la misma aplicación: conserva exactamente retícula, sidebar, barra superior, tipografía, tabla, espaciados e iconografía; cambia solo los datos/avisos propios del estado solicitado.
- Producto: SkyCampus · Operación de flota · ECI. Usuario principal: Operador de drones.
- Identidad: interfaz oscura, sobria, técnica y humana. Azul noche #0F172A; superficies #111C2E; bordes #25324A; texto principal #F8FAFC; texto secundario #94A3B8; azul de acción #2563EB; violeta #8B5CF6 solo como acento pequeño. Interfaz en Inter; identificadores y códigos en JetBrains Mono.
- Estados siempre con etiqueta legible y un indicador, nunca comunicar solo mediante color: Disponible #22C55E; En vuelo #38BDF8; En carga #FACC15; Fallo #EF4444; No disponible sin subestado #64748B. Batería crítica #F59E0B.
- Composición de producto: sidebar compacta con monograma SC creado como ruta geométrica con nodos, navegación “Resumen”, “Flota”, “Misiones”, “Configuración”; barra superior con “SkyCampus”, “Monitoreo de la flota” y “Operación de flota · ECI”. Mostrar la opción “Flota” activa. Jerarquía clara, tabla ordenada, líneas finas, superficies planas, bordes sutiles y espacio en blanco. Nada de neón, glassmorphism, gradientes brillantes, tarjetas repetidas, foto de stock ni ilustraciones decorativas de drones.
- La pantalla se centra en consultar la flota. Mostrar tabla con columnas Drone, Batería, Estado, Ubicación y Acción. No inventar métricas, rutas, coordenadas ni funciones.
- Datos reales base del escenario normal (mantener literalmente):
  D-01 | DJI Mini 3 | 85% | Disponible | Bloque A | Seleccionar habilitado
  D-02 | DJI Mini 3 | 42% | No disponible | Biblioteca | acción deshabilitada; no inventar la causa ni cambiar el estado a En vuelo, En carga o Fallo
  D-03 | DJI Mini 3 | 91% | Disponible | Bloque C | Seleccionar habilitado
  D-04 | DJI Mini 3 | 18% | Disponible | Bloque B | asignación deshabilitada: batería inferior al mínimo de 30%
  D-05 | DJI Mini 3 | 67% | Disponible | Bloque D | Seleccionar habilitado
- Cuando haya texto pequeño, prioriza su legibilidad y escritura exacta. No agregues drones. No uses lorem ipsum. El resultado debe parecer una captura de una app de operación real, no arte de IA.

ESTADO A — NORMAL
- Nombre del archivo: 11_Mock_Normal.png
- Título de estado: “Flota operativa”. Mostrar los cinco registros base sin cambiar un solo valor.
- Resumen compacto: “05 drones en flota”, “03 disponibles para asignar”, “01 requiere atención”.
- Aviso de batería: “D-04 tiene 18% de batería; mínimo para asignación: 30%.” D-04 conserva el estado “Disponible”, pero su acción para asignar queda deshabilitada por batería insuficiente.
- D-02 se presenta como “No disponible” sin explicación inventada.

ESTADO B — ALERTA
- Nombre del archivo: 11_Mock_Alerta.png
- Conserva el panel, los datos base y el contexto de la vista normal. Añade una alerta de fallo destacada y accionable, sin tapar la tabla ni usar solo color.
- Para visualizar el estado solicitado, representa D-02 con etiqueta “FALLO” solo en esta variante simulada y agrega una banda visible “ESCENARIO DE DEMOSTRACIÓN · estado simulado”. No presentes este cambio como dato real: el registro base de D-02 solo dice “No disponible”. No alteres batería (42%) ni ubicación (Biblioteca).
- Mensaje concreto: “D-02 · Fallo reportado en este escenario simulado. Drone no asignable. Revisar estado.” Acción visible: “Ver detalle”. No inventes la naturaleza de la avería ni un sensor, ruta o procedimiento técnico específico.
- Mantén también la alerta real de batería de D-04: 18%; mínimo 30%. No mezcles ese aviso con el fallo simulado de D-02.

ESTADO C — VACÍO / TODOS EN MISIÓN
- Nombre del archivo: 11_Mock_Vacio.png
- Muestra la misma pantalla cuando no queda ningún drone disponible para asignar. La tabla conserva los cinco IDs, modelos, baterías y ubicaciones base, pero el estado de cada fila cambia a “En vuelo” únicamente en esta simulación del estado vacío.
- Incluye arriba una etiqueta claramente visible: “ESTADO SIMULADO · los cinco drones están en misión”. No presentes esta variación como el estado real del inventario.
- Mostrar un mensaje vacío útil: “No hay drones disponibles para asignar”, explicación “Los 5 drones están en misión en este escenario”, y una acción “Actualizar flota”. No inventar horarios estimados de regreso, recorridos ni ubicaciones distintas.

CALIDAD DE ENTREGA
- Devuelve tres imágenes independientes, una para cada estado, todas con las mismas dimensiones y el mismo diseño.
- Revisa la ortografía de cada etiqueta; conserva acentos y porcentajes. Comprueba especialmente “No disponible”, “En vuelo”, “Fallo”, “Biblioteca” y el mínimo de 30%.
- Las imágenes son mocks de interfaz, no datos conectados a drones reales. No añadas un logotipo oficial de la universidad; usa solo el wordmark SkyCampus y el monograma SC descrito.
```

## Heurísticas de Nielsen reflejadas

| Heurística | Aplicación verificable en los mocks |
|---|---|
| **#1 Visibilidad del estado del sistema** | Cada fila enseña batería, disponibilidad/estado y ubicación. Las vistas de alerta y vacío rotulan de forma prominente que son escenarios simulados. |
| **#3 Control y libertad del usuario** | La vista de fallo ofrece `Ver detalle`; la vista vacía ofrece `Actualizar flota`, acciones reconocibles para seguir operando. |
| **#5 Prevención de errores** | No habilitar asignación para D-02, y bloquear la de D-04 por batería inferior al 30%; mostrar el motivo junto al control. |
| **#8 Diseño estético y minimalista** | Priorizar la información necesaria para decidir (ID, batería, estado, ubicación y acción), con alertas jerarquizadas y sin datos decorativos. |
| **#9 Ayudar a reconocer, diagnosticar y recuperarse de errores** | Los mensajes indican qué drone requiere atención y qué puede hacer el operador; la alerta de D-04 muestra valor observado y umbral requerido. La falla de D-02 se limita a lo conocido en el escenario simulado. |

## Evidencias que faltan para cerrar el punto

- [x] Adjuntar las dos capturas de referencia real.
- [ ] Generar y agregar `11_Mock_Normal.png`.
- [ ] Generar y agregar `11_Mock_Alerta.png`.
- [ ] Generar y agregar `11_Mock_Vacio.png`.
- [ ] Revisar las imágenes y corregir texto/layout si la IA deformó datos.


