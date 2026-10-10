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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Familia CSV (separador ';'): EAFIT importa los reportes en su hoja de cálculo. */
public final class FabricaReportesEafit implements FabricaReportesSede {
    @Override
    public ReporteResumen crearResumen() { return new ResumenCsv(); }

    @Override
    public ReporteDetalle crearDetalle() { return new DetalleCsv(); }

    static String celda(String valor) {
        boolean requiereComillas = valor.contains(";") || valor.contains("\"") || valor.contains("\n");
        return requiereComillas ? "\"" + valor.replace("\"", "\"\"") + "\"" : valor;
    }

    private static String fila(String... celdas) {
        return Stream.of(celdas).map(FabricaReportesEafit::celda).collect(Collectors.joining(";"));
    }

    private static final class ResumenCsv implements ReporteResumen {
        @Override public FormatoReporte formato() { return FormatoReporte.CSV; }

        @Override
        public String generar(MetricasEficienciaSede metricas) {
            requerirMetricas(metricas);
            return fila("sede", "tasa_exito_pct", "tiempo_promedio_min", "drone_mas_utilizado", "urgentes_pct") + "\n"
                    + fila(metricas.sede().codigo(), porcentaje(metricas.tasaExito()), tiempoPromedio(metricas),
                    droneMasUtilizado(metricas), decimal(metricas.porcentajeUrgentes())) + "\n";
        }
    }

    private static final class DetalleCsv implements ReporteDetalle {
        @Override public FormatoReporte formato() { return FormatoReporte.CSV; }

        @Override
        public String generar(Sede sede, List<Mision> misiones) {
            String filas = misionesDeLaSede(sede, misiones).stream()
                    .map(mision -> fila(mision.id(), mision.droneId(), mision.prioridad().name(),
                            mision.estado().name(), minutosEntrega(mision)) + "\n")
                    .collect(Collectors.joining());
            return fila("mision", "drone", "prioridad", "estado", "minutos") + "\n" + filas;
        }
    }
}
