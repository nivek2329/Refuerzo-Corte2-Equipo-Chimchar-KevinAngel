package edu.eci.skycampus.enterprise.reporting;

import edu.eci.skycampus.enterprise.analytics.MetricasEficienciaSede;

/** Producto A de la familia: indicadores agregados de una sede (sale del reto 01). */
public interface ReporteResumen {
    FormatoReporte formato();

    String generar(MetricasEficienciaSede metricas);
}
