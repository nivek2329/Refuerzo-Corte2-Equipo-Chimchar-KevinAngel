package edu.eci.skycampus.notificacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("GestorFlota (sujeto del Observer)")
class GestorFlotaTest {
    @Mock
    private ObservadorDrone cuartoObservador;

    private GestorFlota gestor;
    private PanelOperador panel;
    private SistemaLog log;
    private AlertaTecnico alertaTecnico;
    private Drone drone;

    @BeforeEach
    void setUp() {
        gestor = new GestorFlota();
        panel = new PanelOperador();
        log = new SistemaLog(Clock.fixed(Datos.AHORA.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
        alertaTecnico = new AlertaTecnico();
        gestor.suscribir(panel);
        gestor.suscribir(log);
        gestor.suscribir(alertaTecnico);
        drone = Datos.drone("D-07", TipoDrone.EXPRESS, 80);
    }

    @Test
    @DisplayName("un cambio de estado llega al panel y al log")
    void cambiarEstado_enVuelo_notificaPanelYLog() {
        // Act
        gestor.cambiarEstado(drone, EstadoDrone.EN_VUELO);

        // Assert
        assertEquals(List.of("D-07 · EN_VUELO"), panel.avisos());
        assertEquals(List.of("2026-10-08T10:00 | D-07 | EN_VUELO"), log.registros());
    }

    @Test
    @DisplayName("el técnico solo recibe orden cuando el drone entra en FALLO")
    void cambiarEstado_soloFallo_generaOrdenTecnico() {
        // Act
        gestor.cambiarEstado(drone, EstadoDrone.EN_VUELO);
        gestor.cambiarEstado(drone, EstadoDrone.FALLO);

        // Assert
        assertEquals(List.of("Revisar D-07 (EXPRESS): entró en FALLO"), alertaTecnico.ordenes());
    }

    @Test
    @DisplayName("devuelve el drone con el nuevo estado")
    void cambiarEstado_aterrizando_devuelveDroneActualizado() {
        // Act
        Drone actualizado = gestor.cambiarEstado(drone, EstadoDrone.ATERRIZANDO);

        // Assert
        assertEquals(EstadoDrone.ATERRIZANDO, actualizado.estado());
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

    @Test
    @DisplayName("rechaza observadores null")
    void suscribir_null_lanzaExcepcion() {
        // Act
        NullPointerException error = assertThrows(NullPointerException.class, () -> gestor.suscribir(null));

        // Assert
        assertEquals("observador no puede ser null", error.getMessage());
    }

    @Test
    @DisplayName("los avisos que se exponen no se pueden modificar desde afuera")
    void avisos_copiaInmodificable_lanzaExcepcionAlModificar() {
        // Arrange
        gestor.cambiarEstado(drone, EstadoDrone.EN_VUELO);

        // Act / Assert
        assertThrows(UnsupportedOperationException.class, () -> panel.avisos().add("x"));
        assertTrue(alertaTecnico.ordenes().isEmpty());
    }
}
