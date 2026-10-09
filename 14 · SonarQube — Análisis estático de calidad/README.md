# 14 · SonarQube — Análisis estático de calidad

Proyecto analizado: `../12 · TDD` (clave `skycampus-eci`; en SonarQube aparece con el nombre del artefacto, `skycampus-tdd`). Servidor local SonarQube Community Build v26.9, perfil de calidad **Sonar way**.

## Cómo ejecutarlo

1. Levantar SonarQube: `docker start sonarqube` (o `docker run -d --name sonarqube -p 9000:9000 sonarqube:lts-community` la primera vez).
2. En `http://localhost:9000` → *My Account → Security*, generar un **Global Analysis Token** y guardarlo solo como variable de entorno local `SONAR_TOKEN` (nunca en el repositorio).
3. Desde `12 · TDD`: `mvn clean verify sonar:sonar`

## Antes y después

- **Antes:** código del commit GREEN `a470467`, todavía en el paquete por defecto. Se analizó desde una copia exacta de ese commit con el mismo `pom.xml`.
- **Después:** código actual (`HEAD`), con el paquete `edu.eci.skycampus` y los casos límite.

| Momento | Seguridad | Confiabilidad | Mantenibilidad | Code smells | Deuda técnica | Cobertura | Duplicación | Quality Gate |
|---|---|---|---|---|---|---|---|---|
| Antes (GREEN `a470467`) | A · 0 | A · 0 | A · 4 issues | 4 | 40 min | 88,9 % | 0,0 % | Passed |
| Después (`HEAD`) | A · 0 | A · 0 | A · 0 issues | **0** | **0 min** | **100 %** | 0,0 % | Passed |

## Code smells corregidos

| # | Regla | Archivo (antes) | Corrección | Commit |
|---|---|---|---|---|
| 1 | `java:S1220` Move this file to a named package | `src/main/java/DestinoInvalidoException.java` | Movido a `edu.eci.skycampus` | `07731d2` refactor(12) |
| 2 | `java:S1220` Move this file to a named package | `src/main/java/Drone.java` | Movido a `edu.eci.skycampus` | `07731d2` refactor(12) |
| 3 | `java:S1220` Move this file to a named package | `src/main/java/ValidadorMision.java` | Movido a `edu.eci.skycampus` | `07731d2` refactor(12) |
| 4 | `java:S1220` Move this file to a named package | `src/test/java/ValidadorMisionTest.java` | Movido a `edu.eci.skycampus` | `07731d2` refactor(12) |

La cobertura pasó de 88,9 % a 100 % con las pruebas de casos límite y `DroneTest` (commit `dfa9700`), que cubren las validaciones del constructor de `Drone` que antes no tenían prueba.

## Evidencias

| Captura | Contenido |
|---|---|
| `Sonar_Antes.png` | Overview del análisis "antes": 4 issues de mantenibilidad, 88,9 % de cobertura |
| `Sonar_Issues_Antes_1.png`, `Sonar_Issues_Antes_2.png` | Los 4 issues `S1220` (40 min de esfuerzo) |
| `Sonar_Despues.png` | Overview del análisis "después": 0 issues, 100 % de cobertura, Quality Gate *Passed* |
| `Sonar_Issues_Despues.png` | "No Issues. Hooray!": 0 issues, 0 esfuerzo |
