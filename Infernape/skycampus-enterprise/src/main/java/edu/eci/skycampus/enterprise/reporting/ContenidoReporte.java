package edu.eci.skycampus.enterprise.reporting;

import edu.eci.skycampus.enterprise.analytics.MetricasEficienciaSede;
import edu.eci.skycampus.enterprise.domain.Mision;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Datos comunes a todos los formatos; cada familia solo decide cómo escribirlos. */
final class ContenidoReporte {
    private ContenidoReporte() { }

    static String porcentaje(double fraccion) {
        return String.format(Locale.ROOT, "%.1f", fraccion * 100);
    }

    static String decimal(double valor) {
        return String.format(Locale.ROOT, "%.1f", valor);
    }

    static String tiempoPromedio(MetricasEficienciaSede metricas) {
        return metricas.tiempoPromedioEntregaMinutos().isPresent()
                ? decimal(metricas.tiempoPromedioEntregaMinutos().getAsDouble()) : "sin entregas";
    }

    static String droneMasUtilizado(MetricasEficienciaSede metricas) {
        return metricas.droneMasUtilizado().orElse("ninguno");
    }

    static String minutosEntrega(Mision mision) {
        return mision.tiempoEntrega().map(duracion -> decimal(duracion.toSeconds() / 60.0)).orElse("");
    }

    static List<Mision> misionesDeLaSede(Sede sede, List<Mision> misiones) {
        Objects.requireNonNull(sede, "sede no puede ser null");
        List<Mision> copia = List.copyOf(Objects.requireNonNull(misiones, "misiones no puede ser null"));
        if (copia.stream().anyMatch(mision -> !mision.sede().equals(sede))) {
            throw new IllegalArgumentException("el detalle solo admite misiones de la sede " + sede.codigo());
        }
        return copia;
    }

    static MetricasEficienciaSede requerirMetricas(MetricasEficienciaSede metricas) {
        return Objects.requireNonNull(metricas, "metricas no puede ser null");
    }
}
