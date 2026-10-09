# Hotfix v3.0.1 · Ruta ECI → UNAL con viento sur

## Incidente simulado

El cálculo de avance debe interpretar `vientoDesdeGrados` como la dirección meteorológica **desde donde viene** el viento. Para un rumbo norte (0°), un viento desde el sur (180°) es de cola y aumenta la velocidad sobre el suelo. Invertir ese convenio resta velocidad en vez de sumarla y estima mal la entrega inter-sede.

## Corrección

`CalculadorRutaInterSede` proyecta el viento sobre el rumbo mediante el coseno de la diferencia angular. La velocidad efectiva es `velocidadDron - velocidadViento × cos(vientoDesde - rumbo)`. Así, un dron a 30 km/h con viento de 10 km/h desde el sur vuela hacia el norte a 40 km/h; un trayecto de 60 km tarda 1,5 horas.

El cálculo rechaza direcciones inválidas y condiciones en las que el viento iguala o supera la velocidad de avance. Los casos de viento de cola, viento de frente, viento cruzado y viento que impide avanzar tienen pruebas automatizadas.

## Verificación

Desde `Infernape/skycampus-enterprise`, ejecutar `mvn verify`. El hotfix reutiliza este cálculo en la arquitectura de rutas del reto 03.
