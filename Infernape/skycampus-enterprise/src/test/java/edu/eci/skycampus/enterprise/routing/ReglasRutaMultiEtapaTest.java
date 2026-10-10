package edu.eci.skycampus.enterprise.routing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.eci.skycampus.enterprise.domain.Sede;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

/** SC-15: ECI → estación de la 116 → Uniandes. RN-1 (5 km con carga) y RN-2 (30 min en estación). */
class ReglasRutaMultiEtapaTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final Sede ESTACION_116 = new Sede("EST-116", "Estación de carga calle 116");
    private static final Sede UNIANDES = new Sede("UNIANDES", "Universidad de los Andes");

    @Test
    void rutaConRecargaIntermedia_cumpleAmbasReglas() {
        assertDoesNotThrow(() -> ReglasRutaMultiEtapa.validar(
                List.of(etapa("E1", ECI, ESTACION_116, 4.8, 1.2), etapa("E2", ESTACION_116, UNIANDES, 5.0, 1.2)),
                List.of(Duration.ofMinutes(30))));
    }

    @Test
    void rn1_tramoConCargaDeMasDe5Km_seReporta() {
        List<ReglasRutaMultiEtapa.Incumplimiento> encontrados = ReglasRutaMultiEtapa.incumplimientos(
                List.of(etapa("E1", ECI, UNIANDES, 9.5, 1.2)), List.of());

        assertEquals(List.of(new ReglasRutaMultiEtapa.Incumplimiento("E1", "más de 5 km con carga sin recargar")),
                encontrados);
    }

    @Test
    void rn1_tramoLargoSinCarga_sePermite() {
        assertEquals(List.of(), ReglasRutaMultiEtapa.incumplimientos(List.of(etapa("E1", ECI, UNIANDES, 9.5, 0)),
                List.of()));
    }

    @Test
    void rn2_esperaDe31MinutosEnLaEstacion_seRechaza() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ReglasRutaMultiEtapa.validar(
                        List.of(etapa("E1", ECI, ESTACION_116, 4, 1), etapa("E2", ESTACION_116, UNIANDES, 4, 1)),
                        List.of(Duration.ofMinutes(31))));

        assertEquals("EST-116: el paquete no puede esperar más de 30 min en la estación", error.getMessage());
    }

    @Test
    void esperasQueNoCorrespondenALasEtapas_seRechazan() {
        assertThrows(IllegalArgumentException.class,
                () -> ReglasRutaMultiEtapa.incumplimientos(List.of(etapa("E1", ECI, UNIANDES, 4, 1)),
                        List.of(Duration.ZERO)));
        assertThrows(IllegalArgumentException.class, () -> ReglasRutaMultiEtapa.incumplimientos(List.of(), List.of()));
        assertEquals(1, ReglasRutaMultiEtapa.incumplimientos(
                List.of(etapa("E1", ECI, ESTACION_116, 4, 1), etapa("E2", ESTACION_116, UNIANDES, 4, 1)),
                List.of(Duration.ofMinutes(-1))).size());
    }

    private static EtapaRuta etapa(String id, Sede origen, Sede destino, double km, double cargaKg) {
        return new EtapaRuta(id, origen, destino, km, 0, 0.2, cargaKg, TipoDroneEtapa.LIGERO);
    }
}
