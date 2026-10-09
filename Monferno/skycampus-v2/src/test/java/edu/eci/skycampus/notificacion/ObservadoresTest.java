package edu.eci.skycampus.notificacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;
import edu.eci.skycampus.modelo.TipoDrone;
import edu.eci.skycampus.soporte.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.List;

@DisplayName("Suscriptores: PanelOperador, SistemaLog y AlertaTecnico")
class ObservadoresTest {
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
    @DisplayName("un despegue llega al panel y al log, pero no al técnico")
    void cambiarEstado_enVuelo_notificaPanelYLogSinOrdenTecnico() {
        // Act
        gestor.cambiarEstado(drone, EstadoDrone.EN_VUELO);

        // Assert
        assertEquals(List.of("D-07 · EN_VUELO"), panel.avisos());
        assertEquals(List.of("2026-10-08T10:00 | D-07 | EN_VUELO"), log.registros());
        assertTrue(alertaTecnico.ordenes().isEmpty());
    }

    @Test
    @DisplayName("un FALLO llega a los tres suscriptores a la vez")
    void cambiarEstado_fallo_notificaALosTres() {
        // Act
        gestor.cambiarEstado(drone, EstadoDrone.FALLO);

        // Assert
        assertEquals(List.of("D-07 · FALLO"), panel.avisos());
        assertEquals(List.of("2026-10-08T10:00 | D-07 | FALLO"), log.registros());
        assertEquals(List.of("Revisar D-07 (EXPRESS): entró en FALLO"), alertaTecnico.ordenes());
    }

    @Test
    @DisplayName("los avisos del panel no se pueden modificar desde afuera")
    void avisos_copiaExpuesta_esInmodificable() {
        // Arrange
        List<String> avisos = panel.avisos();

        // Act / Assert
        assertThrows(UnsupportedOperationException.class, () -> avisos.add("x"));
    }

    @Test
    @DisplayName("los registros del log no se pueden modificar desde afuera")
    void registros_copiaExpuesta_esInmodificable() {
        // Arrange
        List<String> registros = log.registros();

        // Act / Assert
        assertThrows(UnsupportedOperationException.class, () -> registros.add("x"));
    }

    @Test
    @DisplayName("las órdenes del técnico no se pueden modificar desde afuera")
    void ordenes_copiaExpuesta_esInmodificable() {
        // Arrange
        List<String> ordenes = alertaTecnico.ordenes();

        // Act / Assert
        assertThrows(UnsupportedOperationException.class, () -> ordenes.add("x"));
    }

    @Test
    @DisplayName("el log exige un reloj")
    void crearSistemaLog_relojNulo_lanzaExcepcion() {
        // Act
        NullPointerException error = assertThrows(NullPointerException.class, () -> new SistemaLog(null));

        // Assert
        assertEquals("reloj no puede ser null", error.getMessage());
    }
}
