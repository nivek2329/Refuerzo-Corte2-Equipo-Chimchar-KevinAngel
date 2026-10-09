package edu.eci.skycampus.asignacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.eci.skycampus.externo.ApiMeteorologica;
import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.SolicitudReparto;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.notificacion.GestorFlota;
import edu.eci.skycampus.notificacion.ObservadorDrone;
import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * Reto 12 Monferno (TDD): los 5 casos del enunciado. Estas pruebas se escribieron ANTES que AsignadorMision.
 * La API del clima es un sistema externo, así que se simula con Mockito; el notificador también es un mock
 * suscrito a un GestorFlota real, para comprobar que la asignación dispara el Observer.
 * Los casos borde están en {@link AsignadorMisionBordesTest} y las entradas inválidas en
 * {@link AsignadorMisionValidacionTest}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AsignadorMision (TDD con Mockito): casos del enunciado")
class AsignadorMisionTest {
    @Mock
    private ApiMeteorologica clima;
    @Mock
    private ObservadorDrone notificador;

    private AsignadorMision asignador;
    private List<Drone> flota;
    private SolicitudReparto normal;

    @BeforeEach
    void setUp() {
        GestorFlota gestorFlota = new GestorFlota();
        gestorFlota.suscribir(notificador);
        asignador = new AsignadorMision(clima, gestorFlota, PoliticaAsignacion.porDefecto());
        flota = List.of(
                Datos.drone("D-01", TipoDrone.MINI, 91),
                Datos.drone("D-02", TipoDrone.EXPRESS, 60),
                Datos.drone("D-03", TipoDrone.CARGO, 85),
                Datos.drone("D-04", TipoDrone.MINI, 20));
        normal = Datos.solicitud("S-1", 200, Prioridad.NORMAL);
    }

    @Test
    @DisplayName("1a. misión NORMAL con clima apto: asigna el drone de mayor batería")
    void asignar_misionNormalClimaApto_asignaMayorBateria() {
        // Arrange
        when(clima.esApto()).thenReturn(true);

        // Act
        Optional<Drone> asignado = asignador.asignar(flota, normal);

        // Assert
        assertEquals("D-01", asignado.orElseThrow().id());
    }

    @Test
    @DisplayName("1b. el drone asignado sale en estado EN_VUELO")
    void asignar_misionNormalClimaApto_droneQuedaEnVuelo() {
        // Arrange
        when(clima.esApto()).thenReturn(true);

        // Act
        Optional<Drone> asignado = asignador.asignar(flota, normal);

        // Assert
        assertEquals(EstadoDrone.EN_VUELO, asignado.orElseThrow().estado());
    }

    @Test
    @DisplayName("1c. la asignación notifica a los observadores (Observer)")
    void asignar_misionNormalClimaApto_notificaCambioDeEstado() {
        // Arrange
        when(clima.esApto()).thenReturn(true);

        // Act
        Optional<Drone> asignado = asignador.asignar(flota, normal);

        // Assert
        verify(notificador).onEstadoCambiado(asignado.orElseThrow(), EstadoDrone.EN_VUELO);
    }

    @Test
    @DisplayName("2. clima adverso: no asigna ni notifica")
    void asignar_climaAdverso_vacioSinNotificar() {
        // Arrange
        when(clima.esApto()).thenReturn(false);

        // Act
        Optional<Drone> asignado = asignador.asignar(flota, normal);

        // Assert
        assertTrue(asignado.isEmpty());
        verify(notificador, never()).onEstadoCambiado(any(), any());
    }

    @Test
    @DisplayName("3. sin drones aptos (en vuelo o batería < 30 %): no asigna ni notifica")
    void asignar_sinDronesAptos_vacioSinNotificar() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        List<Drone> sinAptos = List.of(
                Datos.drone("D-05", TipoDrone.MINI, 95, EstadoDrone.EN_VUELO, 0),
                Datos.drone("D-06", TipoDrone.EXPRESS, 15));

        // Act
        Optional<Drone> asignado = asignador.asignar(sinAptos, normal);

        // Assert
        assertTrue(asignado.isEmpty());
        verify(notificador, never()).onEstadoCambiado(any(), any());
    }

    @Test
    @DisplayName("4. paquete más pesado que cualquier drone: excepción y ni siquiera consulta el clima")
    void asignar_paqueteMuyPesado_lanzaExcepcionSinConsultarClima() {
        // Arrange
        SolicitudReparto muyPesado = Datos.solicitud("S-4", 2500, Prioridad.NORMAL);

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> asignador.asignar(flota, muyPesado));

        // Assert
        assertEquals("el paquete pesa 2500 g y supera la capacidad del drone más grande (2000 g)", error.getMessage());
        verifyNoInteractions(clima);
    }

    @Test
    @DisplayName("5. misión URGENTE: asigna el EXPRESS aunque el MINI tenga más batería")
    void asignar_misionUrgente_asignaExpress() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        SolicitudReparto urgente = Datos.solicitud("S-5", 300, Prioridad.URGENTE);

        // Act
        Optional<Drone> asignado = asignador.asignar(flota, urgente);

        // Assert
        assertEquals("D-02", asignado.orElseThrow().id());
    }
}
