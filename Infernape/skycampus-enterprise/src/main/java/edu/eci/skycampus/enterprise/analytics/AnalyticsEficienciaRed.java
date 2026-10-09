package edu.eci.skycampus.enterprise.analytics;

import edu.eci.skycampus.enterprise.domain.EstadoMision;
import edu.eci.skycampus.enterprise.domain.Mision;
import edu.eci.skycampus.enterprise.domain.PrioridadMision;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AnalyticsEficienciaRed {
    public ResultadoAnalyticsRed calcular(List<Sede> sedes, List<Mision> misiones) {
        Objects.requireNonNull(sedes, "sedes no puede ser null");
        Objects.requireNonNull(misiones, "misiones no puede ser null");
        Map<Sede, ResumenParcial> acumulados = misiones.stream()
                .collect(Collectors.groupingBy(Mision::sede, collectorPorSede()));
        return construirResultado(sedes, acumulados);
    }

    private ResultadoAnalyticsRed construirResultado(List<Sede> sedes, Map<Sede, ResumenParcial> acumulados) {
        Map<Sede, Optional<MetricasEficienciaSede>> metricas = sedes.stream().distinct()
                .collect(Collectors.toUnmodifiableMap(sede -> sede,
                        sede -> Optional.ofNullable(acumulados.get(sede)).map(resumen -> resumen.metricas(sede))));
        List<MetricasEficienciaSede> ranking = metricas.values().stream().flatMap(Optional::stream)
                .sorted(Comparator.comparingDouble(MetricasEficienciaSede::tasaExito).reversed()
                        .thenComparing(Comparator.comparingLong(MetricasEficienciaSede::misionesEntregadas).reversed())
                        .thenComparing(metrica -> metrica.sede().codigo()))
                .toList();
        return new ResultadoAnalyticsRed(metricas, ranking);
    }

    private static Collector<Mision, ?, ResumenParcial> collectorPorSede() {
        Collector<Mision, ?, Entregas> entregas = entregasCollector();
        Collector<Mision, ?, UsoYUrgencia> drones = dronesUrgentesCollector();
        Collector<Mision, ?, DatosComplementarios> complementarios =
                Collectors.teeing(entregas, drones, DatosComplementarios::new);
        return Collectors.teeing(Collectors.counting(), complementarios, ResumenParcial::new);
    }

    private static Collector<Mision, ?, Entregas> entregasCollector() {
        return Collectors.filtering(mision -> mision.estado() == EstadoMision.ENTREGADA,
                Collectors.teeing(Collectors.counting(), Collectors.averagingDouble(AnalyticsEficienciaRed::minutosEntrega),
                        Entregas::new));
    }

    private static Collector<Mision, ?, UsoYUrgencia> dronesUrgentesCollector() {
        return Collectors.teeing(Collectors.groupingBy(Mision::droneId, Collectors.counting()),
                Collectors.filtering(mision -> mision.prioridad() == PrioridadMision.URGENTE,
                        Collectors.counting()), UsoYUrgencia::new);
    }

    private static double minutosEntrega(Mision mision) {
        return mision.tiempoEntrega().orElseThrow().toMillis() / 60_000.0;
    }

    private record Entregas(long cantidad, double promedioMinutos) { }

    private record UsoYUrgencia(Map<String, Long> porDrone, long urgentes) { }

    private record DatosComplementarios(Entregas entregas, UsoYUrgencia usoYUrgencia) { }

    private record ResumenParcial(long total, DatosComplementarios datos) {
        private MetricasEficienciaSede metricas(Sede sede) {
            Entregas entregas = datos.entregas();
            double tasaExito = (double) entregas.cantidad() / total;
            double porcentajeUrgentes = (double) datos.usoYUrgencia().urgentes() * 100 / total;
            return new MetricasEficienciaSede(sede, total, entregas.cantidad(), tasaExito,
                    promedio(entregas), droneMasUsado(datos.usoYUrgencia().porDrone()), porcentajeUrgentes);
        }

        private OptionalDouble promedio(Entregas entregas) {
            return entregas.cantidad() == 0 ? OptionalDouble.empty()
                    : OptionalDouble.of(entregas.promedioMinutos());
        }

        private Optional<String> droneMasUsado(Map<String, Long> porDrone) {
            return porDrone.entrySet().stream().max(Map.Entry.<String, Long>comparingByValue()
                    .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder()))).map(Map.Entry::getKey);
        }
    }
}
