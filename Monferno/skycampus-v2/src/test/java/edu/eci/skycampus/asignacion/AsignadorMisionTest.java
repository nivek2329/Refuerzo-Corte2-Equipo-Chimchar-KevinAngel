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
 * Reto 12 Monferno (TDD): estas pruebas se escribieron ANTES que AsignadorMision.
 * La API del clima es un sistema externo, así que se simula con Mockito; el notificador también es un mock
 * suscrito a un GestorFlota real, para comprobar que la asignación dispara el Observer.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AsignadorMision (TDD con Mockito)")
class AsignadorMisionTest {
    @Mock
    private ApiMeteorologica clima;
    @Mock
    private ObservadorDrone notificador;

    private AsignadorMision asignador;
    private List<Drone> flota;

    @BeforeEach
    void setUp() {
        GestorFlota gestorFlota = new GestorFlota();
        gestorFlota.suscribir(notificador);
        asignador = new AsignadorMision(clima, gestorFlota, new AsignacionMayorBateria(), new AsignacionMasRapido());
        flota = List.of(
                Datos.drone("D-01", TipoDrone.MINI, 91),
                Datos.drone("D-02", TipoDrone.EXPRESS, 60),
                Datos.drone("D-03", TipoDrone.CARGO, 85),
                Datos.drone("D-04", TipoDrone.MINI, 20));
    }

    @Test
    @DisplayName("1. misión NORMAL con clima apto: asigna el de mayor batería, lo pone EN_VUELO y notifica")
    void asignar_misionNormalClimaApto_asignaMayorBateriaYNotifica() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        SolicitudReparto normal = Datos.solicitud("S-1", 200, Prioridad.NORMAL);

        // Act
        Optional<Drone> asignado = asignador.asignar(flota, normal);

        // Assert
        assertEquals("D-01", asignado.orElseThrow().id());
        assertEquals(EstadoDrone.EN_VUELO, asignado.get().estado());
        verify(notificador).onEstadoCambiado(asignado.get(), EstadoDrone.EN_VUELO);
    }

    @Test
    @DisplayName("2. clima adverso: no asigna ni notifica")
    void asignar_climaAdverso_vacioSinNotificar() {
        // Arrange
        when(clima.esApto()).thenReturn(false);
        SolicitudReparto normal = Datos.solicitud("S-2", 200, Prioridad.NORMAL);

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
        SolicitudReparto normal = Datos.solicitud("S-3", 200, Prioridad.NORMAL);

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
        assertEquals("el paquete pesa 2500 g y supera la capacidad máxima de la flota (2000 g)", error.getMessage());
        verifyNoInteractions(clima);
    }

    @Test
    @DisplayName("5. misión URGENTE: asigna el EXPRESS aunque tenga menos batería que el MINI")
    void asignar_misionUrgente_asignaExpress() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        SolicitudReparto urgente = Datos.solicitud("S-5", 300, Prioridad.URGENTE);

        // Act
        Optional<Drone> asignado = asignador.asignar(flota, urgente);

        // Assert
        assertEquals("D-02", asignado.orElseThrow().id());
        assertEquals(TipoDrone.EXPRESS, asignado.get().tipo());
    }

    @Test
    @DisplayName("borde: un paquete de exactamente 2000 g sí se asigna (al CARGO)")
    void asignar_pesoIgualAlMaximo_asignaCargo() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        SolicitudReparto limite = Datos.solicitud("S-6", 2000, Prioridad.NORMAL);

        // Act
        Optional<Drone> asignado = asignador.asignar(flota, limite);

        // Assert
        assertEquals("D-03", asignado.orElseThrow().id());
    }

    @Test
    @DisplayName("borde: URGENTE sin EXPRESS apto usa el siguiente más rápido (MINI antes que CARGO)")
    void asignar_urgenteSinExpress_asignaElSiguienteMasRapido() {
        // Arrange
        when(clima.esApto()).thenReturn(true);
        List<Drone> sinExpress = List.of(Datos.drone("D-03", TipoDrone.CARGO, 99), Datos.drone("D-01", TipoDrone.MINI, 50));
        SolicitudReparto urgente = Datos.solicitud("S-7", 300, Prioridad.URGENTE);

        // Act
        Optional<Drone> asignado = asignador.asignar(sinExpress, urgente);

        // Assert
        assertEquals("D-01", asignado.orElseThrow().id());
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
