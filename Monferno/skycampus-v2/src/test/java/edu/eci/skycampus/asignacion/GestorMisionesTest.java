package edu.eci.skycampus.asignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;
import edu.eci.skycampus.modelo.EstadoMision;
import edu.eci.skycampus.modelo.Mision;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.SolicitudReparto;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Flota diseñada para que cada criterio elija un drone distinto (sin empates que dependan del orden):
 * mayor batería → D-02, menor uso → D-03, tipo compatible → D-01, la estrategia escrita en la prueba → D-04.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GestorMisiones (contexto del Strategy)")
class GestorMisionesTest {
    @Mock
    private EstrategiaAsignacion estrategiaSimulada;

    private List<Drone> flota;
    private SolicitudReparto solicitud;

    @BeforeEach
    void setUp() {
        flota = List.of(
                Datos.drone("D-01", TipoDrone.MINI, 50, EstadoDrone.DISPONIBLE, 300),
                Datos.drone("D-02", TipoDrone.EXPRESS, 95, EstadoDrone.DISPONIBLE, 200),
                Datos.drone("D-03", TipoDrone.CARGO, 70, EstadoDrone.DISPONIBLE, 5),
                Datos.drone("D-04", TipoDrone.MINI, 40, EstadoDrone.DISPONIBLE, 800));
        solicitud = Datos.solicitud("S-7", 300, Prioridad.NORMAL);
    }

    static Stream<Arguments> estrategias() {
        EstrategiaAsignacion siempreElUltimo = (flotaDisponible, paquete) -> flotaDisponible.isEmpty()
                ? Optional.empty() : Optional.of(flotaDisponible.get(flotaDisponible.size() - 1));
        return Stream.of(
                Arguments.of("mayor batería", new AsignacionMayorBateria(), "D-02"),
                Arguments.of("menor uso", new AsignacionMenorUso(), "D-03"),
                Arguments.of("tipo compatible", new AsignacionTipoCompatible(), "D-01"),
                Arguments.of("estrategia nueva escrita en la prueba", siempreElUltimo, "D-04"));
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

    @ParameterizedTest(name = "con flota vacía y la estrategia «{0}» no se crea misión")
    @MethodSource("estrategias")
    void crearMision_flotaVacia_vacio(String nombre, EstrategiaAsignacion estrategia, String ignorado) {
        // Arrange
        GestorMisiones gestor = new GestorMisiones(estrategia);

        // Act
        Optional<Mision> mision = gestor.crearMision(List.of(), solicitud, Datos.AHORA);

        // Assert
        assertTrue(mision.isEmpty());
    }

    @Test
    @DisplayName("le pasa a la estrategia exactamente la flota y el paquete de la solicitud")
    void crearMision_estrategiaSimulada_recibeFlotaYPaquete() {
        // Arrange
        when(estrategiaSimulada.seleccionar(any(), any())).thenReturn(Optional.of(flota.get(0)));
        GestorMisiones gestor = new GestorMisiones(estrategiaSimulada);

        // Act
        gestor.crearMision(flota, solicitud, Datos.AHORA);

        // Assert
        verify(estrategiaSimulada).seleccionar(flota, solicitud.paquete());
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
    @DisplayName("rechaza una flota null aunque la estrategia no la valide")
    void crearMision_flotaNula_lanzaExcepcion() {
        // Arrange
        GestorMisiones gestor = new GestorMisiones((flotaDisponible, paquete) -> Optional.empty());

        // Act
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> gestor.crearMision(null, solicitud, Datos.AHORA));

        // Assert
        assertEquals("flota no puede ser null", error.getMessage());
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
