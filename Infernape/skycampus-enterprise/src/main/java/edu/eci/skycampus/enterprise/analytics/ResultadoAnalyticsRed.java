package edu.eci.skycampus.enterprise.analytics;

import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ResultadoAnalyticsRed(
        Map<Sede, Optional<MetricasEficienciaSede>> metricasPorSede,
        List<MetricasEficienciaSede> ranking) {

    public ResultadoAnalyticsRed {
        metricasPorSede = Map.copyOf(metricasPorSede);
        ranking = List.copyOf(ranking);
    }
}
