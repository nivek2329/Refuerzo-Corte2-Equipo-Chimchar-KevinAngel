package edu.eci.skycampus.enterprise.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.eci.skycampus.enterprise.analytics.MetricasEficienciaSede;
import java.time.Instant;
import java.util.Optional;
import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

class ValidacionDominioTest {
    private static final Sede SEDE = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final Instant INICIO = Instant.parse("2026-10-01T10:00:00Z");

    @Test
    void sedeRechazaCodigoEnBlanco() {
        assertThrows(IllegalArgumentException.class, () -> new Sede(" ", "Universidad"));
    }

    @Test
    void sedeRechazaUniversidadEnBlanco() {
        assertThrows(IllegalArgumentException.class, () -> new Sede("ECI", " "));
    }

    @Test
    void misionRechazaEntregaAnteriorALaCreacion() {
        assertThrows(IllegalArgumentException.class, () -> new Mision("M-1", SEDE, "D-01",
                PrioridadMision.NORMAL, EstadoMision.ENTREGADA, INICIO,
                Optional.of(INICIO.minusSeconds(1))));
    }

    @Test
    void misionRechazaFechaEnMisionNoEntregada() {
        assertThrows(IllegalArgumentException.class, () -> new Mision("M-2", SEDE, "D-01",
                PrioridadMision.NORMAL, EstadoMision.PENDIENTE, INICIO,
                Optional.of(INICIO.plusSeconds(1))));
    }

    @Test
    void metricasRechazanConteosInconsistentes() {
        assertThrows(IllegalArgumentException.class, () -> new MetricasEficienciaSede(
                SEDE, 1, 2, 2.0, OptionalDouble.empty(), Optional.empty(), 0.0));
    }
}
