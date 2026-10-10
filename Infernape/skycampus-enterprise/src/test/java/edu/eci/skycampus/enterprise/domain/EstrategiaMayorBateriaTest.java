package edu.eci.skycampus.enterprise.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EstrategiaMayorBateriaTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final Drone NORMAL_90 = new Drone("D-01", ECI, 90, 5.0, true, false);
    private static final Drone EXPRESS_60 = new Drone("D-02", ECI, 60, 3.0, true, true);
    private static final Drone EXPRESS_75 = new Drone("D-03", ECI, 75, 3.0, true, true);
    private final EstrategiaMayorBateria estrategia = new EstrategiaMayorBateria();

    @Test
    void normal_eligeLaMayorBateriaSinImportarSiEsExpress() {
        assertEquals(Optional.of(NORMAL_90),
                estrategia.elegir(List.of(EXPRESS_60, NORMAL_90, EXPRESS_75), solicitud(PrioridadMision.NORMAL)));
    }

    @Test
    void urgente_soloConsideraExpressAunqueHayaUnNormalConMasBateria() {
        assertEquals(Optional.of(EXPRESS_75),
                estrategia.elegir(List.of(NORMAL_90, EXPRESS_60, EXPRESS_75), solicitud(PrioridadMision.URGENTE)));
    }

    @Test
    void urgente_sinExpress_noElige() {
        assertTrue(estrategia.elegir(List.of(NORMAL_90), solicitud(PrioridadMision.URGENTE)).isEmpty());
    }

    @Test
    void empateDeBateria_ganaElIdMenor() {
        Drone d05 = new Drone("D-05", ECI, 80, 5.0, true, false);
        Drone d04 = new Drone("D-04", ECI, 80, 5.0, true, false);

        assertEquals(Optional.of(d04), estrategia.elegir(List.of(d05, d04), solicitud(PrioridadMision.BAJO)));
    }

    private static SolicitudAsignacion solicitud(PrioridadMision prioridad) {
        return new SolicitudAsignacion("M-1", ECI, "ECI", "UNAL", 1.0, prioridad);
    }
}
