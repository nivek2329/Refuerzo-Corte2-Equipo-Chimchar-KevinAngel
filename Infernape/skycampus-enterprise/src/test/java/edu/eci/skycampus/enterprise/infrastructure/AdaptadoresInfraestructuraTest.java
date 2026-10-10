package edu.eci.skycampus.enterprise.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdaptadoresInfraestructuraTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final Sede UNAL = new Sede("UNAL", "Universidad Nacional");

    @Test
    void repositorio_devuelveSoloDisponiblesDeLaSedeOrdenadosPorId() {
        RepositorioFlotaEnMemoria repo = new RepositorioFlotaEnMemoria(List.of(
                new Drone("D-03", ECI, 50, 2.0, true, false),
                new Drone("D-01", ECI, 50, 2.0, true, false),
                new Drone("D-02", ECI, 50, 2.0, false, false),
                new Drone("D-04", UNAL, 50, 2.0, true, false)));

        assertEquals(List.of("D-01", "D-03"), repo.findDisponibles(ECI).stream().map(Drone::id).toList());
    }

    @Test
    void repositorio_registrarAsignacion_dejaElDroneNoDisponibleYNoPermiteRepetir() {
        RepositorioFlotaEnMemoria repo = new RepositorioFlotaEnMemoria(List.of(new Drone("D-01", ECI, 50, 2.0, true, false)));

        repo.registrarAsignacion("M-1", "D-01");

        assertTrue(repo.findDisponibles(ECI).isEmpty());
        assertThrows(IllegalStateException.class, () -> repo.registrarAsignacion("M-2", "D-01"));
        assertThrows(IllegalStateException.class, () -> repo.registrarAsignacion("M-3", "D-99"));
    }

    @Test
    void repositorio_rechazaNulos() {
        assertThrows(NullPointerException.class, () -> new RepositorioFlotaEnMemoria(null));
        ArrayList<Drone> conNulo = new ArrayList<>();
        conNulo.add(null);
        assertThrows(NullPointerException.class, () -> new RepositorioFlotaEnMemoria(conNulo));
        RepositorioFlotaEnMemoria repo = new RepositorioFlotaEnMemoria(List.of());
        assertThrows(NullPointerException.class, () -> repo.findDisponibles(null));
        assertThrows(NullPointerException.class, () -> repo.registrarAsignacion(null, "D-01"));
        assertThrows(NullPointerException.class, () -> repo.registrarAsignacion("M-1", null));
    }

    @Test
    void climaSimulado_aplicaLaReglaConfiguradaYRechazaNulos() {
        ServicioClimaSimulado clima = new ServicioClimaSimulado((origen, destino) -> !destino.equals("EAFIT"));

        assertTrue(clima.condicionesAptas("ECI", "UNAL"));
        assertFalse(clima.condicionesAptas("ECI", "EAFIT"));
        assertThrows(NullPointerException.class, () -> new ServicioClimaSimulado(null));
        assertThrows(NullPointerException.class, () -> clima.condicionesAptas(null, "UNAL"));
        assertThrows(NullPointerException.class, () -> clima.condicionesAptas("ECI", null));
    }
}
