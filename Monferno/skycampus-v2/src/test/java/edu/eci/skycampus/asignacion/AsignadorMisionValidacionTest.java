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

import java.util.List;
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
        asignador = new AsignadorMision(clima, new GestorFlota(), new AsignacionMayorBateria(), new AsignacionMasRapido());
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

    static Stream<Arguments> constructoresConNull() {
        ApiMeteorologica api = () -> true;
        GestorFlota gestor = new GestorFlota();
        EstrategiaAsignacion normal = new AsignacionMayorBateria();
        EstrategiaAsignacion urgente = new AsignacionMasRapido();
        return Stream.of(
                Arguments.of((Executable) () -> new AsignadorMision(null, gestor, normal, urgente),
                        "clima no puede ser null"),
                Arguments.of((Executable) () -> new AsignadorMision(api, null, normal, urgente),
                        "gestorFlota no puede ser null"),
                Arguments.of((Executable) () -> new AsignadorMision(api, gestor, null, urgente),
                        "estrategiaNormal no puede ser null"),
                Arguments.of((Executable) () -> new AsignadorMision(api, gestor, normal, null),
                        "estrategiaUrgente no puede ser null"));
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
