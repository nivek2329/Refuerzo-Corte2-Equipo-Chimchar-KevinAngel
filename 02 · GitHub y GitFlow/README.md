# 02 · GitHub y GitFlow

El reto pide un repositorio propio, así que el trabajo vive en uno aparte:
**[SkyCampus-ECI-Angel](https://github.com/nivek2329/SkyCampus-ECI-Angel)**.

## Lo que pide el reto y dónde está

| Requisito | Evidencia en SkyCampus-ECI-Angel |
|---|---|
| Ramas `main` y `develop` | Ambas existen; `develop` se creó desde `main` |
| Rama `feature/[Apellido]-modelo-flota` desde `develop` | `feature/Angel-modelo-flota` |
| Records `Drone`, `Mision`, `TipoCarga`, `EstadoMision` en 2 commits descriptivos | `feat: agrega record Drone con campos id, bateria, disponible y ubicacion` y `feat: agrega record Mision con enums TipoCarga y EstadoMision` |
| Código de Streams del reto 01 en un tercer commit | `feat: agrega consultas con Streams sobre la flota (reto 01)` |
| Merge a `develop` (nunca directo a `main`) | Fast-forward inicial y luego `merge --no-ff`: `merge: integra refactor de ConsultasFlota en develop` |

## Historial (`git log --oneline --graph --all`)

```text
    *   c24327b merge: integra refactor de ConsultasFlota en develop
    |\  
    | * 2f5cfc8 refactor: ConsultasFlota con nombres descriptivos, constructor privado y Stream.toList()
    |/  
    * c3a1535 feat: agrega consultas con Streams sobre la flota (reto 01)
    * 98cc6cb feat: agrega record Mision con enums TipoCarga y EstadoMision
    * e1daac2 feat: agrega record Drone con campos id, bateria, disponible y ubicacion
    * f094226 Initial commit
```
