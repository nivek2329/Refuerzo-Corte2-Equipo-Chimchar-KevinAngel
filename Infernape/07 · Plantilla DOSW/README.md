# 07 · Plantilla DOSW — SC-15 Planificar ruta multi-etapa inter-sede

Ficha: [`SC-15_Planificar_ruta_multi_etapa_inter_sede_DOSW.docx`](SC-15_Planificar_ruta_multi_etapa_inter_sede_DOSW.docx), generada con [`gen_sc15.py`](gen_sc15.py) sobre la misma plantilla DOSW de SC-07 (Monferno), así conserva encabezado, estilos y secciones.

| Lo que pide el reto | Dónde está en la ficha |
|---|---|
| Misión con sub-objetos profundos | **Datos de entrada**: `mision` → `paquete` → `dimensiones` (3 niveles), `sedeOrigen/sedeDestino` → `ubicacion` (Coordenada), `restricciones` → `ventanaEntrega`. Cada objeto declara su tipo con atributos y luego se desglosa campo por campo. |
| Condiciones actuales del espacio aéreo | `espacioAereo` (viento, dirección y `zonasRestringidas[i]` con código y techo). |
| Flujo básico: Aerocivil → etapas → estaciones → drones por etapa → confirmar → iniciar vuelo | Pasos 3, 4, 5, 6, 8 y 9 (más 1–2 de solicitud/validación y 7 de revisión del coordinador). |
| Flujos alternos: Aerocivil rechaza, sin estación, paquete demasiado pesado | FA-1, FA-2 y FA-3; además FA-4 (sede inactiva) y FA-5 (Aerocivil sin respuesta). |
| 5 km con carga sin recargar / 30 min máximo en estación | RN-1 y RN-2, también como restricción en `etapas[i].distanciaKm` y `esperas[i].minutos`. |
| Sin "Seleccionador" ni "Lista de objetos" | Todos los campos usan tipos concretos: `Enum(...)`, `Integer`, `Decimal(p,s)`, `DateTime`, objetos con sus atributos y arreglos tipados (`Etapa[1..n]`, `ZonaRestringida[0..n]`) desglosados con `[i]`. |

**Trazabilidad:** SC-15 especifica RF-13, RF-14 y RNF-09 ([reto 06](../06%20·%20RF%20y%20RNF/README.md)). Las reglas que ya tienen código se prueban en `ReglasRutaMultiEtapaTest` (RN-1, RN-2), `RestriccionesVueloTest` (RN-3) y `AsignadorMisionFlujosAlternosTest` (Aerocivil antes de reservar, RN-4). La nota de la ficha separa lo implementado del estado objetivo (reserva de turnos en estaciones y estados de la ruta).

Regenerar: `python gen_sc15.py` desde esta carpeta.
