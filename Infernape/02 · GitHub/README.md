# 02 · GitFlow de Enterprise: release, hotfix y tags

Todo se hizo en el repositorio del equipo, con commits convencionales (`feat`, `fix`, `test`, `chore`, `docs`).

## Release v3.0.0

| Paso | Commit |
|---|---|
| `develop` integra la base Enterprise | `e1001ac feat(infernape): integrar base enterprise a develop` |
| Se crea `release/v3.0.0` desde `develop` | — |
| Preparación 1 | `803fdb3 chore(release): bump Enterprise to 3.0.0` |
| Preparación 2 | `4062c96 docs(release): document Enterprise v3.0.0` ([notas](RELEASE_NOTES_v3.0.0.md)) |
| Merge `--no-ff` a `main` + tag anotado `v3.0.0` | `77d2b06 feat(release): merge v3.0.0 into main` |
| `develop` recibe la release | avanza hasta `77d2b06` |

## Hotfixes

| Versión | Rama | Qué corrige |
|---|---|---|
| v3.0.1 | `hotfix/ruta-inter-sede-unal` | Agregó la proyección del viento de cola/frente ([detalle](HOTFIX_v3.0.1.md)). En la revisión se vio que era una funcionalidad nueva y no la corrección de un defecto existente. |
| v3.0.2 | `hotfix/deriva-viento-cruzado` | Defecto real de v3.0.1: el viento cruzado no afectaba el tiempo estimado. Prueba en rojo, corrección en verde, bump a 3.0.2 ([detalle](HOTFIX_v3.0.2.md)). |

Cada hotfix sale de `main`, vuelve a `main` con merge `--no-ff` y tag anotado, y se integra a `develop` para que la siguiente versión no reabra el defecto. La rama de trabajo `Infernape` recibe `develop` después del hotfix.

## Evidencia

- [`evidencia_git_log_graph.txt`](evidencia_git_log_graph.txt): `git log --graph` final de `main`, `develop`, `Infernape` y los tags.
- [`evidencia/hotfix_v3.0.2_red.txt`](evidencia/hotfix_v3.0.2_red.txt) y [`evidencia/hotfix_v3.0.2_green.txt`](evidencia/hotfix_v3.0.2_green.txt): Maven antes y después de la corrección.
