# 11 · Mocks con IA — Prompt del panel de monitoreo SkyCampus

## Paso 1 · Referencias reales de monitoreo de flotas

| Archivo | Sistema real | Qué muestra | Decisiones que se trasladan al panel | Dónde se ve en los mocks |
|---|---|---|---|---|
| `Referencia_Skydio_Fleet_Manager.png` | Skydio Cloud · módulo **Fleet** ([fuente oficial](https://support.skydio.com/hc/en-us/articles/4402745384475)) | Vehículo seleccionado de la flota: nombre, etiqueta de estado con texto ("Online") y batería en % con barra de color, y acceso a su historial | (a) Estado como etiqueta con texto, nunca solo color. (b) Batería como % + barra de color en la misma línea del drone. (c) Seleccionar un vehículo lleva a su ficha → acción "Ver detalle" | Badges de estado y elegibilidad; barras de batería por fila; barra "Drone seleccionado · Ver detalle" |
| `Referencia_Skydio_Reports_Summary.png` | Skydio Cloud · **Reports › Summary** (misma fuente) | Tarjetas de indicadores de la flota con cifras grandes (vuelos, pilotos activos, vehículos activos) | (a) Resumen numérico de la flota en tarjetas con cifra grande y etiqueta corta. (b) Cifras separadas del listado para leerlas de un vistazo | Panel "Flota en resumen" (registrados, asignables, no asignables, no disponibles) |

`Referencia_DroneSense_DSR.png` y `Referencia_DJI_FlightHub_Cockpit.webp` son cabinas de pilotaje de **un** drone. No sirven para el panel de flota; quedan solo como referencia de la vista de detalle.

## Paso 2 · Estilo

Identidad del manual aprobado en el reto 08 (`Manual_Identidad_SkyCampus.html`, carpeta `08 · Manual de Identidad y UXUI` de este repositorio).

## Paso 3 · Datos del RF SC-01 "Registrar misión de reparto"

- **Entrada que necesita el operador:** la flota con ID, batería, disponibilidad y ubicación de cada drone, y la regla de elegibilidad (disponible y batería ≥ 30 %).
- **Acciones del operador** (plantilla del curso): seleccionar drone, asignar a misión (M-0001) y ver detalle.

## Paso 4 · Prompt

```text
Actúa como diseñador/a UX/UI senior de sistemas de control. Genera TRES capturas desktop (1440 × 1024 px,
vista frontal, sin marco de dispositivo) del "Panel de monitoreo de la flota" de SkyCampus, el sistema de
reparto con drones de la Escuela Colombiana de Ingeniería. Usuario: operador de drones que decide rápido.
Las tres son estados de la MISMA aplicación: misma retícula, barra lateral, tipografía y componentes.
Debe parecer software real, no arte de IA ni wireframe.

IDENTIDAD (manual SkyCampus, modo oscuro)
- Fondo #0F172A, superficies #111C2E, bordes #25324A, texto #F8FAFC, secundario #94A3B8.
- Marca (nunca para estados): azul acción #2563EB, navegación #1D4ED8; violeta #8B5CF6 solo en logo y avatar.
- Estados del drone: Disponible #22C55E, En vuelo #38BDF8, En carga #FACC15, Fallo #EF4444, No disponible #64748B.
- Elegibilidad: Asignable #14B8A6, No asignable por batería < 30% #F97316, otro motivo "No asignable" gris.
  Cada fila lleva estado + elegibilidad. Cada color, un solo significado y siempre con texto.
- Inter 400/600 (700 solo título y logotipo); JetBrains Mono para todo ID. Mínimo 11 px.
- Navegación lateral: Monitoreo (activa), Flota de drones, Planificación, Historial, Configuración.
- Voz: dato + requisito + acción. Sin lorem ipsum ni métricas, rutas o coordenadas inventadas.

ESTRUCTURA COMÚN
Título "Monitoreo de la flota" y etiqueta "MÍNIMO PARA ASIGNAR: 30%".
Izquierda: lista "Flota del campus" (todos los drones en una vista). Cada fila: selector circular, ID, modelo ·
ubicación, barra de batería con %, estado y elegibilidad. Debajo, barra de acción "Drone seleccionado" con
"Ver detalle" y "Asignar a M-0001", y una nota con los drones no seleccionables y su motivo.
Derecha: "Flota en resumen" (4 cifras) y panel de avisos ordenado por gravedad.

ACCIONES (de la plantilla del curso)
- Seleccionar drone: solo los asignables tienen el selector activo; los no asignables lo muestran deshabilitado
  (punteado) y no se pueden elegir.
- Asignar a misión: botón primario habilitado solo con un drone asignable seleccionado.
- Ver detalle: botón secundario sobre el drone seleccionado; en Alerta también en el aviso del drone con fallo.

DATOS BASE: D-01 85% Bloque A · D-03 91% Bloque C · D-05 67% Bloque D · D-04 18% Bloque B ·
D-02 42% Biblioteca (no disponible). Todos DJI Mini 3.

ESTADO A — NORMAL (11_Mock_Normal.png)
D-01, D-03, D-05: Disponible + Asignable. D-04: Disponible + "No asignable · batería" (borde naranja).
D-02: No disponible + No asignable. D-01 seleccionado; "Asignar a M-0001" habilitado.
Resumen: 05 registrados, 03 asignables, 01 no asignable (batería < 30%), 01 no disponible.
Avisos: D-04 batería 18% (mínimo 30%, elige otro drone o espera recarga) y D-02 no disponible.

ESTADO B — ALERTA (11_Mock_Alerta.png) · escenario simulado
Igual que A, pero D-02 aparece "Fallo" + "No asignable" (borde rojo) y el resumen dice "En fallo 01".
Primer aviso, crítico e ícono rojo: "D-02 · Fallo reportado — Drone no asignable. Revisa su estado antes de
planificar misiones." con "Ver detalle de D-02". Debajo, separado, el aviso de batería de D-04 (ícono naranja).
No inventar la causa del fallo.

ESTADO C — VACÍO (11_Mock_Vacio.png) · escenario simulado
Los 5 drones "En vuelo" + "No asignable · en misión". Coherente con la regla del 30 %: todos salieron con
batería suficiente y en vuelo marcan D-01 72%, D-03 80%, D-05 49%, D-04 55%, D-02 38%.
Ningún selector activo; barra de acción "Ninguno" con botones deshabilitados ("Sin drones asignables").
Resumen: 05 registrados, 00 asignables, 05 en vuelo, 00 no disponibles. El panel de avisos se convierte en
estado vacío: "No hay drones disponibles para asignar", "Los 5 drones están en misión. Actualiza la flota
cuando alguno aterrice.", botón "Actualizar flota", y la línea "Sin avisos operativos: los 5 drones en vuelo
tienen batería ≥ 30%".

HEURÍSTICAS OBJETIVO
#1 Visibilidad del estado: estado, elegibilidad y batería de cada drone sin clics; resumen numérico.
#5 Prevención de errores: el selector y el botón Asignar impiden elegir un drone no asignable antes de intentarlo.
#8 Minimalismo: la pantalla muestra solo lo necesario para asignar (sin mapa ni decoración en estas capturas).
#9 Mensajes claros: cada aviso dice el dato, el requisito y la acción.

CALIDAD: revisa la ortografía y cada porcentaje. No agregues drones ni logotipos oficiales.
```

La aclaración de que B y C son escenarios simulados va en el README y no en la pantalla, por la regla de voz del manual (sección 10). La excepción está documentada en la regla de consistencia de la sección 09.
