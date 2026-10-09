package edu.eci.skycampus.enterprise.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.eci.skycampus.enterprise.domain.EstadoMision;
import edu.eci.skycampus.enterprise.domain.Mision;
import edu.eci.skycampus.enterprise.domain.PrioridadMision;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AnalyticsEficienciaRedTest {
    private static final Instant INICIO = Instant.parse("2026-10-01T10:00:00Z");
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final Sede UNAL = new Sede("UNAL", "Universidad Nacional");
    private static final Sede UNIANDES = new Sede("UNIANDES", "Universidad de los Andes");
    private static final Sede EAFIT = new Sede("EAFIT", "Universidad EAFIT");

    @ParameterizedTest(name = "{0}")
    @MethodSource("escenariosDeRed")
    void calcular_escenariosEnterprise(String nombre, List<Sede> sedes, List<Mision> misiones,
                                       Map<Sede, Optional<MetricasEficienciaSede>> metricasEsperadas,
                                       List<Sede> rankingEsperado) {
        ResultadoAnalyticsRed resultado = new AnalyticsEficienciaRed().calcular(sedes, misiones);

        assertEquals(metricasEsperadas, resultado.metricasPorSede());
        assertEquals(rankingEsperado, resultado.ranking().stream().map(MetricasEficienciaSede::sede).toList());
    }

    private static Stream<Arguments> escenariosDeRed() {
        return Stream.of(
                Arguments.of("sede sin actividad", List.of(ECI), List.of(),
                        Map.of(ECI, Optional.empty()), List.of()),
                Arguments.of("una mision urgente entregada", List.of(ECI),
                        List.of(mision("M-1", ECI, "D-01", PrioridadMision.URGENTE, EstadoMision.ENTREGADA, 12)),
                        Map.of(ECI, Optional.of(metricas(ECI, 1, 1, 1.0, 12.0, "D-01", 100.0))), List.of(ECI)),
                Arguments.of("empate de sedes resuelto por codigo", List.of(UNAL, ECI),
                        List.of(mision("M-2", UNAL, "D-02", PrioridadMision.NORMAL, EstadoMision.ENTREGADA, 8),
                                mision("M-3", ECI, "D-01", PrioridadMision.NORMAL, EstadoMision.ENTREGADA, 8)),
                        Map.of(ECI, Optional.of(metricas(ECI, 1, 1, 1.0, 8.0, "D-01", 0.0)),
                                UNAL, Optional.of(metricas(UNAL, 1, 1, 1.0, 8.0, "D-02", 0.0))), List.of(ECI, UNAL)),
                Arguments.of("red completa con cuatro sedes", List.of(ECI, UNAL, UNIANDES, EAFIT),
                        misionesRedCompleta(), metricasRedCompleta(), List.of(UNAL, EAFIT, ECI, UNIANDES)));
    }

    private static List<Mision> misionesRedCompleta() {
        return List.of(
                mision("M-10", ECI, "D-01", PrioridadMision.URGENTE, EstadoMision.ENTREGADA, 10),
                mision("M-11", ECI, "D-01", PrioridadMision.NORMAL, EstadoMision.PENDIENTE, 0),
                mision("M-12", UNAL, "D-02", PrioridadMision.NORMAL, EstadoMision.ENTREGADA, 8),
                mision("M-13", UNAL, "D-03", PrioridadMision.URGENTE, EstadoMision.ENTREGADA, 12),
                mision("M-14", UNIANDES, "D-04", PrioridadMision.NORMAL, EstadoMision.FALLIDA, 0),
                mision("M-15", EAFIT, "D-05", PrioridadMision.URGENTE, EstadoMision.ENTREGADA, 20));
    }

    private static Map<Sede, Optional<MetricasEficienciaSede>> metricasRedCompleta() {
        return Map.of(
                ECI, Optional.of(metricas(ECI, 2, 1, 0.5, 10.0, "D-01", 50.0)),
                UNAL, Optional.of(metricas(UNAL, 2, 2, 1.0, 10.0, "D-02", 50.0)),
                UNIANDES, Optional.of(metricasSinEntrega(UNIANDES, 1, "D-04", 0.0)),
                EAFIT, Optional.of(metricas(EAFIT, 1, 1, 1.0, 20.0, "D-05", 100.0)));
    }

    private static Mision mision(String id, Sede sede, String droneId, PrioridadMision prioridad,
                                 EstadoMision estado, long minutosEntrega) {
        Optional<Instant> entrega = estado == EstadoMision.ENTREGADA
                ? Optional.of(INICIO.plusSeconds(minutosEntrega * 60)) : Optional.empty();
        return new Mision(id, sede, droneId, prioridad, estado, INICIO, entrega);
    }

    private static MetricasEficienciaSede metricas(Sede sede, long total, long entregadas, double tasa,
                                                    double promedio, String drone, double urgentes) {
        return new MetricasEficienciaSede(sede, total, entregadas, tasa,
                OptionalDouble.of(promedio), Optional.of(drone), urgentes);
    }

    private static MetricasEficienciaSede metricasSinEntrega(Sede sede, long total, String drone, double urgentes) {
        return new MetricasEficienciaSede(sede, total, 0, 0.0,
                OptionalDouble.empty(), Optional.of(drone), urgentes);
    }
}
