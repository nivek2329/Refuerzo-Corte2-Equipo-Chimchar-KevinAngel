package edu.eci.skycampus.enterprise.reporting;

import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.droneMasUtilizado;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.minutosEntrega;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.misionesDeLaSede;
import static edu.eci.skycampus.enterprise.reporting.ContenidoReporte.requerirMetricas;

import edu.eci.skycampus.enterprise.analytics.MetricasEficienciaSede;
import edu.eci.skycampus.enterprise.domain.Mision;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Familia JSON: Uniandes consume los reportes desde su propio dashboard. */
public final class FabricaReportesUniandes implements FabricaReportesSede {
    @Override
    public ReporteResumen crearResumen() { return new ResumenJson(); }

    @Override
    public ReporteDetalle crearDetalle() { return new DetalleJson(); }

    static String texto(String valor) {
        return "\"" + valor.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static String numero(double valor) {
        return String.format(Locale.ROOT, "%.4f", valor);
    }

    private static final class ResumenJson implements ReporteResumen {
        @Override public FormatoReporte formato() { return FormatoReporte.JSON; }

        @Override
        public String generar(MetricasEficienciaSede metricas) {
            requerirMetricas(metricas);
            String promedio = metricas.tiempoPromedioEntregaMinutos().isPresent()
                    ? numero(metricas.tiempoPromedioEntregaMinutos().getAsDouble()) : "null";
            return "{\"sede\":" + texto(metricas.sede().codigo())
                    + ",\"totalMisiones\":" + metricas.totalMisiones()
                    + ",\"tasaExito\":" + numero(metricas.tasaExito())
                    + ",\"tiempoPromedioMin\":" + promedio
                    + ",\"droneMasUtilizado\":" + texto(droneMasUtilizado(metricas))
                    + ",\"porcentajeUrgentes\":" + numero(metricas.porcentajeUrgentes()) + "}";
        }
    }

    private static final class DetalleJson implements ReporteDetalle {
        @Override public FormatoReporte formato() { return FormatoReporte.JSON; }

        @Override
        public String generar(Sede sede, List<Mision> misiones) {
            String items = misionesDeLaSede(sede, misiones).stream()
                    .map(mision -> "{\"id\":" + texto(mision.id()) + ",\"drone\":" + texto(mision.droneId())
                            + ",\"prioridad\":" + texto(mision.prioridad().name())
                            + ",\"estado\":" + texto(mision.estado().name())
                            + ",\"minutos\":" + (mision.tiempoEntrega().isPresent() ? minutosEntrega(mision) : "null")
                            + "}")
                    .collect(Collectors.joining(","));
            return "{\"sede\":" + texto(sede.codigo()) + ",\"misiones\":[" + items + "]}";
        }
    }
}
