# 10 · Diagrama de casos de uso — SC-01

Este diagrama documenta el requisito funcional **SC-01: Registrar misión de reparto de documento** y las interacciones del MVP Chimchar.

## Archivos

- `Diagrama_Casos_Uso_SC-01.drawio`: fuente editable en diagrams.net / draw.io.
- `Diagrama_Casos_Uso_SC-01.svg`: exportación vectorial para visualizar e incluir en la entrega.

## Alcance representado

- **Solicitante:** registra una solicitud con origen, destino y tipo de carga, y consulta su estado.
- **Operador de drones:** convierte una solicitud pendiente en misión, consulta la flota y cancela una misión pendiente.
- **Admin:** administra la flota y el catálogo de ubicaciones, que deben estar cargados para procesar SC-01.
- **Sistema SkyCampus:** genera el código único y deja la misión en estado `PENDIENTE` tras validar y asignar manualmente un drone.

## Relaciones UML

- `Registrar misión de reparto` **incluye** `Validar solicitud, destino y drone`: la validación se ejecuta siempre antes de registrar la misión; no es opcional.
- `Informar batería baja` **extiende** `Registrar misión de reparto` cuando la batería del drone seleccionado es inferior al 40%. Si además es inferior al 30%, el flujo de SC-01 rechaza la asignación; entre 30% y 39% se permite registrar y se emite el aviso. Así se conserva el umbral de aviso del ejemplo del punto 10 sin contradecir la regla de elegibilidad de SC-01.

Los casos de uso `Registrar solicitud`, `Ver flota` y `Cancelar misión` completan las funciones nombradas en el HTML y el MVP/Jira. No se representa una integración externa: el contexto de SkyCampus declara que el control de drones es local.
