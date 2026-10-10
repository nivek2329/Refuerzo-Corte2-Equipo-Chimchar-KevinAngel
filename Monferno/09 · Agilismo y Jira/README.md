# 09 · Agilismo y Jira — Sprint 1 de Monferno

## Objetivo del sprint

Entregar el flujo mínimo para asignar misiones con seguridad y dar visibilidad al estado y desempeño de la flota. Las cinco historias se planificaron en el Sprint 1 de Jira, con estimación Fibonacci total de **19 puntos**, bajo la velocidad objetivo de 20. El sprint permanece **sin iniciar**.

## Historias seleccionadas

| Jira | Historia | Puntos |
|---|---|---:|
| [SCRUM-9](https://laboratorio3dows.atlassian.net/browse/SCRUM-9) | Asignar automáticamente el drone óptimo a la misión | 8 |
| [SCRUM-10](https://laboratorio3dows.atlassian.net/browse/SCRUM-10) | Validar condiciones meteorológicas antes de autorizar vuelo | 5 |
| [SCRUM-11](https://laboratorio3dows.atlassian.net/browse/SCRUM-11) | Notificar cambios de estado al panel y al log | 3 |
| [SCRUM-12](https://laboratorio3dows.atlassian.net/browse/SCRUM-12) | Diagnosticar drones y devolver los reparados a la flota | 2 |
| [SCRUM-13](https://laboratorio3dows.atlassian.net/browse/SCRUM-13) | Consultar estadísticas de misiones por tipo de drone | 1 |

Cada HU tiene dos criterios de aceptación en Gherkin. Se copian aquí para que se puedan revisar sin abrir Jira; el texto de cada HU en Jira debe ser el mismo. Debajo de cada HU se nombran las pruebas automatizadas de `skycampus-v2` que demuestran sus criterios.

### SCRUM-9 · Asignar automáticamente el drone óptimo (8 pts)

```gherkin
Escenario: misión urgente con EXPRESS apto
  DADO QUE hay 3 drones disponibles con batería >= 30 %, uno de ellos EXPRESS
  Y la solicitud es URGENTE con un paquete de 300 g
  CUANDO el sistema ejecuta la asignación automática
  ENTONCES selecciona el drone EXPRESS
  Y el drone queda EN_VUELO

Escenario: paquete que ningún drone puede llevar
  DADO QUE el drone más grande de la flota (CARGO) soporta 2000 g
  CUANDO llega una solicitud con un paquete de 2001 g
  ENTONCES la asignación se rechaza con el mensaje "supera la capacidad del drone más grande (2000 g)"
  Y no se consulta la API meteorológica
```
Pruebas: `asignar_misionUrgente_asignaExpress`, `asignar_misionNormalClimaApto_droneQuedaEnVuelo`, `asignar_pesoUnGramoSobreElMaximo_lanzaExcepcion`, `asignar_paqueteMuyPesado_lanzaExcepcionSinConsultarClima`.

### SCRUM-10 · Validar condiciones meteorológicas antes de autorizar el vuelo (5 pts)

```gherkin
Escenario: clima apto
  DADO QUE la API meteorológica reporta condiciones aptas
  Y la solicitud es NORMAL
  CUANDO el sistema ejecuta la asignación
  ENTONCES asigna el drone apto con mayor batería

Escenario: clima adverso
  DADO QUE la API meteorológica reporta condiciones no aptas
  CUANDO el sistema ejecuta la asignación
  ENTONCES no asigna ningún drone
  Y no envía ninguna notificación de cambio de estado
```
Pruebas: `asignar_misionNormalClimaApto_asignaMayorBateria`, `asignar_climaAdverso_vacioSinNotificar`.

### SCRUM-11 · Notificar cambios de estado al panel y al log (3 pts)

```gherkin
Escenario: drone despega
  DADO QUE el PanelOperador, el SistemaLog y la AlertaTecnico están suscritos
  CUANDO un drone pasa de DISPONIBLE a EN_VUELO
  ENTONCES el PanelOperador y el SistemaLog reciben el cambio
  Y la AlertaTecnico no genera orden

Escenario: drone en FALLO
  DADO QUE los tres observadores están suscritos
  CUANDO un drone pasa a FALLO
  ENTONCES los tres reciben el aviso y se crea la orden para el técnico
```
Pruebas: `cambiarEstado_enVuelo_notificaPanelYLogSinOrdenTecnico`, `cambiarEstado_fallo_notificaALosTres`.

### SCRUM-12 · Diagnosticar drones y devolver los reparados a la flota (2 pts)

```gherkin
Escenario: ciclo de reparación
  DADO QUE un drone está en FALLO
  CUANDO el técnico lo pasa a MANTENIMIENTO y después a DISPONIBLE
  ENTONCES cada transición se acepta y el drone vuelve a ser elegible

Escenario: transición no permitida
  DADO QUE un drone está en FALLO
  CUANDO se intenta ponerlo EN_VUELO sin pasar por MANTENIMIENTO
  ENTONCES el sistema rechaza la transición
  Y no notifica a ningún observador
```
Pruebas: `puedePasarA_cadaEstado_respetaElCiclo`, `transicionarA_falloAEnVuelo_lanzaExcepcion`, `cambiarEstado_transicionInvalida_lanzaExcepcionSinNotificar`.

### SCRUM-13 · Consultar estadísticas de misiones por tipo de drone (1 pt)

```gherkin
Escenario: misiones del día
  DADO QUE hoy hay misiones entregadas por drones MINI, CARGO y EXPRESS
  CUANDO el administrador consulta las completadas por tipo
  ENTONCES obtiene el conteo de entregadas por cada tipo

Escenario: sin misiones
  DADO QUE no hay misiones registradas
  CUANDO el administrador consulta las completadas por tipo
  ENTONCES obtiene un resultado vacío, sin error
```
Pruebas: `completadasPorTipo_misionesDelDia_cuentaEntregadasPorTipo`, `completadasPorTipo_listaVacia_mapaVacio`.

## Definition of Done del equipo

Una historia está terminada solo si cumple **todos** estos puntos:

1. Sus 2 criterios Gherkin pasan como pruebas automatizadas (se nombran en la HU de Jira).
2. Pruebas unitarias en verde y JaCoCo con **≥ 85 % de líneas y ≥ 70 % de ramas** (`mvn verify` falla si no se cumple).
3. SonarQube: **0 bugs, 0 vulnerabilidades**, Quality Gate en Passed.
4. Código revisado en un PR por **al menos otro miembro** del equipo.
5. Integrada a `develop` con el flujo GitFlow (rama `feature/*` → PR → merge, sin push directo).
6. Si el RF cambió, se actualizan el diagrama de contexto C4 y la plantilla DOSW.
7. La HU en Jira está en "Hecho" y enlaza el commit o PR.

## Gestión y seguimiento

- Tablero: [DOSW_LAB3_KGR — Backlog](https://laboratorio3dows.atlassian.net/jira/software/projects/SCRUM/boards/1/backlog).
- Las cinco historias están en `SCRUM Sprint 1`, en estado **Por hacer**.
- Planificación: **19 / 20 puntos**; se reserva un punto de margen para incertidumbre.
- No se asignaron responsables ni fechas porque el material de Monferno no especifica integrantes disponibles ni calendario del sprint.
- Antes de iniciar el sprint, el equipo debe acordar fechas y revisar que todos aceptan objetivo, capacidad y Definition of Done.
