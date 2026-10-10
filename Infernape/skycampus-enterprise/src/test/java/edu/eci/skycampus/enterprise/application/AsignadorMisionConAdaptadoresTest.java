package edu.eci.skycampus.enterprise.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.EstadoMision;
import edu.eci.skycampus.enterprise.domain.EstrategiaMayorBateria;
import edu.eci.skycampus.enterprise.domain.PrioridadMision;
import edu.eci.skycampus.enterprise.domain.Sede;
import edu.eci.skycampus.enterprise.domain.SolicitudAsignacion;
import edu.eci.skycampus.enterprise.infrastructure.RepositorioFlotaEnMemoria;
import edu.eci.skycampus.enterprise.infrastructure.RepositorioSedesEnMemoria;
import edu.eci.skycampus.enterprise.infrastructure.ServicioAerocivilSimulado;
import edu.eci.skycampus.enterprise.infrastructure.ServicioClimaSimulado;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Las tres capas juntas con los adaptadores locales: mismo caso de uso, sin cambiar una línea de aplicación. */
class AsignadorMisionConAdaptadoresTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final Sede UNAL = new Sede("UNAL", "Universidad Nacional");
    private static final ServicioAerocivilSimulado AUTORIZA_TODO = new ServicioAerocivilSimulado((o, d) -> true);
    private static final RepositorioSedesEnMemoria ACTIVAS = new RepositorioSedesEnMemoria(Set.of(ECI, UNAL));

    @Test
    void urgenteEnEci_asignaElExpressDeEseCampusYLoSacaDeLaFlota() {
        RepositorioFlotaEnMemoria flota = new RepositorioFlotaEnMemoria(List.of(
                new Drone("D-01", ECI, 95, 5.0, true, false),
                new Drone("D-02", ECI, 70, 3.0, true, true),
                new Drone("D-03", UNAL, 99, 5.0, true, true)));
        List<String> avisos = new ArrayList<>();
        AsignadorMision asignador = new AsignadorMision(flota, new ServicioClimaSimulado((o, d) -> true),
                AUTORIZA_TODO, ACTIVAS,
                new EstrategiaMayorBateria(), (mision, drone, estado) -> avisos.add(mision + ":" + drone.id() + ":" + estado));

        Drone asignado = asignador.asignar(
                new SolicitudAsignacion("M-7", ECI, "ECI", "UNAL", 2.0, PrioridadMision.URGENTE)).orElseThrow();

        assertEquals("D-02", asignado.id());
        assertEquals(List.of("M-7:D-02:" + EstadoMision.EN_VUELO), avisos);
        assertEquals(List.of("D-01"), flota.findDisponibles(ECI).stream().map(Drone::id).toList());
    }

    @Test
    void climaSimuladoAdverso_noAsignaNiCambiaLaFlota() {
        RepositorioFlotaEnMemoria flota = new RepositorioFlotaEnMemoria(List.of(new Drone("D-01", ECI, 95, 5.0, true, true)));
        AsignadorMision asignador = new AsignadorMision(flota,
                new ServicioClimaSimulado((o, d) -> !d.equals("UNAL")), AUTORIZA_TODO, ACTIVAS,
                new EstrategiaMayorBateria(), (mision, drone, estado) -> { });

        assertTrue(asignador.asignar(
                new SolicitudAsignacion("M-8", ECI, "ECI", "UNAL", 1.0, PrioridadMision.NORMAL)).isEmpty());
        assertEquals(1, flota.findDisponibles(ECI).size());
    }
}
