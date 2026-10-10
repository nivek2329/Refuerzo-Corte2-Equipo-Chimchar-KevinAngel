package edu.eci.skycampus.enterprise.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.EstadoMision;
import edu.eci.skycampus.enterprise.domain.EstrategiaAsignacion;
import edu.eci.skycampus.enterprise.domain.ObservadorDrone;
import edu.eci.skycampus.enterprise.domain.PrioridadMision;
import edu.eci.skycampus.enterprise.domain.RepositorioFlota;
import edu.eci.skycampus.enterprise.domain.Sede;
import edu.eci.skycampus.enterprise.domain.ServicioClima;
import edu.eci.skycampus.enterprise.domain.SolicitudAsignacion;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Capa de aplicación aislada: los cuatro puertos son mocks; no hay HTTP, base de datos ni adaptadores reales. */
@ExtendWith(MockitoExtension.class)
class AsignadorMisionTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final SolicitudAsignacion SOLICITUD = new SolicitudAsignacion(
            "M-01", ECI, "ECI", "UNAL", 1.5, PrioridadMision.NORMAL);
    private static final Drone DRONE = new Drone("D-01", ECI, 91, 5.0, true, false);

    @Mock RepositorioFlota repositorio;
    @Mock ServicioClima clima;
    @Mock EstrategiaAsignacion estrategia;
    @Mock ObservadorDrone notificador;
    @InjectMocks AsignadorMision asignador;

    @Test
    void asignar_climaAptoYDroneElegido_registraYNotifica() {
        when(clima.condicionesAptas("ECI", "UNAL")).thenReturn(true);
        when(repositorio.findDisponibles(ECI)).thenReturn(List.of(DRONE));
        when(estrategia.elegir(List.of(DRONE), SOLICITUD)).thenReturn(Optional.of(DRONE));

        Optional<Drone> resultado = asignador.asignar(SOLICITUD);

        assertEquals(Optional.of(DRONE), resultado);
        verify(repositorio).registrarAsignacion("M-01", "D-01");
        verify(notificador).onEstadoCambiado("M-01", DRONE, EstadoMision.EN_VUELO);
    }

    @Test
    void asignar_climaAdverso_noConsultaFlotaNiAsigna() {
        when(clima.condicionesAptas("ECI", "UNAL")).thenReturn(false);

        assertTrue(asignador.asignar(SOLICITUD).isEmpty());

        verifyNoInteractions(repositorio, estrategia, notificador);
    }

    @Test
    void asignar_sinDroneQueSoportePeso_noInvocaEstrategia() {
        Drone dronePequeno = new Drone("D-02", ECI, 95, 1.0, true, false);
        when(clima.condicionesAptas("ECI", "UNAL")).thenReturn(true);
        when(repositorio.findDisponibles(ECI)).thenReturn(List.of(dronePequeno));

        assertTrue(asignador.asignar(SOLICITUD).isEmpty());

        verifyNoInteractions(estrategia, notificador);
        verify(repositorio, never()).registrarAsignacion(any(), any());
    }

    @Test
    void asignar_repositorioDevuelveDroneOcupado_loDescartaAntesDeLaEstrategia() {
        Drone ocupado = new Drone("D-03", ECI, 99, 5.0, false, false);
        when(clima.condicionesAptas("ECI", "UNAL")).thenReturn(true);
        when(repositorio.findDisponibles(ECI)).thenReturn(List.of(ocupado, DRONE));
        when(estrategia.elegir(List.of(DRONE), SOLICITUD)).thenReturn(Optional.of(DRONE));

        assertEquals(Optional.of(DRONE), asignador.asignar(SOLICITUD));
    }

    @Test
    void asignar_estrategiaSinSeleccion_noRegistraNiNotifica() {
        when(clima.condicionesAptas("ECI", "UNAL")).thenReturn(true);
        when(repositorio.findDisponibles(ECI)).thenReturn(List.of(DRONE));
        when(estrategia.elegir(List.of(DRONE), SOLICITUD)).thenReturn(Optional.empty());

        assertTrue(asignador.asignar(SOLICITUD).isEmpty());

        verify(repositorio, never()).registrarAsignacion(any(), any());
        verifyNoInteractions(notificador);
    }

    @Test
    void asignar_estrategiaDevuelveDroneNoCandidato_fallaSinRegistrarNiNotificar() {
        Drone ajeno = new Drone("D-99", ECI, 100, 5.0, true, true);
        when(clima.condicionesAptas("ECI", "UNAL")).thenReturn(true);
        when(repositorio.findDisponibles(ECI)).thenReturn(List.of(DRONE));
        when(estrategia.elegir(List.of(DRONE), SOLICITUD)).thenReturn(Optional.of(ajeno));

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> asignador.asignar(SOLICITUD));

        assertEquals("la estrategia seleccionó un drone que no es candidato", error.getMessage());
        verify(repositorio, never()).registrarAsignacion(any(), any());
        verifyNoInteractions(notificador);
    }

    @Test
    void asignar_solicitudNula_lanzaExcepcionSinTocarPuertos() {
        assertThrows(NullPointerException.class, () -> asignador.asignar(null));

        verifyNoInteractions(clima, repositorio, estrategia, notificador);
    }

    @Test
    void constructor_dependenciaNula_lanzaExcepcionConMensaje() {
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> new AsignadorMision(repositorio, null, estrategia, notificador));

        assertEquals("servicio de clima no puede ser null", error.getMessage());
        assertThrows(NullPointerException.class, () -> new AsignadorMision(null, clima, estrategia, notificador));
        assertThrows(NullPointerException.class, () -> new AsignadorMision(repositorio, clima, null, notificador));
        assertThrows(NullPointerException.class, () -> new AsignadorMision(repositorio, clima, estrategia, null));
    }
}
