package edu.eci.skycampus.enterprise.reporting;

import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.decimal;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.droneMasUtilizado;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.minutosEntrega;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.misionesDeLaSede;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.porcentaje;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.requerirMetricas;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.tiempoPromedio;

import edu.eci.skycampus.enterprise.analytics.MetricasEficienciaSede;
import edu.eci.skycampus.enterprise.domain.Mision;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.ArrayList;
import java.util.List;

/** Familia PDF: la UNAL archiva los reportes como documentos imprimibles. */
public final class FabricaReportesUnal implements FabricaReportesSede {
    @Override
    public ReporteResumen crearResumen() { return new ResumenPdf(); }

    @Override
    public ReporteDetalle crearDetalle() { return new DetallePdf(); }

    private static final class ResumenPdf implements ReporteResumen {
        @Override public FormatoReporte formato() { return FormatoReporte.PDF; }

        @Override
        public String generar(MetricasEficienciaSede metricas) {
            requerirMetricas(metricas);
            return DocumentoPdf.conLineas(List.of(
                    "Resumen de eficiencia - " + metricas.sede().universidad(),
                    "Tasa de exito: " + porcentaje(metricas.tasaExito()) + " %",
                    "Tiempo promedio: " + tiempoPromedio(metricas) + " min",
                    "Drone mas utilizado: " + droneMasUtilizado(metricas),
                    "Urgentes: " + decimal(metricas.porcentajeUrgentes()) + " %"));
        }
    }

    private static final class DetallePdf implements ReporteDetalle {
        @Override public FormatoReporte formato() { return FormatoReporte.PDF; }

        @Override
        public String generar(Sede sede, List<Mision> misiones) {
            List<String> lineas = new ArrayList<>();
            lineas.add("Detalle de misiones - " + sede.codigo());
            misionesDeLaSede(sede, misiones).forEach(mision -> lineas.add(mision.id() + "  " + mision.droneId()
                    + "  " + mision.prioridad() + "  " + mision.estado() + "  " + minutosEntrega(mision)));
            return DocumentoPdf.conLineas(lineas);
        }
    }
}
