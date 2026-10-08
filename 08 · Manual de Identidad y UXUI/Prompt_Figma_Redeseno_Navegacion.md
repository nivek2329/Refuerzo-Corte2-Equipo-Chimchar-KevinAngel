# Prompt de refinamiento para el archivo actual de Figma

```text
Trabaja SOBRE el archivo de Figma que ya existe. Refina su identidad y su prototipo; no te limites a dibujar botones ni reinicies el proyecto desde una plantilla genérica.

OBJETIVO
Convertir SkyCampus en un producto de operación de drones reconocible, sobrio y propio de la Escuela Colombiana de Ingeniería, con cuatro vistas conectadas. La vista Flota existente debe conservarse como base y seguir funcionando.

NAVEGACIÓN FUNCIONAL — OBLIGATORIO
En el prototipo de Figma configura la interacción real de cada opción del menú lateral:
- Resumen → frame “01 Resumen”
- Flota → frame “02 Flota”
- Misiones → frame “03 Misiones”
- Configuración → frame “04 Configuración”
Usa On click → Navigate to, con transición Smart Animate breve. Marca el frame “02 Flota” como inicio del prototipo para conservar la vista actual. En cada frame, resalta solo la sección activa. No dejes ningún elemento que parezca botón sin interacción; si está deshabilitado, muéstralo claramente deshabilitado. Al terminar, comprueba en Present que los cuatro destinos abren con un clic y que desde cada uno se puede volver a los demás.

VISTAS Y DATOS
1. Resumen: muestra 5 drones, 3 disponibles para asignar y 1 alerta de batería. Incluye un esquema vectorial sencillo del campus con nodos para Bloque A, Bloque B, Bloque C, Bloque D y Biblioteca; rotúlalo “esquema conceptual, no geográfico”. No inventes coordenadas ni rutas reales.
2. Flota: conserva la tabla con estas filas exactas: D-01 / DJI Mini 3 / 85% / Disponible / Bloque A; D-02 / DJI Mini 3 / 42% / No disponible / Biblioteca; D-03 / DJI Mini 3 / 91% / Disponible / Bloque C; D-04 / DJI Mini 3 / 18% / Disponible / Bloque B, asignación deshabilitada por batería menor a 30%; D-05 / DJI Mini 3 / 67% / Disponible / Bloque D. No infieras si D-02 está en vuelo, cargando o en fallo: el modelo solo dice disponible=false.
3. Misiones: usa “M-0001 · PENDIENTE (muestra)” claramente marcado como dato demostrativo. Permite ver la acción Cancelar misión, solo mientras su estado sea PENDIENTE.
4. Configuración: muestra únicamente reglas del MVP conocidas: batería mínima de asignación 30%, destinos Bloque A/B/C/D y Biblioteca, y tipos de carga SOBRE/CARPETA/LIBRO. No agregues valores de configuración no definidos.

IDENTIDAD SKY CAMPUS
- Azul noche #0F172A, superficies #111C2E, bordes #25324A, texto #F8FAFC, texto secundario #94A3B8.
- Azul de acción #2563EB y violeta #8B5CF6 como acento muy puntual.
- Disponible #22C55E, en vuelo #38BDF8, en carga #FACC15, fallo #EF4444, sin subestado #64748B, batería crítica #F59E0B.
- Inter para interfaz y JetBrains Mono para IDs y códigos. Cada estado lleva texto e indicador; nunca dependas solo del color.

DIRECCIÓN DE ARTE
Haz que parezca un producto diseñado por un equipo de ingeniería, no un dashboard automático de plantilla: composición editorial con una retícula asimétrica, jerarquía tipográfica marcada, tabla legible y un esquema de campus creado con líneas finas y nodos. Diseña un monograma “SC” propio basado en una ruta con nodos, integrado discretamente en la navegación. Prefiere superficies planas, bordes finos y una sola sombra suave. Evita tarjetas repetidas en exceso, gradientes brillantes, neón, glassmorphism, globos 3D, ilustraciones de drones genéricas, stock photos, gráficos inventados y texto de relleno.

DETALLES UX
- En todas las vistas conserva el menú lateral y la barra superior; el estado activo de navegación debe coincidir con la vista abierta.
- En Flota, muestra “D-04 tiene 18% de batería; mínimo para asignación: 30%” y deshabilita su asignación. D-02 también queda no asignable, sin inventar el motivo.
- Mantén la tabla enfocada en ID, batería, estado, ubicación y acción. Las acciones disponibles deben tener prototipo conectado; las no disponibles deben explicar por qué.
- Usa Auto Layout, componentes reutilizables y estados normal, hover, foco, seleccionado y deshabilitado. Mantén textos legibles y evita funciones ajenas al MVP.
```
