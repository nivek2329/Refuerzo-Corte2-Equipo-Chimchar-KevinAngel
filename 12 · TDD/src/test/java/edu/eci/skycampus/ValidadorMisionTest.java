package edu.eci.skycampus;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        void tieneBateriaSuficiente_bateriaEnElMinimo_retornaTrue() {
            // Arrange
            Drone drone = new Drone("D-01", "DJI Mini 3", 30, true, "Bloque A");

            // Act
            boolean resultado = validador.tieneBateriaSuficiente(drone);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("acepta batería en el máximo de 100%")
        void tieneBateriaSuficiente_bateriaEnElMaximo_retornaTrue() {
            // Arrange
            Drone drone = new Drone("D-03", "DJI Mini 3", 100, true, "Bloque C");

            // Act
            boolean resultado = validador.tieneBateriaSuficiente(drone);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("rechaza batería inferior al mínimo")
        void tieneBateriaSuficiente_bateriaInferiorAlMinimo_retornaFalse() {
            // Arrange
            Drone drone = new Drone("D-04", "DJI Mini 3", 29, true, "Bloque B");

            // Act
            boolean resultado = validador.tieneBateriaSuficiente(drone);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("rechaza un drone null con mensaje claro")
        void tieneBateriaSuficiente_droneNulo_lanzaExcepcionConMensaje() {
            // Arrange
            Drone drone = null;

            // Act
            NullPointerException error = assertThrows(NullPointerException.class,
                    () -> validador.tieneBateriaSuficiente(drone));

            // Assert
            assertEquals("drone no puede ser null", error.getMessage());
        }
    }

    @Nested
    @DisplayName("validarDestino")
    class ValidarDestinoTests {
        @ParameterizedTest(name = "acepta el destino del catálogo \"{0}\"")
        @ValueSource(strings = {"Bloque A", "Bloque B", "Bloque C", "Bloque D", "Biblioteca"})
        void validarDestino_destinoDelCatalogo_noLanzaExcepcion(String destino) {
            // Arrange: destino recibido por parámetro

            // Act / Assert
            assertDoesNotThrow(() -> validador.validarDestino(destino));
        }

        @Test
        @DisplayName("rechaza un destino fuera del catálogo con mensaje descriptivo")
        void validarDestino_destinoFueraDelCatalogo_lanzaDestinoInvalidoConMensaje() {
            // Arrange
            String destino = "Edificio Inexistente";

            // Act
            DestinoInvalidoException error = assertThrows(DestinoInvalidoException.class,
                    () -> validador.validarDestino(destino));

            // Assert
            assertEquals("Destino no válido: Edificio Inexistente", error.getMessage());
        }

        @ParameterizedTest(name = "rechaza la variante no exacta \"{0}\"")
        @ValueSource(strings = {"biblioteca", " Biblioteca ", ""})
        void validarDestino_textoNoExacto_lanzaDestinoInvalido(String destino) {
            // Arrange: destino recibido por parámetro

            // Act / Assert
            assertThrows(DestinoInvalidoException.class, () -> validador.validarDestino(destino));
        }

        @Test
        @DisplayName("rechaza un destino null con mensaje claro")
        void validarDestino_destinoNulo_lanzaExcepcionConMensaje() {
            // Arrange
            String destino = null;

            // Act
            NullPointerException error = assertThrows(NullPointerException.class,
                    () -> validador.validarDestino(destino));

            // Assert
            assertEquals("destino no puede ser null", error.getMessage());
        }
    }

    @Nested
    @DisplayName("droneEstaDisponible")
    class DroneEstaDisponibleTests {
        @Test
        @DisplayName("informa disponible cuando el indicador es true")
        void droneEstaDisponible_indicadorTrue_retornaTrue() {
            // Arrange
            Drone drone = new Drone("D-01", "DJI Mini 3", 85, true, "Bloque A");

            // Act
            boolean resultado = validador.droneEstaDisponible(drone);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("informa no disponible cuando el indicador es false")
        void droneEstaDisponible_indicadorFalse_retornaFalse() {
            // Arrange
            Drone drone = new Drone("D-02", "DJI Mini 3", 42, false, "Biblioteca");

            // Act
            boolean resultado = validador.droneEstaDisponible(drone);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("rechaza un drone null con mensaje claro")
        void droneEstaDisponible_droneNulo_lanzaExcepcionConMensaje() {
            // Arrange
            Drone drone = null;

            // Act
            NullPointerException error = assertThrows(NullPointerException.class,
                    () -> validador.droneEstaDisponible(drone));

            // Assert
            assertEquals("drone no puede ser null", error.getMessage());
        }
    }
}
