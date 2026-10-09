package edu.eci.skycampus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Drone")
class DroneTest {

    @ParameterizedTest(name = "acepta la batería límite {0}%")
    @ValueSource(ints = {0, 100})
    void crearDrone_bateriaEnLimiteValido_conservaBateria(int bateria) {
        // Arrange: batería recibida por parámetro

        // Act
        Drone drone = new Drone("D-01", "DJI Mini 3", bateria, true, "Bloque A");

        // Assert
        assertEquals(bateria, drone.bateria());
    }

    @ParameterizedTest(name = "rechaza la batería fuera de rango {0}%")
    @ValueSource(ints = {-1, 101})
    void crearDrone_bateriaFueraDeRango_lanzaExcepcionConMensaje(int bateria) {
        // Arrange: batería recibida por parámetro

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Drone("D-01", "DJI Mini 3", bateria, true, "Bloque A"));

        // Assert
        assertEquals("bateria debe estar entre 0 y 100", error.getMessage());
    }

    @Test
    @DisplayName("rechaza id null con mensaje claro")
    void crearDrone_idNulo_lanzaExcepcionConMensaje() {
        // Arrange
        String id = null;

        // Act
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> new Drone(id, "DJI Mini 3", 50, true, "Bloque A"));

        // Assert
        assertEquals("id no puede ser null", error.getMessage());
    }

    @Test
    @DisplayName("rechaza modelo null con mensaje claro")
    void crearDrone_modeloNulo_lanzaExcepcionConMensaje() {
        // Arrange
        String modelo = null;

        // Act
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> new Drone("D-01", modelo, 50, true, "Bloque A"));

        // Assert
        assertEquals("modelo no puede ser null", error.getMessage());
    }

    @Test
    @DisplayName("rechaza ubicación null con mensaje claro")
    void crearDrone_ubicacionNula_lanzaExcepcionConMensaje() {
        // Arrange
        String ubicacion = null;

        // Act
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> new Drone("D-01", "DJI Mini 3", 50, true, ubicacion));

        // Assert
        assertEquals("ubicacion no puede ser null", error.getMessage());
    }
}
