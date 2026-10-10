package edu.eci.skycampus.enterprise.reporting;

/**
 * Abstract Factory: cada sede crea una familia coherente de reportes (resumen + detalle) en un solo formato.
 * Quien pide los reportes no sabe qué clases concretas recibe; solo que ambos pertenecen a la misma familia.
 */
public interface FabricaReportesSede {
    ReporteResumen crearResumen();

    ReporteDetalle crearDetalle();
}
