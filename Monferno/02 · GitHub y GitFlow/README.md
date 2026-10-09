# 02 · GitFlow — conflicto de asignación y alertas

Esta evidencia reproduce el escenario del reto en un repositorio temporal aislado; no cambia las ramas ni los archivos de trabajo del repositorio del equipo y no hace push.

## Escenario

- `develop` es la base común.
- `feature/asignacion-automatica` cambia el resultado de `asignar` para bloquear el vuelo si el clima no es apto.
- `feature/alertas-estado` cambia ese mismo punto para notificar el nuevo estado.
- Se integran ambas ramas a `develop`. Los cambios de negocio son distintos, pero ambos reemplazan la misma línea de retorno, así que Git marca el conflicto. La resolución calcula el estado con la validación meteorológica, lo notifica y devuelve el mismo resultado.

La segunda feature es conceptualmente independiente, aunque ambas concurren sobre la misma línea de `AsignadorMision.java`: el conflicto ilustra por qué dos cambios funcionales distintos pueden requerir coordinación al tocar el mismo fragmento.

## Evidencia reproducible

Ejecutar desde PowerShell:

```powershell
cd 'Monferno\02 · GitHub y GitFlow'
.\simular_gitflow.ps1
```

El script crea el repo de simulación como carpeta temporal dentro de este directorio, provoca y resuelve el conflicto, y escribe aquí `evidencia_git_log_graph.txt` y `evidencia_merge_resuelto.diff`. Elimina el repo temporal al terminar. Las confirmaciones pertenecen solo al repo temporal; la rama del equipo permanece intacta.
