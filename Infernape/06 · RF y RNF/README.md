# 06 · Matriz de trazabilidad de SkyCampus Enterprise

Ocho RF (RF-10 a RF-17) y cuatro RNF (RNF-09 a RNF-12), numerados a continuación de los de Monferno (RF-04…RF-09, RNF-04…RNF-08). Cada fila se puede seguir hasta un caso de uso del [reto 10](../10%20·%20Casos%20de%20Uso), una historia de usuario y una prueba automatizada de [`skycampus-enterprise`](../skycampus-enterprise) que hoy pasa en `mvn verify`.

> **HU en Jira:** los identificadores HU-E01…HU-E12 son los que se crean en el tablero del equipo (épica *SkyCampus Enterprise*). El texto de cada HU está en la sección final para copiarlo tal cual.

## Términos que quitan la ambigüedad

| Término | Definición usada en todos los requisitos |
|---|---|
| Sede | Una de las 4 universidades de la red (ECI, UNAL, UNIANDES, EAFIT), identificada por su código. |
| Sede activa | Sede que el superadmin no ha suspendido. Una sede inactiva no crea misiones nuevas; las que ya están en vuelo terminan. |
| Radio configurado | Distancia máxima en km, medida desde la sede, que el coordinador fija para sus drones. |
| Radio efectivo | `min(radio configurado, radio autorizado por la Aerocivil)`. Es el único que usa el sistema para volar. |
| Zona urbana | Todo el espacio sobre Bogotá y Medellín entre sedes; ahí la altura máxima es 120 m. |
| Peso del paquete | Se recibe en gramos por la API y se compara en kg contra la capacidad del drone. |
| Rechazo con motivo | Respuesta `422` con uno de los 5 motivos: `SEDE_INACTIVA`, `CLIMA_ADVERSO`, `AEROCIVIL_RECHAZA`, `SIN_DRONES_DISPONIBLES`, `PAQUETE_DEMASIADO_PESADO`. |

## Requisitos funcionales

| Código | Nombre | MoSCoW | CU (paquete) | HU | Prueba que lo valida |
|---|---|---|---|---|---|
| RF-10 | Registrar una misión inter-sede por la API (`POST /api/v3/misiones`) | Must | Crear misión (Gestión de Misiones) | HU-E01 | `MisionControllerIntegracionTest.crearMision_climaApto_retornaCreatedConDroneAsignado`, `FlujoMisionE2ETest.crearMision_persisteLaAsignacion_sacaElDroneDeLaFlotaYNotifica` |
| RF-11 | Asignar automáticamente el drone de la sede según la prioridad (URGENTE solo EXPRESS; resto, mayor batería) | Must | Asignar drone (Gestión de Flota) | HU-E02 | `AsignadorMisionTest.asignar_climaAptoYDroneElegido_registraYNotifica`, `EstrategiaMayorBateriaTest.urgente_soloConsideraExpressAunqueHayaUnNormalConMasBateria`, `MisionControllerIntegracionTest.crearMision_urgente_asignaElExpress` |
| RF-12 | El coordinador configura el radio máximo de vuelo de los drones de su sede | Should | Configurar sede (Administración) | HU-E03 | `RestriccionesVueloTest.rf12_radioConfiguradoMayorQueElDeAerocivil_seAplicaElDeAerocivilYSeAvisa`, `RestriccionesVueloTest.rf12_radioConfiguradoMenor_seRespetaLaDecisionDelCoordinador` |
| RF-13 | Autorizar cada ruta inter-sede con la Aerocivil antes de reservar un drone | Must | Autorizar con Aerocivil (Rutas y Navegación) | HU-E04 | `AsignadorMisionFlujosAlternosTest.flujoAlterno3_aerocivilRechaza_noConsultaLaFlota`, `MisionControllerIntegracionTest.flujoAlterno_aerocivilRechaza_retorna422` |
| RF-14 | Planificar una ruta multi-etapa con estaciones de carga (SC-15: máx. 5 km con carga sin recargar, máx. 30 min en estación) | Must | Calcular ruta multi-etapa (Rutas y Navegación) | HU-E05 | `PatronesRutaTest.gestorEjecutaRutaCompuestaYNotificaCadaEtapa`, `ReglasRutaMultiEtapaTest` (5 pruebas: RN-1 y RN-2) |
| RF-15 | Rechazar una misión no viable explicando el motivo (sede inactiva, clima, sin drones, peso) | Must | Crear misión «extend» Rechazar misión (Gestión de Misiones) | HU-E06 | `AsignadorMisionFlujosAlternosTest` (flujos 1, 2, 4, 4b, 5), `MisionControllerIntegracionTest.flujoAlterno_*` |
| RF-16 | Calcular la eficiencia por sede (tasa de éxito, tiempo promedio, drone más usado, % urgentes) | Should | Ver analytics (Gestión de Flota) | HU-E07 | `AnalyticsEficienciaRedTest.calcular_escenariosEnterprise` (5 escenarios) |
| RF-17 | Generar los reportes de cada sede en su formato (ECI HTML, UNAL PDF, Uniandes JSON, EAFIT CSV) | Could | Generar reportes (Administración) | HU-E08 | `FabricaReportesSedeTest` (10 pruebas) |

