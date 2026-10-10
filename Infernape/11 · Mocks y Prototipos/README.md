# 11 · Prototipo navegable del dashboard Enterprise multi-sede

Prototipo: [`Dashboard_Enterprise_Prototipo.html`](Dashboard_Enterprise_Prototipo.html). Abrirlo **desde esta carpeta del repositorio** (usa `../08 · Identidad y UX/tokens.css` y `componentes.css`). Funciona sin servidor; solo descarga las fuentes de Google si hay internet.

Mock vs prototipo: el mock de Monferno mostraba pantallas; este simula la aplicación: navegación entre vistas, datos que cambian solos, un asistente de 4 pasos con validación y todos los estados de error con salida.

## Cómo cumple el reto

| Pedido | Dónde |
|---|---|
| 1. Red completa de 4 sedes con la flota en tiempo real | Vista **Red**: mapa esquemático de Bogotá (ECI, UNAL, Uniandes, estaciones EST-116/26/72) y Medellín (EAFIT), drones en vuelo moviéndose, tarjetas por sede con barra de estados, totales y ranking de eficiencia. Vista **Flota**: 24 drones con filtros por sede y estado. Batería, estados y posiciones se actualizan cada 3 s. |
| 2. Dos identidades (ECI y UNAL) solo cambiando tokens | Selector **ECI / UNAL** en la barra: cambia `data-sede` en `<html>` y nada más. Colores, tipografía (Space Grotesk ↔ Merriweather), radios y escudo salen de `tokens.css` del reto 08. Cambia también quién planifica (la coordinadora de la ECI o el coordinador de la UNAL). |
| 3. Flujo completo de planificación inter-sede con todos sus errores | Vista **Planificar ruta** (SC-15): datos del envío → verificación (sede/datos, clima, Aerocivil, estaciones) → propuesta de etapas ≤ 5 km con esperas ≤ 30 min → confirmación y vuelo en vivo. Errores: Aerocivil rechaza, sin estación disponible, paquete demasiado pesado, destino fuera del área (EAFIT desde Bogotá), clima adverso y Aerocivil sin respuesta. Cada uno dice la causa, confirma que no se reservó nada y ofrece una acción que lo resuelve. |
| 4. Probado por un compañero sin instrucciones | Protocolo y registro en la sección siguiente. |

Los errores aparecen por **datos reales del formulario**, no por botones ocultos: peso > 15 000 g, destino EAFIT, cadena de frío en una ruta cuya estación no refrigera, o salir entre 10:00 y 12:00 entre ECI y UNAL (zona restringida). El *Panel del evaluador*, cerrado al final de la página, solo precarga esos casos para revisar rápido; no se muestra al compañero en la prueba.

## Heurísticas de Nielsen (10/10)

| # | Heurística | Cómo se aplica |
|---|---|---|
| 1 | Visibilidad del estado | Indicador "En vivo", pasos 1–4 del asistente, cada verificación pasa de "Consultando…" a correcto/no cumple, barra de progreso del vuelo. |
| 2 | Relación con el mundo real | Sedes por su sigla, gramos, km, minutos, "estación de carga", "Aerocivil"; mapa con la geografía real (Bogotá/Medellín). |
| 3 | Control y libertad | "Volver a los datos" durante la verificación, "Cambiar datos" y "Descartar ruta" (libera reservas) en la propuesta, "Editar los datos" en cada error, "Cerrar" en el selector. |
| 4 | Consistencia | Mismos componentes del sistema de diseño en todas las vistas; mismos nombres de estado y colores que el manual del reto 08. |
| 5 | Prevención de errores | El selector de destino marca EAFIT como "fuera del área de vuelo" antes de elegirla; altura limitada a 120 m por el control; tipo "Muestra de laboratorio" activa la cadena de frío. |
| 6 | Reconocer antes que recordar | Ayudas debajo de cada campo (límites de peso por tipo de drone, regla de 120 m), propuesta con todas las etapas, esperas y código de autorización a la vista. |
| 7 | Flexibilidad y eficiencia | Filtros rápidos en Flota, selector de destino en cuadrícula, "Restablecer" para empezar de nuevo, la acción de cada error resuelve y vuelve a verificar en un clic. |
| 8 | Diseño estético y minimalista | Una tarea por vista; el detalle (ranking, leyenda del mapa) queda en tarjetas secundarias. |
| 9 | Reconocer, diagnosticar y recuperarse de errores | Cada error: título claro, causa concreta (zona y hora, peso y límite, estación sin refrigeración), "no se reservó nada" y una salida: reprogramar, dividir el envío, enviar sin frío, elegir otro destino, reintentar. |
| 10 | Ayuda y documentación | Texto de ayuda contextual en el formulario y la leyenda del mapa; no hace falta manual. |

## Prueba con un compañero (sin instrucciones)

**Protocolo** (15 min, una persona que no haya visto el sistema):

1. Abrir el prototipo en la vista Red con la identidad ECI. Decir solo: *"Eres la coordinadora de la ECI"*.
2. Pedirle, una a la vez, sin explicar la interfaz:
   - T1: "¿Cuántos drones están volando ahora en la UNAL?"
   - T2: "Muéstrame los drones con fallo."
   - T3: "Envía un equipo de 1,2 kg a Uniandes y déjalo volando."
   - T4: "Envía una muestra de laboratorio a Uniandes." *(provoca sin estación con frío)*
   - T5: "Envía un paquete de 18 kg a Uniandes." *(provoca demasiado pesado)*
   - T6: "Envía algo a EAFIT."
   - T7: "Cambia la app a la identidad de la UNAL."
3. Observar sin ayudar. Anotar cada pregunta que haga, cada duda (> 5 s sin actuar) y si completa la tarea.
4. Regla del reto: **si pregunta algo, es un problema de Nielsen**: se registra, se corrige el prototipo y se repite la tarea.

**Registro** (completar al hacer la prueba; no se inventan resultados):

| Tarea | ¿Completó? | Tiempo | Preguntas o dudas | Heurística afectada | Corrección hecha |
|---|---|---|---|---|---|
| T1 | | | | | |
| T2 | | | | | |
| T3 | | | | | |
| T4 | | | | | |
| T5 | | | | | |
| T6 | | | | | |
| T7 | | | | | |

Participante: ____________________ · Fecha: ____________ · Observador: ____________

## Verificación técnica hecha

Recorrido automático con Playwright (Chromium) sobre el archivo: las 3 vistas, cambio ECI ↔ UNAL, selector de destino, ruta válida hasta "en vuelo" y los 6 errores (también desde la UNAL), sin errores de JavaScript y sin desbordamiento horizontal a 390 px de ancho (menú hamburguesa).
