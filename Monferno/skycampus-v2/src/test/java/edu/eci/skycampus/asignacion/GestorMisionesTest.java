package edu.eci.skycampus.asignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoMision;
import edu.eci.skycampus.modelo.Mision;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.SolicitudReparto;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@DisplayName("GestorMisiones (contexto del Strategy)")
class GestorMisionesTest {
    private List<Drone> flota;
    private SolicitudReparto solicitud;

    @BeforeEach
    void setUp() {
        flota = List.of(Datos.drone("D-01", TipoDrone.MINI, 60), Datos.drone("D-02", TipoDrone.EXPRESS, 90));
        solicitud = Datos.solicitud("S-7", 300, Prioridad.NORMAL);
    }

    static Stream<Arguments> estrategias() {
        EstrategiaAsignacion siempreElUltimo = (flotaDisponible, paquete) ->
                flotaDisponible.isEmpty() ? Optional.empty() : Optional.of(flotaDisponible.get(flotaDisponible.size() - 1));
        return Stream.of(
                Arguments.of("mayor batería", new AsignacionMayorBateria(), "D-02"),
                Arguments.of("menor uso", new AsignacionMenorUso(), "D-01"),
                Arguments.of("tipo compatible", new AsignacionTipoCompatible(), "D-01"),
                Arguments.of("estrategia nueva escrita en la prueba", siempreElUltimo, "D-02"));
    }

    @ParameterizedTest(name = "funciona sin cambios con la estrategia «{0}»")
    @MethodSource("estrategias")
    void crearMision_cualquierEstrategia_usaElDroneQueEllaElige(String nombre, EstrategiaAsignacion estrategia,
                                                                 String esperado) {
        // Arrange
        GestorMisiones gestor = new GestorMisiones(estrategia);

        // Act
        Mision mision = gestor.crearMision(flota, solicitud, Datos.AHORA).orElseThrow();

        // Assert
        assertEquals(esperado, mision.drone().id());
    }

    @Test
    @DisplayName("la misión nace PENDIENTE con el ID derivado de la solicitud")
    void crearMision_droneElegido_misionPendiente() {
        // Arrange
        GestorMisiones gestor = new GestorMisiones(new AsignacionMayorBateria());

        // Act
        Mision mision = gestor.crearMision(flota, solicitud, Datos.AHORA).orElseThrow();

        // Assert
        assertEquals("M-S-7", mision.id());
        assertEquals(EstadoMision.PENDIENTE, mision.estado());
        assertEquals(Datos.AHORA, mision.creadaEn());
    }

    @Test
    @DisplayName("si la estrategia no elige, no se crea misión")
    void crearMision_estrategiaSinResultado_vacio() {
        // Arrange
        GestorMisiones gestor = new GestorMisiones((flotaDisponible, paquete) -> Optional.empty());

        // Act / Assert
        assertTrue(gestor.crearMision(flota, solicitud, Datos.AHORA).isEmpty());
    }

    @Test
    @DisplayName("exige una estrategia")
    void crearGestor_estrategiaNula_lanzaExcepcion() {
        // Act
        NullPointerException error = assertThrows(NullPointerException.class, () -> new GestorMisiones(null));

        // Assert
        assertEquals("estrategia no puede ser null", error.getMessage());
    }
}