**Won't (esta versión):** *Transferir drone entre sedes* y *Gestionar usuarios* aparecen en el diagrama de CU como alcance del sistema, pero no entran en la v3.1; no tienen RF en esta matriz ni código.

## Requisitos no funcionales

| Código | Atributo | Requisito medible | MoSCoW | HU | Prueba / control |
|---|---|---|---|---|---|
| RNF-09 | Cumplimiento regulatorio | Las rutas inter-sede respetan el espacio aéreo de la Aerocivil: radio autorizado por sede y altura ≤ 120 m en zona urbana. Se cumple aunque el coordinador configure otra cosa. | Must | HU-E09 | `RestriccionesVueloTest.rnf09_tramoDentroDelRadioEfectivoYBajo120mEnZonaUrbana` (5 casos) |
| RNF-10 | Rendimiento | Con 100 drones en la sede, una asignación responde en < 500 ms. | Should | HU-E10 | `AsignadorMisionRendimientoTest.cienDrones_asignaEnMenosDe500ms` |
| RNF-11 | Mantenibilidad / portabilidad | `domain` solo importa el JDK; `application` solo JDK y `domain`. Cambiar JPA o el cliente HTTP no toca la lógica. | Must | HU-E11 | `ArquitecturaCapasTest.cadaCapaSoloImportaLoPermitido` (2 casos) |
| RNF-12 | Calidad verificable | Cobertura ≥ 85 % de líneas y ≥ 75 % de ramas; 0 bugs y 0 vulnerabilidades en SonarQube. | Must | HU-E12 | Regla `check` de JaCoCo en `mvn verify` (falla el build) y Quality Gate del [reto 13](../13%20·%20JaCoCo%20—%20Cobertura%20de%20código) |

## Comprobación de trazabilidad

| RF | ¿Tiene CU? | ¿Tiene HU? | ¿Tiene prueba en verde? |
|---|---|---|---|
| RF-10 … RF-17 | 8/8 | 8/8 | 8/8 |
| RNF-09 … RNF-12 | 4/4 (restricciones sobre los CU de Rutas, Flota y todo el sistema) | 4/4 | 4/4 |

Ningún RF queda sin CU ni sin prueba. Las pruebas citadas existen con ese nombre exacto en `src/test/java`.

## Contradicciones detectadas y cómo se resuelven

### 1. RF-12 (coordinador fija el radio) vs RNF-09 (Aerocivil fija el espacio aéreo)

**Tensión.** Si el coordinador de la ECI configura 12 km y la Aerocivil autoriza 8 km, obedecer a RF-12 viola RNF-09.

**Resolución (implementada en `ConfiguracionVueloSede`):**
1. El valor del coordinador **se guarda tal cual** (es su intención y la auditoría lo necesita).
2. El sistema vuela con el **radio efectivo** = `min(configurado, Aerocivil)`; la altura en zona urbana nunca supera 120 m.
3. `recortadaPorAerocivil()` devuelve `true` y la pantalla *Configurar sede* le avisa al coordinador: "Su radio de 12 km se limita a 8 km por la Aerocivil".

