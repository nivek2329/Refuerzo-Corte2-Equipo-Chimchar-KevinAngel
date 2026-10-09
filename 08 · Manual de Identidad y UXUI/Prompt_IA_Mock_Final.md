# Prompt final para generar el mock con IA

Este prompt reproduce `Mockup_SkyCampus.html` a partir de la versión vigente de `Manual_Identidad_SkyCampus.html`. Los prompts `Prompt_Figma_*.md` son las exploraciones iniciales en Figma y quedan como historial del proceso.

```text
Actúa como diseñador/a UX/UI senior de sistemas de control. Genera un prototipo HTML de una sola página
(HTML + CSS + JS en un archivo) del "Panel de monitoreo de la flota" de SkyCampus, el sistema de reparto con
drones de la Escuela Colombiana de Ingeniería. Usuario: operador de drones que decide rápido.

IDENTIDAD (manual SkyCampus, modo oscuro)
- Fondo #0F172A, superficies #111C2E, bordes #25324A, texto #F8FAFC, secundario #94A3B8.
- Marca (nunca para estados): azul acción #2563EB, navegación #1D4ED8, foco #3B82F6, violeta #8B5CF6 solo en
  logo, avatar y brillo de fondo.
- Estados del drone: Disponible #22C55E, En vuelo #38BDF8, En carga #FACC15, Fallo #EF4444, No disponible #64748B.
- Elegibilidad: Asignable #14B8A6, No asignable por batería < 30% #F97316. Misión PENDIENTE #C4B5FD.
- Cada color tiene un solo significado y siempre va con su etiqueta.
- Tipografía: Inter 400 (texto), 600 (etiquetas, secciones), 700 solo título de página y logotipo; JetBrains Mono
  para TODO ID de drone o código de misión (también en títulos, alertas y diálogos). Tamaño mínimo 11 px.
- Voz: dato + requisito + acción. Prohibido en pantalla: "dato demostrativo", "muestra", "ilustrativo", notas
  sobre el modelo de datos.

DATOS (exactos, modelo Chimchar: Drone(id, modelo, bateria, disponible, ubicacion))
D-01 DJI Mini 3 85% disponible Bloque A · D-02 42% NO disponible Biblioteca · D-03 91% disponible Bloque C ·
D-04 18% disponible Bloque B · D-05 67% disponible Bloque D. Regla: asignable = disponible y batería ≥ 30%.
No inventes vuelo, carga ni fallo para ningún drone.
Misión M-0001: PENDIENTE, Bloque A → Biblioteca, CARPETA, sin drone. Tipos de carga: SOBRE, CARPETA, LIBRO.

VISTAS (navegación lateral: Monitoreo, Flota de drones, Planificación, Historial, Configuración)
1. Monitoreo: arriba la lista de los 5 drones (ID, batería con barra, estado + elegibilidad, ubicación);
   al lado, resumen (05 registrados, 03 asignables, 01 no asignable, 01 no disponible) y avisos de D-04 y D-02;
   debajo, mapa esquemático con la ubicación registrada de cada drone (sin rutas ni decoración).
2. Flota: tabla con las mismas columnas; la fila de D-04 con borde y etiqueta naranja "No asignable · batería".
3. Detalle de drone (dinámico según el drone elegido): ubicación registrada, regla de asignación paso a paso
   (disponible / batería ≥ 30% / resultado), botón "Asignar a M-0001" habilitado solo si el drone es asignable,
   la misión está PENDIENTE y aún no tiene drone; en otro caso, deshabilitado con el motivo.
4. Planificación: M-0001 con botón "Cancelar misión" que abre un diálogo de confirmación
   ("Mantener misión" con foco por defecto / "Sí, cancelar misión" en rojo).
5. Historial: búsqueda y filtro por estado.
6. Configuración: batería mínima 30%, destinos, tipos de carga y vocabulario de estados.
El estado de M-0001 es una sola fuente: al asignar o cancelar, se actualizan Planificación, Detalle, Historial
y la habilitación del botón de asignar.

HEURÍSTICAS A CUMPLIR: #1 visibilidad del estado, #3 control (cancelar con confirmación), #4 consistencia,
#5 prevención de errores (D-04 no asignable visible antes de intentarlo), #6 reconocer antes que recordar,
#8 minimalismo, #9 mensajes como "D-04 tiene batería insuficiente (18%). Mínimo requerido: 30%."
Responsive (desktop + móvil), foco visible y prefers-reduced-motion.
```
