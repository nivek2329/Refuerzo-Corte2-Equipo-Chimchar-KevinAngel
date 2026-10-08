# 04 · Principios SOLID — GestorDrone

## 1\. Por qué cada principio está violado

**SRP (violado):** `GestorDrone` tiene cinco responsabilidades sin relación entre sí (asignar, persistir, alertar, reportar, calcular ruta), así que un cambio en cualquiera de esos cinco dominios obliga a modificar la misma clase.

**OCP (violado):** `calcularRuta` decide con `if/else if` sobre strings de tipo, así que agregar un nuevo tipo de ruta exige modificar el método existente en vez de extenderlo con código nuevo.

**LSP (no aplica directamente):** no hay herencia en este fragmento, así que no hay una subclase que evaluar frente a su padre.

**ISP (violado, posible):** un cliente que solo necesita asignar misiones queda forzado a depender de una clase que también expone `guardarEnBD`, `enviarAlertaEmail` y `generarReportePDF`, métodos que no le interesan ni debería conocer.

**DIP (violado):** `guardarEnBD` depende directamente de la clase concreta `DriverManager` y de una cadena de conexión MySQL hardcodeada, en lugar de depender de una abstracción de persistencia — la lógica de negocio queda acoplada a un motor de base de datos específico.

## 2\. Rediseño

Se separó `GestorDrone` en cinco piezas, cada una con una sola razón para cambiar:

- `RepositorioMision` (interfaz) / `RepositorioMisionMySQL` (implementación): cambia solo si cambia cómo se persiste una misión.
- `AlertaOperador` (interfaz) / `AlertaOperadorEmail` (implementación): cambia solo si cambia el canal de notificación.
- `GeneradorReporte`\: cambia solo si cambia el formato o contenido del reporte.
- `EstrategiaRuta` (interfaz) / `RutaDirecta`, `RutaEvitandoEdificios` (implementaciones): cada algoritmo de ruta vive en su propia clase; agregar uno nuevo no toca los existentes (resuelve OCP).
- `AsignadorMision`\: orquesta las tres abstracciones anteriores recibidas por constructor (`RepositorioMision`, `AlertaOperador`, `EstrategiaRuta`); cambia solo si cambia la lógica de "cómo se asigna una misión", nunca por detalles de MySQL, SMTP o PDF (resuelve DIP, porque depende de interfaces, no de `DriverManager`).

Un cliente que solo necesita asignar misiones ya no arrastra `guardarEnBD`/`enviarAlertaEmail`/`generarReportePDF` (resuelve ISP), y cambiar el repositorio o la estrategia de ruta no requiere tocar `AsignadorMision` (resuelve DIP/OCP en conjunto).