Así RF-12 queda reformulado sin ambigüedad: *el coordinador configura el radio deseado; el sistema aplica el menor entre ese valor y el autorizado por la Aerocivil*. Pruebas: `rf12_radioConfiguradoMayorQueElDeAerocivil_seAplicaElDeAerocivilYSeAvisa` y `rnf09_tramoDentroDelRadioEfectivoYBajo120mEnZonaUrbana`.

### 2. RF-11 (asignar de inmediato) vs RF-13 (esperar autorización de la Aerocivil)

**Tensión.** Si se reserva el drone y después la Aerocivil rechaza, el drone queda bloqueado sin misión.

**Resolución:** el orden del caso de uso es *sede activa → clima → Aerocivil → flota → capacidad → estrategia*. Ningún drone se reserva antes de la autorización. Prueba: `flujoAlterno3_aerocivilRechaza_noConsultaLaFlota` verifica que el repositorio no se toca.

### 3. RF-14 regla RN-1 (5 km con carga) vs la estrategia "ruta más rápida" (reto 03)

**Tensión.** La ruta directa ECI → Uniandes (9,5 km) es la más rápida, pero rompe RN-1 si lleva carga.

**Resolución:** en SC-15 el paso *calcular etapas* filtra con `ReglasRutaMultiEtapa` **antes** de que la estrategia elija; la estrategia solo compara alternativas válidas. Prueba: `rn1_tramoConCargaDeMasDe5Km_seReporta`.

### 4. RF-15 (sede inactiva rechaza misiones) vs misiones ya en vuelo

**Tensión.** Suspender una sede podría interpretarse como abortar sus vuelos en curso.

**Resolución:** "sede inactiva" solo bloquea la **creación** de misiones (definición en la tabla de términos). La verificación ocurre únicamente en `evaluar`, al crear. Prueba: `flujoAlterno1_sedeInactiva_rechazaSinConsultarNadaMas`.

## Texto de las HU para Jira

| HU | Historia |
|---|---|
| HU-E01 | Como **solicitante** quiero registrar una misión entre sedes indicando destino, peso y prioridad para que el sistema la despache sin intervención manual. |
| HU-E02 | Como **operador de sede** quiero que el sistema elija el drone según la prioridad para que las urgentes salgan en un EXPRESS y las demás con el drone de más batería. |
| HU-E03 | Como **coordinador de sede** quiero configurar el radio máximo de vuelo de mis drones para controlar su alcance, sabiendo cuándo la Aerocivil lo limita. |
| HU-E04 | Como **coordinador de sede** quiero que cada ruta inter-sede pase por la Aerocivil antes de reservar un drone para no bloquear flota en rutas no autorizadas. |
| HU-E05 | Como **operador** quiero planificar rutas con estaciones de carga intermedias para cubrir trayectos largos sin superar 5 km con carga ni dejar paquetes más de 30 min en estación. |
| HU-E06 | Como **solicitante** quiero saber exactamente por qué no se pudo despachar mi misión para decidir si reprogramo o cambio el paquete. |
| HU-E07 | Como **superadmin** quiero ver la eficiencia de cada sede para comparar su desempeño. |
| HU-E08 | Como **coordinador de sede** quiero los reportes en el formato que usa mi universidad para integrarlos a mis sistemas. |
| HU-E09 | Como **Aerocivil** (actor externo) necesito que ningún vuelo supere 120 m en zona urbana ni salga del radio autorizado. |
| HU-E10 | Como **operador** quiero que la asignación responda en menos de medio segundo aunque la sede tenga 100 drones. |
| HU-E11 | Como **equipo de desarrollo** queremos que el dominio no dependa de frameworks para cambiar persistencia o clientes externos sin reescribir la lógica. |
| HU-E12 | Como **equipo de desarrollo** queremos que el build falle si la cobertura baja de 85 %/75 % para no degradar la calidad entre sprints. |
