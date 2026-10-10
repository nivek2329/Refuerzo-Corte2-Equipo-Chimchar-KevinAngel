# 13 · Estándares de producción: JaCoCo + Quality Gate de Enterprise

Proyecto: [`skycampus-enterprise`](../skycampus-enterprise).

## Evolución de los estándares

| Métrica | MVP (Chimchar) | v2 (Monferno) | **Enterprise (Infernape)** | Dónde se controla |
|---|---|---|---|---|
| Cobertura de líneas | 80 % | 80 % (subida a 85 %) | **≥ 85 %** | Regla `check` de JaCoCo en `mvn verify` + Quality Gate |
| Cobertura de ramas | — | 70 % | **≥ 75 %** | Regla `check` de JaCoCo + Quality Gate (`branch_coverage`) |
| Bugs | 0 | 0 | **0** | Quality Gate |
| Vulnerabilidades | 0 | 0 | **0** | Quality Gate |
| Deuda técnica | 0 | < 30 min | **< 15 min** | Quality Gate (`sqale_index`) |
| Duplicación | — | < 5 % | **< 3 %** | Quality Gate |

En Monferno se vio que "Sonar way" solo evalúa el código nuevo y no tiene condición de ramas. Por eso Enterprise usa un **Quality Gate propio sobre el código total**, con los 6 indicadores.

## 1. JaCoCo en el build

En el `pom.xml` JaCoCo 0.8.15 hace `prepare-agent` (instrumenta las pruebas), `report` en la fase `test` y `check` en `verify` con **LINE ≥ 0.85 y BRANCH ≥ 0.75**: si baja de ahí, `mvn verify` termina en `BUILD FAILURE`. Solo se excluye `SkyCampusEnterpriseApplication` (el `main` de Spring; también en `sonar.coverage.exclusions`). Medición previa a la subida: **129 pruebas, 99,8 % de líneas y 95,0 % de ramas**.

## 2. Quality Gate "SkyCampus Enterprise"

[`crear_quality_gate.cmd`](crear_quality_gate.cmd) lo crea por la API de SonarQube y lo asigna al proyecto `skycampus-enterprise`:

| Condición (código total) | Falla si |
|---|---|
| Coverage | < 85 % |
| Condition coverage (ramas) | < 75 % |
| Bugs | > 0 |
| Vulnerabilities | > 0 |
| Technical debt | > 15 min |
| Duplicated lines | > 3 % |

Si el servidor está en el modo *Multi-Quality Rule*, el script usa las métricas equivalentes (`software_quality_reliability_issues`, `software_quality_security_issues`, `software_quality_maintainability_remediation_effort`).

## 3. Cómo se ejecuta (desde cmd)

```
docker start sonarqube
set SONAR_TOKEN=<token de análisis global, solo en la variable>
cd Infernape\13*
crear_quality_gate.cmd
cd ..\skycampus-enterprise
mkdir "..\13 · JaCoCo — Cobertura de código\evidencia"
mvn clean verify sonar:sonar > "..\13 · JaCoCo — Cobertura de código\evidencia\mvn_verify_sonar.txt" 2>&1
```

Después, en `http://localhost:9000/dashboard?id=skycampus-enterprise`, guardar en `evidencia/` la captura del Quality Gate (`QualityGate_Enterprise.png`) y la de *Overall Code*.

## 4. Issues encontrados y cómo se corrigieron

Regla del reto: corregir la causa con refactorización o prueba, **nunca suprimir el issue**.

| Issue | Causa raíz | Corrección | Commit |
|---|---|---|---|
| `EtapaRuta.validarDatos` con complejidad ciclomática 13 (> 10) | 10 condiciones encadenadas en un solo `if` | Se extrajo `finitos(...)` y `enRango(...)`; CCN máximo del proyecto: 9 | commit `feat(infernape-12)` de esta entrega |
| `@SuppressWarnings("java:S107")` en el constructor del `AsignadorMision` | Se había silenciado la regla de muchos parámetros | Se quitó: con 6 puertos no supera el umbral de la regla (7); no se suprime nada | mismo commit |
| Getters sin uso en `SedeEntidad` y `AsignacionEntidad` | Código muerto que bajaba la cobertura | Eliminados | mismo commit |
| *Pendiente de completar con el análisis* | Lo que reporte SonarQube en la primera ejecución | | |

## 5. Resultado del Quality Gate

*Completar tras la ejecución del paso 3* (no se inventan cifras):

| Indicador | Valor medido | Estado |
|---|---|---|
| Cobertura de líneas | | |
| Cobertura de ramas | | |
| Bugs | | |
| Vulnerabilidades | | |
| Deuda técnica | | |
| Duplicación | | |
