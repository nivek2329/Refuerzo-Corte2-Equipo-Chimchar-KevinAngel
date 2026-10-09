package edu.eci.skycampus.notificacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@DisplayName("GestorFlota (sujeto del Observer)")
class GestorFlotaTest {
    @Mock
    private ObservadorDrone cuartoObservador;

    private GestorFlota gestor;
    private Drone drone;

    @BeforeEach
    void setUp() {
        gestor = new GestorFlota();
        drone = Datos.drone("D-07", TipoDrone.EXPRESS, 80);
    }

    @Test
    @DisplayName("un 4º observador se suscribe y recibe avisos sin modificar GestorFlota")
    void suscribir_cuartoObservador_recibeNotificacionSinCambiarGestorFlota() {
        // Arrange
        gestor.suscribir(cuartoObservador);

        // Act
        Drone actualizado = gestor.cambiarEstado(drone, EstadoDrone.EN_VUELO);

        // Assert
        verify(cuartoObservador).onEstadoCambiado(actualizado, EstadoDrone.EN_VUELO);
    }

    @Test
    @DisplayName("devuelve el drone con el nuevo estado")
    void cambiarEstado_enVueloAAterrizando_devuelveDroneActualizado() {
        // Arrange
        Drone enVuelo = gestor.cambiarEstado(drone, EstadoDrone.EN_VUELO);

        // Act
        Drone aterrizando = gestor.cambiarEstado(enVuelo, EstadoDrone.ATERRIZANDO);

        // Assert
        assertEquals(EstadoDrone.ATERRIZANDO, aterrizando.estado());
    }

    @Test
    @DisplayName("si el estado no cambia, no se notifica a nadie")
    void cambiarEstado_mismoEstado_noNotifica() {
        // Arrange
        gestor.suscribir(cuartoObservador);

        // Act
        Drone resultado = gestor.cambiarEstado(drone, EstadoDrone.DISPONIBLE);

        // Assert
        assertSame(drone, resultado);
        verify(cuartoObservador, never()).onEstadoCambiado(any(), any());
    }

    @Test
    @DisplayName("una transición no permitida se rechaza y no se notifica")
    void cambiarEstado_transicionInvalida_lanzaExcepcionSinNotificar() {
        // Arrange
        gestor.suscribir(cuartoObservador);
        Drone enFallo = Datos.drone("D-07", TipoDrone.EXPRESS, 80, EstadoDrone.FALLO, 0);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> gestor.cambiarEstado(enFallo, EstadoDrone.EN_VUELO));

        // Assert
        assertEquals("transición no permitida para D-07: FALLO → EN_VUELO", error.getMessage());
        verify(cuartoObservador, never()).onEstadoCambiado(any(), any());
    }

    @Test
    @DisplayName("un observador desuscrito deja de recibir avisos")
    void desuscribir_observador_noRecibeMas() {
        // Arrange
        gestor.suscribir(cuartoObservador);
        gestor.desuscribir(cuartoObservador);

        // Act
        gestor.cambiarEstado(drone, EstadoDrone.EN_VUELO);

        // Assert
        verify(cuartoObservador, never()).onEstadoCambiado(any(), any());
    }

    @Test
    @DisplayName("suscribir dos veces el mismo observador no duplica avisos")
    void suscribir_mismoObservadorDosVeces_unSoloAviso() {
        // Arrange
        gestor.suscribir(cuartoObservador);
        gestor.suscribir(cuartoObservador);

        // Act
        Drone actualizado = gestor.cambiarEstado(drone, EstadoDrone.EN_VUELO);

        // Assert
        verify(cuartoObservador, times(1)).onEstadoCambiado(actualizado, EstadoDrone.EN_VUELO);
    }

    static Stream<Arguments> entradasNulas() {
        GestorFlota sujeto = new GestorFlota();
        Drone unDrone = Datos.drone("D-01", TipoDrone.MINI, 80);
        return Stream.of(
                Arguments.of("suscribir", (Executable) () -> sujeto.suscribir(null), "observador no puede ser null"),
                Arguments.of("desuscribir", (Executable) () -> sujeto.desuscribir(null),
                        "observador no puede ser null"),
                Arguments.of("cambiarEstado(drone)", (Executable) () -> sujeto.cambiarEstado(null, EstadoDrone.FALLO),
                        "drone no puede ser null"),
                Arguments.of("cambiarEstado(nuevo)", (Executable) () -> sujeto.cambiarEstado(unDrone, null),
                        "nuevo no puede ser null"));
    }

    @ParameterizedTest(name = "{0} rechaza null con mensaje claro")
    @MethodSource("entradasNulas")
    void metodosPublicos_entradaNull_lanzaExcepcionConMensaje(String metodo, Executable llamada, String mensaje) {
        // Act
        NullPointerException error = assertThrows(NullPointerException.class, llamada);

        // Assert
        assertEquals(mensaje, error.getMessage());
    }
}
