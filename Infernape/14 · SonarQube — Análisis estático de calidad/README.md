# 14 · SonarQube — análisis de SkyCampus Enterprise

Se analiza `skycampus-enterprise` con el Quality Gate propio del [reto 13](../13%20·%20JaCoCo%20—%20Cobertura%20de%20código/README.md).

## Módulos

El enunciado nombra PlanificadorRuta, GestorZonas y MonitorFlota. En el código esas responsabilidades viven en estos paquetes:

| Módulo del enunciado | Paquetes / clases | Qué hace |
|---|---|---|
| **PlanificadorRuta** | `routing` (`GestorMisiones`, `RutaCompuesta`, `TramoRuta`, estrategias, fábricas de drones, `CalculadorRutaInterSede`, `ReglasRutaMultiEtapa`) | Rutas simples y multi-etapa, viento y deriva, reglas de SC-15 |
| **GestorZonas** | `domain`: `ConfiguracionVueloSede`, `LimitesAerocivil`, `Sede`, `RepositorioSedes` + `infrastructure.persistence.RepositorioSedesJpa` | Sedes activas, radio efectivo y límites de la Aerocivil (RF-12 / RNF-09) |
| **MonitorFlota** | `application.AsignadorMision`, `infrastructure` (flota JPA y en memoria, `RegistroNotificaciones`, controladores REST), `analytics`, `reporting` | Asignación con los 5 flujos alternos, estado de la flota, eficiencia y reportes |

## Métricas locales (antes de SonarQube)

Medidas con `lizard` sobre `src/main/java` (complejidad ciclomática por método):

| Módulo | Métodos | CCN máximo | CCN promedio |
|---|---:|---:|---:|
| PlanificadorRuta (`routing`) | 48 | 9 | 1,7 |
| GestorZonas (`domain`) | 9 | 3 | 1,6 |
| MonitorFlota (`application` + `infrastructure` + `analytics` + `reporting`) | 80 | 4 | 1,3 |

Ningún método supera **10** (el único que lo hacía, `EtapaRuta.validarDatos` con 13, se refactorizó en el reto 13). Los bloques "duplicados" que reporta `lizard` son solo listas de `import` de las fábricas de reportes; SonarQube no cuenta los imports como duplicación.

## Cómo se ejecuta

```
docker start sonarqube
set SONAR_TOKEN=<token, solo en la variable>
cd Infernape\skycampus-enterprise
mvn clean verify sonar:sonar
```

## Deuda técnica por módulo (SonarQube)

*Completar con el análisis* (pestaña **Measures → Maintainability → Technical Debt**, filtrando por carpeta):

| Módulo | Deuda | Code smells | Bugs | Vulnerabilidades | Duplicación |
|---|---|---|---|---|---|
| PlanificadorRuta | | | | | |
| GestorZonas | | | | | |
| MonitorFlota | | | | | |
| **Total** | | | | | |

## Issues críticos corregidos

| Issue (regla) | Módulo | Causa | Corrección | Commit |
|---|---|---|---|---|
| *Completar con lo que reporte SonarQube* | | | | |

## Quality Gate final

Captura `QualityGate_Enterprise_final.png` en esta carpeta, con los 6 indicadores en verde.
