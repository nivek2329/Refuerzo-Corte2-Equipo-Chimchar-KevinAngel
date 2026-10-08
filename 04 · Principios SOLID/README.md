# 04 · Principios SOLID — GestorDrone

## 1. Por qué cada principio está violado

**SRP (violado):** `GestorDrone` tiene cinco responsabilidades sin relación entre sí (`asignarMision`, `guardarEnBD`, `enviarAlertaEmail`, `generarReportePDF`, `calcularRuta`), así que un cambio en cualquiera de esos cinco dominios obliga a modificar la misma clase.

**OCP (violado):** `calcularRuta` decide con `if (tipo.equals("DIRECTO")) … else if (tipo.equals("EVITAR"))`, así que agregar un nuevo tipo de ruta exige modificar el método existente en vez de extenderlo con código nuevo.

**LSP (no aplica directamente):** no hay herencia en este fragmento, así que no hay una subclase que evaluar frente a su padre.

**ISP (violado en el diseño original):** un cliente que solo necesita `asignarMision` queda forzado a depender de una clase que también expone `guardarEnBD`, `enviarAlertaEmail` y `generarReportePDF`, métodos que no le interesan.

**DIP (violado):** la lógica de negocio depende de tres detalles concretos en lugar de abstracciones: `DriverManager.getConnection` con MySQL en `guardarEnBD`, SMTP directo en `enviarAlertaEmail` e iText en `generarReportePDF`; además, `"root", "1234"` quemados en el código exponen credenciales en el repositorio.

## 2. Rediseño

Cada pieza tiene una sola razón para cambiar:

| Pieza | Única razón para cambiar |
|---|---|
| `RepositorioMision` (interfaz) / `RepositorioMisionMySQL` | Cómo se persiste una misión |
| `AlertaOperador` (interfaz) / `AlertaOperadorEmail` | El canal de notificación al operador |
| `GeneradorReporte` | El reporte PDF de misiones (en el MVP el formato es fijo: PDF) |
| `EstrategiaRuta` (interfaz) / `RutaDirecta`, `RutaEvitandoEdificios` | Cada algoritmo de ruta vive en su clase; uno nuevo no toca los existentes (OCP) |
| `AsignadorMision` | Las reglas de asignación: el drone debe estar disponible y la misión en `PENDIENTE`; luego calcula ruta, guarda y avisa a través de interfaces recibidas por constructor (DIP) |

- Las credenciales MySQL se inyectan en el constructor de `RepositorioMisionMySQL`; no quedan en el código.
- `AlertaOperadorEmail` y `GeneradorReporte` son stubs documentados: el SMTP e iText reales están fuera del alcance de Chimchar.
- La estrategia de ruta se elige al crear `AsignadorMision` (configuración del servicio). Si en el futuro cada misión necesita su propio tipo de ruta, la estrategia pasaría a ser parámetro de `asignar`.

Un cliente que solo asigna misiones ya no arrastra `guardarEnBD`/`enviarAlertaEmail`/`generarReportePDF` (ISP), y cambiar el repositorio o la estrategia de ruta no requiere tocar `AsignadorMision` (DIP + OCP).
