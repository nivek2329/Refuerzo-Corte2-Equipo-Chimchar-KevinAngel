package edu.eci.skycampus.enterprise.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

class DroneYSolicitudTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");

    @ParameterizedTest(name = "id=''{0}'' bateria={1} capacidad={2}")
    @CsvSource({"' ',50,1.0", "D-1,-1,1.0", "D-1,101,1.0", "D-1,50,0", "D-1,50,NaN", "D-1,50,Infinity"})
    void droneConDatosInvalidos_seRechaza(String id, int bateria, double capacidad) {
        assertThrows(IllegalArgumentException.class, () -> new Drone(id, ECI, bateria, capacidad, true, false));
    }

    @Test
    void noDisponible_conservaLosDemasDatos() {
        Drone drone = new Drone("D-1", ECI, 80, 2.5, true, true);

        Drone ocupado = drone.noDisponible();

        assertFalse(ocupado.disponible());
        assertEquals(new Drone("D-1", ECI, 80, 2.5, false, true), ocupado);
    }

    @ParameterizedTest(name = "mision=''{0}'' origen=''{1}'' destino=''{2}'' peso={3}")
    @CsvSource({"' ',ECI,UNAL,1", "M-1,' ',UNAL,1", "M-1,ECI,' ',1", "M-1,ECI,UNAL,0", "M-1,ECI,UNAL,NaN"})
    void solicitudConDatosInvalidos_seRechaza(String mision, String origen, String destino, double peso) {
        assertThrows(IllegalArgumentException.class,
                () -> new SolicitudAsignacion(mision, ECI, origen, destino, peso, PrioridadMision.NORMAL));
    }
}
