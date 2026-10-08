# 14 · SonarQube — Análisis estático de calidad

Proyecto analizado: `../12 · TDD` (clave `skycampus-eci`, servidor local `http://localhost:9000`).

## Cómo ejecutarlo

1. Levantar SonarQube: `docker run -d --name sonarqube -p 9000:9000 sonarqube:lts-community` (o `docker start sonarqube` si el contenedor ya existe).
2. Entrar a `http://localhost:9000`, crear un token de análisis y guardarlo **solo** como variable de entorno local `SONAR_TOKEN` (nunca en el repositorio).
3. Desde `12 · TDD`: `mvn verify sonar:sonar`

## Resultados

| Momento | Calificación | Bugs | Vulnerabilidades | Code smells | Deuda técnica |
|---|---|---|---|---|---|
| Análisis inicial | _pendiente_ | | | | |
| Tras correcciones | _pendiente_ | | | | |

## Code smells corregidos

| # | Regla | Archivo | Corrección |
|---|---|---|---|
| 1 | _pendiente_ | | |
| 2 | | | |
| 3 | | | |

Evidencias: `Sonar_Antes.png`, `Sonar_Despues.png` — _pendientes_.
