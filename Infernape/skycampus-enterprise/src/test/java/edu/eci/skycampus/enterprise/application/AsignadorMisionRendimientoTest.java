package edu.eci.skycampus.enterprise.application;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.EstrategiaMayorBateria;
import edu.eci.skycampus.enterprise.domain.PrioridadMision;
import edu.eci.skycampus.enterprise.domain.Sede;
import edu.eci.skycampus.enterprise.domain.SolicitudAsignacion;
import edu.eci.skycampus.enterprise.infrastructure.RepositorioFlotaEnMemoria;
import edu.eci.skycampus.enterprise.infrastructure.RepositorioSedesEnMemoria;
import edu.eci.skycampus.enterprise.infrastructure.ServicioAerocivilSimulado;
import edu.eci.skycampus.enterprise.infrastructure.ServicioClimaSimulado;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/** RNF-12: con la red completa (100 drones) una asignación responde en menos de 500 ms. */
class AsignadorMisionRendimientoTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");

    @Test
    void cienDrones_asignaEnMenosDe500ms() {
        List<Drone> flota = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new Drone("D-%03d".formatted(i), ECI, i % 101, 1 + i % 15, true, i % 4 == 0))
                .toList();
        AsignadorMision asignador = new AsignadorMision(new RepositorioFlotaEnMemoria(flota),
                new ServicioClimaSimulado((o, d) -> true), new ServicioAerocivilSimulado((o, d) -> true),
                new RepositorioSedesEnMemoria(Set.of(ECI)), new EstrategiaMayorBateria(), (m, d, e) -> { });

        assertTimeoutPreemptively(Duration.ofMillis(500), () -> assertTrue(asignador.asignar(
                new SolicitudAsignacion("M-1", ECI, "ECI", "UNAL", 3.0, PrioridadMision.URGENTE)).isPresent()));
    }
}
