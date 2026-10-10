package edu.eci.skycampus.enterprise.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.EstrategiaAsignacion;
import edu.eci.skycampus.enterprise.domain.MotivoRechazo;
import edu.eci.skycampus.enterprise.domain.ObservadorDrone;
import edu.eci.skycampus.enterprise.domain.PrioridadMision;
import edu.eci.skycampus.enterprise.domain.RepositorioFlota;
import edu.eci.skycampus.enterprise.domain.RepositorioSedes;
import edu.eci.skycampus.enterprise.domain.ResultadoAsignacion;
import edu.eci.skycampus.enterprise.domain.Sede;
import edu.eci.skycampus.enterprise.domain.ServicioAerocivil;
import edu.eci.skycampus.enterprise.domain.ServicioClima;
import edu.eci.skycampus.enterprise.domain.SolicitudAsignacion;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Capa unitaria del reto 12: los 5 flujos alternos del AsignadorMision Enterprise, cada uno con el motivo exacto
 * y comprobando que el flujo se corta ahí (no consulta lo que viene después ni asigna).
 */
@ExtendWith(MockitoExtension.class)
class AsignadorMisionFlujosAlternosTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final SolicitudAsignacion SOLICITUD = new SolicitudAsignacion(
            "M-10", ECI, "ECI", "UNAL", 2.0, PrioridadMision.NORMAL);
    private static final Drone LIGERO = new Drone("D-01", ECI, 90, 1.0, true, false);
    private static final Drone CARGA = new Drone("D-02", ECI, 70, 15.0, true, false);

    @Mock RepositorioFlota repositorio;
    @Mock ServicioClima clima;
    @Mock ServicioAerocivil aerocivil;
    @Mock RepositorioSedes sedes;
    @Mock EstrategiaAsignacion estrategia;
    @Mock ObservadorDrone notificador;
    @InjectMocks AsignadorMision asignador;

    @Test
    void flujoAlterno1_sedeInactiva_rechazaSinConsultarNadaMas() {
        when(sedes.estaActiva(ECI)).thenReturn(false);

        assertRechazo(MotivoRechazo.SEDE_INACTIVA, asignador.evaluar(SOLICITUD));
        verifyNoInteractions(clima, aerocivil, repositorio, estrategia, notificador);
    }

    @Test
    void flujoAlterno2_climaAdverso_rechazaAntesDeLaAerocivil() {
        when(sedes.estaActiva(ECI)).thenReturn(true);
        when(clima.condicionesAptas("ECI", "UNAL")).thenReturn(false);

        assertRechazo(MotivoRechazo.CLIMA_ADVERSO, asignador.evaluar(SOLICITUD));
        verifyNoInteractions(aerocivil, repositorio, estrategia, notificador);
    }

    @Test
    void flujoAlterno3_aerocivilRechaza_noConsultaLaFlota() {
        aptoHastaAerocivil(false);

        assertRechazo(MotivoRechazo.AEROCIVIL_RECHAZA, asignador.evaluar(SOLICITUD));
        verifyNoInteractions(repositorio, estrategia, notificador);
    }

    @Test
    void flujoAlterno4_sinDronesDisponibles_rechazaSinInvocarEstrategia() {
        aptoHastaAerocivil(true);
        when(repositorio.findDisponibles(ECI)).thenReturn(List.of());

        assertRechazo(MotivoRechazo.SIN_DRONES_DISPONIBLES, asignador.evaluar(SOLICITUD));
        verifyNoInteractions(estrategia, notificador);
    }

    @Test
    void flujoAlterno4b_estrategiaSinCandidatoPorPrioridad_seReportaComoSinDrones() {
        aptoHastaAerocivil(true);
        when(repositorio.findDisponibles(ECI)).thenReturn(List.of(CARGA));
        when(estrategia.elegir(List.of(CARGA), SOLICITUD)).thenReturn(Optional.empty());

        assertRechazo(MotivoRechazo.SIN_DRONES_DISPONIBLES, asignador.evaluar(SOLICITUD));
        verify(repositorio, never()).registrarAsignacion(any(), any());
    }

    @Test
    void flujoAlterno5_paqueteDemasiadoPesado_distingueDeSinDrones() {
        aptoHastaAerocivil(true);
        when(repositorio.findDisponibles(ECI)).thenReturn(List.of(LIGERO));

        assertRechazo(MotivoRechazo.PAQUETE_DEMASIADO_PESADO, asignador.evaluar(SOLICITUD));
        verifyNoInteractions(estrategia, notificador);
    }

    @Test
    void flujoBasico_todoApto_asignaElDroneQueSoportaElPeso() {
        aptoHastaAerocivil(true);
        when(repositorio.findDisponibles(ECI)).thenReturn(List.of(LIGERO, CARGA));
        when(estrategia.elegir(List.of(CARGA), SOLICITUD)).thenReturn(Optional.of(CARGA));

        ResultadoAsignacion resultado = asignador.evaluar(SOLICITUD);

        assertTrue(resultado.fueAsignado());
        assertEquals(Optional.of(CARGA), resultado.drone());
        verify(repositorio).registrarAsignacion("M-10", "D-02");
    }

    private void aptoHastaAerocivil(boolean autoriza) {
        when(sedes.estaActiva(ECI)).thenReturn(true);
        when(clima.condicionesAptas("ECI", "UNAL")).thenReturn(true);
        when(aerocivil.autorizaRuta("ECI", "UNAL")).thenReturn(autoriza);
    }

    private static void assertRechazo(MotivoRechazo esperado, ResultadoAsignacion resultado) {
        assertEquals(Optional.of(esperado), resultado.motivo());
        assertTrue(resultado.drone().isEmpty());
    }
}
