package edu.eci.skycampus.asignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import edu.eci.skycampus.externo.ApiMeteorologica;
import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.SolicitudReparto;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.notificacion.GestorFlota;
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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@DisplayName("AsignadorMision: validación de entradas")
class AsignadorMisionValidacionTest {
    @Mock
    private ApiMeteorologica clima;

    private AsignadorMision asignador;
    private List<Drone> flota;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorMision(clima, new GestorFlota(), PoliticaAsignacion.porDefecto());
        flota = List.of(Datos.drone("D-01", TipoDrone.MINI, 91));
    }

    @Test
    @DisplayName("rechaza una solicitud null con mensaje claro")
    void asignar_solicitudNula_lanzaExcepcion() {
        // Act
        NullPointerException error = assertThrows(NullPointerException.class, () -> asignador.asignar(flota, null));

        // Assert
        assertEquals("solicitud no puede ser null", error.getMessage());
        verifyNoInteractions(clima);
    }

    @Test
    @DisplayName("rechaza una flota null sin consultar el clima")
    void asignar_flotaNula_lanzaExcepcionSinConsultarClima() {
        // Arrange
        SolicitudReparto solicitud = Datos.solicitud("S-1", 200, Prioridad.NORMAL);

        // Act
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> asignador.asignar(null, solicitud));

        // Assert
        assertEquals("flota no puede ser null", error.getMessage());
        verifyNoInteractions(clima);
    }

    @Test
    @DisplayName("rechaza una flota con un drone null, con mensaje y sin consultar el clima")
    void asignar_flotaConDroneNulo_lanzaExcepcionSinConsultarClima() {
        // Arrange
        List<Drone> conNulo = Arrays.asList(Datos.drone("D-01", TipoDrone.MINI, 91), null);
        SolicitudReparto solicitud = Datos.solicitud("S-2", 200, Prioridad.NORMAL);

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> asignador.asignar(conNulo, solicitud));

        // Assert
        assertEquals("la flota no puede contener drones null", error.getMessage());
        verifyNoInteractions(clima);
    }

    @Test
    @DisplayName("exige una estrategia para cada prioridad al construirse")
    void crearAsignador_faltaEstrategiaParaBajo_lanzaExcepcion() {
        // Arrange
        Map<Prioridad, EstrategiaAsignacion> sinBajo = new EnumMap<>(PoliticaAsignacion.porDefecto());
        sinBajo.remove(Prioridad.BAJO);
        GestorFlota gestor = new GestorFlota();

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new AsignadorMision(clima, gestor, sinBajo));

        // Assert
        assertEquals("falta la estrategia para la prioridad BAJO", error.getMessage());
    }

    @Test
    @DisplayName("rechaza estrategias con una prioridad null, con mensaje claro")
    void crearAsignador_prioridadNula_lanzaExcepcion() {
        // Arrange
        Map<Prioridad, EstrategiaAsignacion> conClaveNula = new HashMap<>(PoliticaAsignacion.porDefecto());
        conClaveNula.put(null, new AsignacionMenorUso());
        GestorFlota gestor = new GestorFlota();

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new AsignadorMision(clima, gestor, conClaveNula));

        // Assert
        assertEquals("las estrategias no pueden tener una prioridad null", error.getMessage());
    }

    static Stream<Arguments> constructoresConNull() {
        ApiMeteorologica api = () -> true;
        GestorFlota gestor = new GestorFlota();
        Map<Prioridad, EstrategiaAsignacion> estrategias = PoliticaAsignacion.porDefecto();
        return Stream.of(
                Arguments.of((Executable) () -> new AsignadorMision(null, gestor, estrategias),
                        "clima no puede ser null"),
                Arguments.of((Executable) () -> new AsignadorMision(api, null, estrategias),
                        "gestorFlota no puede ser null"),
                Arguments.of((Executable) () -> new AsignadorMision(api, gestor, null),
                        "estrategiasPorPrioridad no puede ser null"));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("constructoresConNull")
    void crearAsignador_dependenciaNula_lanzaExcepcionConMensaje(Executable creacion, String mensaje) {
        // Act
        NullPointerException error = assertThrows(NullPointerException.class, creacion);

        // Assert
        assertEquals(mensaje, error.getMessage());
    }
}
