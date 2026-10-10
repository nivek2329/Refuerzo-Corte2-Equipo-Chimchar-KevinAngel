package edu.eci.skycampus.enterprise.reporting;

import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.droneMasUtilizado;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.minutosEntrega;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.misionesDeLaSede;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.porcentaje;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.requerirMetricas;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.tiempoPromedio;

import edu.eci.skycampus.enterprise.analytics.MetricasEficienciaSede;
import edu.eci.skycampus.enterprise.domain.Mision;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.List;
import java.util.stream.Collectors;

/** Familia HTML: el panel web de la ECI muestra los reportes embebidos. */
public final class FabricaReportesEci implements FabricaReportesSede {
    @Override
    public ReporteResumen crearResumen() { return new ResumenHtml(); }

    @Override
    public ReporteDetalle crearDetalle() { return new DetalleHtml(); }

    static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static final class ResumenHtml implements ReporteResumen {
        @Override public FormatoReporte formato() { return FormatoReporte.HTML; }

        @Override
        public String generar(MetricasEficienciaSede metricas) {
            requerirMetricas(metricas);
            return "<section class=\"resumen\"><h1>" + escapar(metricas.sede().universidad()) + "</h1><ul>"
                    + "<li>Tasa de éxito: " + porcentaje(metricas.tasaExito()) + " %</li>"
                    + "<li>Tiempo promedio: " + tiempoPromedio(metricas) + " min</li>"
                    + "<li>Drone más utilizado: " + escapar(droneMasUtilizado(metricas)) + "</li>"
                    + "<li>Urgentes: " + ContenidoReporte.decimal(metricas.porcentajeUrgentes()) + " %</li>"
                    + "</ul></section>";
        }
    }

    private static final class DetalleHtml implements ReporteDetalle {
        @Override public FormatoReporte formato() { return FormatoReporte.HTML; }

        @Override
        public String generar(Sede sede, List<Mision> misiones) {
            String filas = misionesDeLaSede(sede, misiones).stream()
                    .map(mision -> "<tr><td>" + escapar(mision.id()) + "</td><td>" + escapar(mision.droneId())
                            + "</td><td>" + mision.prioridad() + "</td><td>" + mision.estado() + "</td><td>"
                            + minutosEntrega(mision) + "</td></tr>")
                    .collect(Collectors.joining());
            return "<table class=\"detalle\" data-sede=\"" + escapar(sede.codigo()) + "\"><tr><th>Misión</th>"
                    + "<th>Drone</th><th>Prioridad</th><th>Estado</th><th>Minutos</th></tr>" + filas + "</table>";
        }
    }
}
