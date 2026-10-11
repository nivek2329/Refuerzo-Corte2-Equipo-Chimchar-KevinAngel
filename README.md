# Refuerzo Corte 2 · Equipo Chimchar — SkyCampus MVP

Ejercicios de refuerzo de **DOSW 2026-2** (ECI) en el nivel **Chimchar**: el MVP de SkyCampus, un sistema de reparto con drones dentro del campus.

- **Equipo:** Kevin · Angel
- **Rama de trabajo:** `Chimchar`
- **Alcance:** 5 drones DJI Mini 3, destinos fijos (Bloque A, B, C, D y Biblioteca), carga `SOBRE` / `CARPETA` / `LIBRO` y asignación manual por un operador.

## Modelo de dominio (Chimchar)

```text
Drone(id:String, modelo:String, bateria:int, disponible:boolean, ubicacion:String)
Mision(id:String, drone:Drone, origen:String, destino:String, tipoCarga:TipoCarga, estado:EstadoMision)
TipoCarga: Enum(SOBRE, CARPETA, LIBRO)      EstadoMision: Enum(PENDIENTE, EN_VUELO, ENTREGADA, FALLIDA)
Regla de asignación: drone disponible y batería ≥ 30 %
```

## Retos

| # | Reto | Carpeta / evidencia | Estado |
|---|---|---|---|
| 01 | Streams & Lambdas | `01 · Streams & Lambdas/` — 4 consultas con Streams (`ConsultasFlota`) | Revisado |
| 02 | GitHub y GitFlow | Repo aparte [SkyCampus-ECI-Angel](https://github.com/nivek2329/SkyCampus-ECI-Angel): `main` / `develop` / `feature/Angel-modelo-flota`, commits descriptivos y merge a `develop` | Completado |
| 03 | Patrones de diseño | `03 · Patrones de Diseño/` — Builder (`MisionBuilder`), Chain of Responsibility (`CadenaValidacionMision`) y Strategy (`AsignacionPorMayorBateria`) | Revisado |
| 04 | Principios SOLID | `04 · Principios SOLID/` — `GestorDrone` dividido en `AsignadorMision`, `RepositorioMision`, `AlertaOperador`, `GeneradorReporte` y `EstrategiaRuta` | Revisado |
| 05 | Diagrama de contexto C4 | `05 · Diagrama de Contexto C4/` (`.drawio` + `.svg`) | Completado |
| 06 | RF vs RNF y MoSCoW | `06 · RF vs RNF y Prioridad MoSCoW/` — 3 RF, 3 RNF medibles | Completado |
| 07 | Plantilla DOSW | `07 · Plantilla DOSW/SC-01_Registrar_mision_DOSW.docx` | Completado |
| 08 | Manual de identidad y UX/UI | `08 · Manual de Identidad y UXUI/` — manual, mock con IA y verificación de 7 heurísticas de Nielsen | Revisado |
| 09 | Agilismo y Jira | `09 · Agilismo y Jira/` — épica, feature, 3 HU, subtareas, criterios y capturas de Jira | Completado |
| 10 | Diagrama de casos de uso | `10 · Diagramas de Casos de Uso/` — SC-01 con `<<include>>` y `<<extend>>` | Completado |
| 11 | Mocks con IA | `11 · Mocks con IA/` — referencias reales, prompt y 3 estados (normal, alerta, vacío) | Revisado |
| 12 | TDD | `12 · TDD/` — Maven + JUnit 5; commits RED → GREEN → REFACTOR | Revisado |
| 13 | JaCoCo | `13 · JaCoCo — Cobertura de código/` — 100 % líneas y ramas, 24 pruebas | Revisado con el 12 |
| 14 | SonarQube | `14 · SonarQube — Análisis estático de calidad/` — antes: 4 code smells, 40 min de deuda; después: 0 smells, 0 deuda, 100 % de cobertura | Completado |

"Revisado" significa que el código o el entregable pasó por el agente revisor del curso (`DOSW_Agente_Chimchar`) hasta obtener APROBADO.

## Cómo ejecutar

Cada carpeta de código es un reto independiente y compila sola. Los retos 01 a 04 usan el paquete por defecto, así que basta con `javac` (Java 17 o superior, porque usan `record`):

```bash
cd "01 · Streams & Lambdas"
javac -d out *.java
java -cp out FlotaDronesApp
```

El reto 12 es un módulo Maven (paquete `edu.eci.skycampus`):

```bash
cd "12 · TDD"
mvn clean verify     # 24 pruebas + reporte JaCoCo en target/site/jacoco/index.html (mínimo 80 %)
```

## Notas

- `Drone`, `Mision` y `TipoCarga` aparecen en varias carpetas con variantes. Cada reto agrega solo lo que su enunciado necesita; por ejemplo, el 03 añade `capacidadMaxima` para validar la carga. Las diferencias están explicadas en el README de cada carpeta.
- El prompt general del revisor menciona `EQUIPO` en `TipoCarga` y un `EstadoDrone`; no se usan porque no forman parte del enunciado Chimchar.
- Las carpetas usan los caracteres `·` y `—` en sus nombres. En Windows la ruta con `·` corrompe el archivo de datos de JaCoCo, así que el `pom.xml` del 12 lo guarda en la carpeta temporal del sistema (ver la nota técnica en el README del 13).
