import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ValidadorMision")
class ValidadorMisionTest {
    private ValidadorMision validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorMision();
    }

    @Nested
    @DisplayName("tieneBateriaSuficiente")
    class BateriaSuficienteTests {
        @Test
        @DisplayName("acepta batería exactamente en el mínimo de 30%")
        void bateriaEnElMinimo_esSuficiente() {
            // Arrange
            Drone drone = new Drone("D-01", "DJI Mini 3", 30, true, "Bloque A");

            // Act
            boolean resultado = validador.tieneBateriaSuficiente(drone);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("rechaza batería inferior al mínimo")
        void bateriaInferiorAlMinimo_noEsSuficiente() {
            // Arrange
            Drone drone = new Drone("D-04", "DJI Mini 3", 29, true, "Bloque B");

            // Act
            boolean resultado = validador.tieneBateriaSuficiente(drone);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("rechaza un drone null con mensaje claro")
        void droneNulo_lanzaExcepcion() {
            // Arrange
            Drone drone = null;

            // Act / Assert
            assertThrows(NullPointerException.class, () -> validador.tieneBateriaSuficiente(drone));
        }
    }

    @Nested
    @DisplayName("validarDestino")
    class ValidarDestinoTests {
        @Test
        @DisplayName("acepta un destino del catálogo")
        void destinoDelCatalogo_noLanzaExcepcion() {
            // Arrange
            String destino = "Biblioteca";

            // Act / Assert
            assertDoesNotThrow(() -> validador.validarDestino(destino));
        }

        @Test
        @DisplayName("rechaza un destino fuera del catálogo")
        void destinoFueraDelCatalogo_lanzaExcepcionEspecifica() {
            // Arrange
            String destino = "Edificio Inexistente";

            // Act / Assert
            assertThrows(DestinoInvalidoException.class, () -> validador.validarDestino(destino));
        }

        @Test
        @DisplayName("rechaza un destino null")
        void destinoNulo_lanzaExcepcion() {
            // Arrange
            String destino = null;

            // Act / Assert
            assertThrows(NullPointerException.class, () -> validador.validarDestino(destino));
        }
    }

    @Nested
    @DisplayName("droneEstaDisponible")
    class DroneEstaDisponibleTests {
        @Test
        @DisplayName("informa disponible cuando el indicador es true")
        void droneDisponible_retornaTrue() {
            // Arrange
            Drone drone = new Drone("D-01", "DJI Mini 3", 85, true, "Bloque A");

            // Act
            boolean resultado = validador.droneEstaDisponible(drone);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("informa no disponible cuando el indicador es false")
        void droneNoDisponible_retornaFalse() {
            // Arrange
            Drone drone = new Drone("D-02", "DJI Mini 3", 42, false, "Biblioteca");

            // Act
            boolean resultado = validador.droneEstaDisponible(drone);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("rechaza un drone null con mensaje claro")
        void droneNulo_lanzaExcepcion() {
            // Arrange
            Drone drone = null;

            // Act / Assert
            assertThrows(NullPointerException.class, () -> validador.droneEstaDisponible(drone));
        }
    }
}
