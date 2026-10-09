package edu.eci.skycampus.asignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.eci.skycampus.externo.ApiMeteorologica;
import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.notificacion.GestorFlota;
import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
    }

    @Test
    @DisplayName("exige la API del clima")
    void crearAsignador_climaNulo_lanzaExcepcion() {
        // Arrange
        GestorFlota gestorFlota = new GestorFlota();
        AsignacionMayorBateria normal = new AsignacionMayorBateria();
        AsignacionMasRapido urgente = new AsignacionMasRapido();

        // Act
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> new AsignadorMision(null, gestorFlota, normal, urgente));

        // Assert
        assertEquals("clima no puede ser null", error.getMessage());
    }
}
