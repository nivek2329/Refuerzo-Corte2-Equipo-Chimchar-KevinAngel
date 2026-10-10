# 02 · GitFlow — conflicto entre asignación automática y alertas

La simulación corre en un repositorio temporal que el script crea y borra; no toca las ramas del equipo ni hace push.

## Escenario

| Rama | Autor simulado | Qué cambia en `AsignadorMision.asignar` |
|---|---|---|
| `feature/asignacion-automatica` | Juan | **Selección**: bloquea si el clima no es apto y elige la estrategia según la prioridad (líneas 3–4) |
| `feature/alertas-estado` | María | **Cambio de estado**: lo hace por `GestorFlota` para notificar a los observadores (línea 4 original) |

Las dos ramas salen del mismo commit de `develop` y modifican partes diferentes del método: una la selección del drone y otra la notificación. Son sentencias contiguas, y Git considera conflicto cualquier par de cambios que se tocan aunque no editen la misma instrucción. Por eso el segundo merge se detiene.

## Resolución

En `evidencia_conflicto.txt` se ven las marcas `<<<<<<< HEAD` / `=======` / `>>>>>>> feature/alertas-estado`. La resolución conserva **las dos líneas de Juan** (clima + estrategia por prioridad) y **la línea de María** (`gestorFlota.cambiarEstado`), borra las marcas y cierra el merge con `merge: integra asignacion automatica y sistema de alertas`.

Después se libera `develop` en `main` y se etiqueta `v2.0.0` ("SkyCampus v2: flota autonoma, 3 tipos de drone, alertas").

## Por qué merge y no rebase

Las features ya estarían publicadas para el equipo; un merge conserva la historia y deja visible dónde convergen. Rebase solo se usaría en una rama local que nadie ha descargado.

## Evidencia reproducible

Desde cmd, en esta carpeta:

```
powershell -ExecutionPolicy Bypass -File simular_gitflow.ps1
```

El script valida que el conflicto realmente ocurra (si el merge fuera automático, falla), y escribe:

- `evidencia_conflicto.txt`: `git status` con `UU AsignadorMision.java` y el archivo con las marcas.
- `evidencia_merge_resuelto.diff`: el commit de merge y su diff combinado.
- `evidencia_git_log_graph.txt`: `git log --graph --all` con las dos ramas convergiendo en `develop` y el tag `v2.0.0` en `main`.
