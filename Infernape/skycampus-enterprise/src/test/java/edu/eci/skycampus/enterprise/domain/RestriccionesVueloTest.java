package edu.eci.skycampus.enterprise.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Tensión RF-12 (radio que configura el coordinador) vs RNF-09 (límites de la Aerocivil). Siempre gana RNF-09. */
class RestriccionesVueloTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final LimitesAerocivil AEROCIVIL = LimitesAerocivil.urbanos(8.0);

    @Test
    void rf12_radioConfiguradoMayorQueElDeAerocivil_seAplicaElDeAerocivilYSeAvisa() {
        ConfiguracionVueloSede config = new ConfiguracionVueloSede(ECI, 12.0);

        assertEquals(8.0, config.radioEfectivoKm(AEROCIVIL));
        assertTrue(config.recortadaPorAerocivil(AEROCIVIL));
        assertEquals(12.0, config.radioConfiguradoKm(), "se conserva lo que pidió el coordinador");
    }

    @Test
    void rf12_radioConfiguradoMenor_seRespetaLaDecisionDelCoordinador() {
        ConfiguracionVueloSede config = new ConfiguracionVueloSede(ECI, 5.0);

        assertEquals(5.0, config.radioEfectivoKm(AEROCIVIL));
        assertFalse(config.recortadaPorAerocivil(AEROCIVIL));
    }

    @ParameterizedTest(name = "{0} km a {1} m (urbana={2}) -> permitido={3}")
    @CsvSource({"7.9,120,true,true", "8.1,100,true,false", "6,121,true,false", "6,150,false,true", "6,-1,false,false"})
    void rnf09_tramoDentroDelRadioEfectivoYBajo120mEnZonaUrbana(double km, int altura, boolean urbana, boolean ok) {
        ConfiguracionVueloSede config = new ConfiguracionVueloSede(ECI, 12.0);

        assertEquals(ok, config.permiteTramo(km, altura, urbana, AEROCIVIL));
    }

    @Test
    void limitesInvalidos_seRechazan() {
        assertThrows(IllegalArgumentException.class, () -> new LimitesAerocivil(5, 130));
        assertThrows(IllegalArgumentException.class, () -> LimitesAerocivil.urbanos(0));
        assertThrows(IllegalArgumentException.class, () -> new ConfiguracionVueloSede(ECI, -1));
        assertThrows(NullPointerException.class, () -> new ConfiguracionVueloSede(ECI, 3).radioEfectivoKm(null));
    }

    @Test
    void resultadoAsignacion_tieneDroneOMotivoNuncaAmbos() {
        Drone drone = new Drone("D-1", ECI, 50, 2, true, false);

        assertTrue(ResultadoAsignacion.asignado(drone).fueAsignado());
        assertFalse(ResultadoAsignacion.rechazado(MotivoRechazo.CLIMA_ADVERSO).fueAsignado());
        assertThrows(IllegalArgumentException.class,
                () -> new ResultadoAsignacion(Optional.of(drone), Optional.of(MotivoRechazo.CLIMA_ADVERSO)));
        assertThrows(IllegalArgumentException.class, () -> new ResultadoAsignacion(Optional.empty(), Optional.empty()));
        assertEquals("la Aerocivil no autorizó la ruta", MotivoRechazo.AEROCIVIL_RECHAZA.mensaje());
    }
}
