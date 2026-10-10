# 12 · TDD — 3 capas de pruebas para el AsignadorMision Enterprise

Proyecto: [`skycampus-enterprise`](../skycampus-enterprise), ahora una aplicación **Spring Boot 3.3.4** con API REST, JPA y H2 en memoria. El dominio y la capa de aplicación siguen sin Spring (lo impide `ArquitecturaCapasTest`).

## La pirámide

| Capa | Qué prueba | Herramienta | Clases (casos) |
|---|---|---|---|
| **Unitarias** (base) | Lógica aislada del `AsignadorMision`, estrategias, reglas de espacio aéreo y de SC-15, rutas, reportes, analytics | JUnit 5 + Mockito, sin Spring | `AsignadorMisionFlujosAlternosTest` (7), `AsignadorMisionTest` (8), `EstrategiaMayorBateriaTest` (4), `RestriccionesVueloTest` (9), `ReglasRutaMultiEtapaTest` (5), `DroneYSolicitudTest`, `ValidacionDominioTest`, `PatronesRutaTest`, `ValidacionesRutaTest`, `CalculadorRutaInterSedeTest`, `FabricaReportesSedeTest`, `AnalyticsEficienciaRedTest`, `ArquitecturaCapasTest`, `AsignadorMisionRendimientoTest` |
| **Integración** (medio) | Endpoint `POST /api/v3/misiones` con Spring, JPA y H2; clima y Aerocivil como `@MockBean` | `@SpringBootTest` + `@AutoConfigureMockMvc` + H2 | `MisionControllerIntegracionTest` (9); `AsignadorMisionConAdaptadoresTest` y `AdaptadoresInfraestructuraTest` (capas juntas sin Spring) |
| **E2E simulado** (punta) | RF-10/RF-11 completos sin mocks: endpoint → caso de uso → JPA → H2 → notificación | MockMvc + datos sembrados (`data.sql`) | `FlujoMisionE2ETest` (2) |

Total: **129 pruebas**, todas en verde.

## Los 5 flujos alternos, cada uno con su prueba

`AsignadorMision.evaluar` devuelve un `ResultadoAsignacion` con el drone **o** el motivo exacto; la API responde `201` o `422` con ese motivo.

| Flujo alterno | Motivo | Unitaria (corta el flujo ahí) | Integración (HTTP 422) |
|---|---|---|---|
| Sede inactiva | `SEDE_INACTIVA` | `flujoAlterno1_sedeInactiva_rechazaSinConsultarNadaMas` | `flujoAlterno_sedeInactiva_retorna422` |
| Clima adverso | `CLIMA_ADVERSO` | `flujoAlterno2_climaAdverso_rechazaAntesDeLaAerocivil` | `flujoAlterno_climaAdverso_retorna422` |
| Aerocivil rechaza | `AEROCIVIL_RECHAZA` | `flujoAlterno3_aerocivilRechaza_noConsultaLaFlota` | `flujoAlterno_aerocivilRechaza_retorna422` |
| Sin drones | `SIN_DRONES_DISPONIBLES` | `flujoAlterno4_sinDronesDisponibles_rechazaSinInvocarEstrategia` (+ `4b`: la estrategia no encuentra candidato por prioridad) | `flujoAlterno_sinDronesDisponibles_retorna422` |
| Paquete pesado | `PAQUETE_DEMASIADO_PESADO` | `flujoAlterno5_paqueteDemasiadoPesado_distingueDeSinDrones` | `flujoAlterno_paqueteDemasiadoPesado_retorna422` |

Las unitarias verifican además con `verifyNoInteractions` que, al cortar, **no se consultó lo que sigue** (por ejemplo, con la Aerocivil en contra no se toca la flota, así ningún drone queda reservado).

La prueba del enunciado existe tal cual: `crearMision_climaApto_retornaCreatedConDroneAsignado` envía `{"sede":"ECI","destino":"UNAL","pesoPaquete":300,"prioridad":"NORMAL"}` y comprueba `201`, `$.droneAsignado.id` y `$.estado = EN_VUELO`. Se agregó `sede` al cuerpo porque en Enterprise hay cuatro sedes de origen.

## Cobertura (JaCoCo)

La regla `check` del `pom.xml` exige **≥ 85 % de líneas y ≥ 75 % de ramas** y hace fallar `mvn verify` si no se cumple. Medición en la última ejecución: ver [`evidencia/mvn_verify.txt`](evidencia/mvn_verify.txt) (el resumen de JaCoCo queda en `target/site/jacoco/index.html`). Antes de subirlo se midió 99,8 % de líneas y 95,1 % de ramas con las 129 pruebas. Solo se excluye la clase `SkyCampusEnterpriseApplication` (el `main` que arranca Spring).

## Cómo se ejecuta

```
cd Infernape\skycampus-enterprise
mvn clean verify
```

## Nota sobre el ciclo

En este reto no se guardó una ejecución en rojo separada: las pruebas de los 5 flujos fijan el contrato de `ResultadoAsignacion` y el orden de verificación del caso de uso. Las pruebas del reto 04 siguen pasando porque `asignar` se mantuvo como atajo de `evaluar`.
