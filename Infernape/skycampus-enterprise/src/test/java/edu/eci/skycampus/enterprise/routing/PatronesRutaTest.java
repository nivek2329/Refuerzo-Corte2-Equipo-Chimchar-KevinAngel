package edu.eci.skycampus.enterprise.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.eci.skycampus.enterprise.domain.Sede;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PatronesRutaTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final Sede UNAL = new Sede("UNAL", "Universidad Nacional");
    private static final Sede UNIANDES = new Sede("UNIANDES", "Universidad de los Andes");
    private static final Instant INICIO = Instant.parse("2026-10-01T10:00:00Z");

    @Test
    void gestorEjecutaRutaCompuestaYNotificaCadaEtapa() {
        List<EventoEtapaRuta> eventos = new ArrayList<>();
        Ruta ruta = new RutaCompuesta("R-01", List.of(tramo("E1", ECI, UNAL, 60, 0, 0.2, 1, TipoDroneEtapa.LIGERO),
                tramo("E2", UNAL, UNIANDES, 24, 90, 0.4, 8, TipoDroneEtapa.CARGA)));
        ResultadoRutaPlanificada resultado = gestor(eventos).planificar(List.of(ruta), sinViento(), INICIO);

        assertEquals(3.0, resultado.tiempoEstimadoHoras(), 1e-9);
        assertEquals(List.of(TipoDroneEtapa.LIGERO, TipoDroneEtapa.CARGA),
                resultado.drones().stream().map(DroneEtapa::tipo).toList());
        assertEquals(List.of("E1", "E2"), eventos.stream().map(EventoEtapaRuta::etapaId).toList());
        assertEquals(INICIO.plusSeconds(7_200), eventos.get(1).inicio());
        assertEquals(ECI, resultado.ruta().origen());
        assertEquals(UNIANDES, resultado.ruta().destino());
        assertEquals(0.4, resultado.ruta().nivelRiesgo(), 1e-9);
        assertEquals(2, resultado.ruta().cantidadEtapas());
    }

    @Test
    void gestorEjecutaRutaSimplePorLaMismaInterfaz() {
        Ruta ruta = tramo("SIMPLE", ECI, UNAL, 60, 0, 0.1, 1, TipoDroneEtapa.LIGERO);
        ResultadoRutaPlanificada resultado = gestor(new ArrayList<>())
                .planificar(List.of(ruta), sinViento(), INICIO);

        assertEquals(2.0, resultado.tiempoEstimadoHoras(), 1e-9);
        assertEquals(1, resultado.drones().size());
    }

    @Test
    void estrategiasEligenEntreVelocidadYRiesgo() {
        Ruta rapida = tramo("RAPIDA", ECI, UNAL, 30, 0, 0.8, 1, TipoDroneEtapa.LIGERO);
        Ruta segura = tramo("SEGURA", ECI, UNAL, 60, 0, 0.1, 1, TipoDroneEtapa.LIGERO);

        assertEquals("RAPIDA", new RutaMasRapida().elegir(List.of(segura, rapida), sinViento()).id());
        assertEquals("SEGURA", new RutaMasSegura().elegir(List.of(rapida, segura), sinViento()).id());
    }

    @Test
    void compositeRechazaEtapasDesconectadas() {
        Ruta primera = tramo("E1", ECI, UNAL, 10, 0, 0.1, 1, TipoDroneEtapa.LIGERO);
        Ruta segunda = tramo("E2", ECI, UNIANDES, 10, 0, 0.1, 1, TipoDroneEtapa.LIGERO);

        assertThrows(IllegalArgumentException.class, () -> new RutaCompuesta("R-02", List.of(primera, segunda)));
    }

    @Test
    void factoryMethodRechazaCargaSuperiorALaCapacidad() {
        EtapaRuta etapa = etapa("E3", ECI, UNAL, 10, 0, 0.1, 3, TipoDroneEtapa.LIGERO);

        assertThrows(IllegalArgumentException.class, () -> fabricaDrones().crear(etapa));
    }

    @Test
    void estrategiaRechazaListaVaciaYFactoryFaltaTipo() {
        assertThrows(IllegalArgumentException.class, () -> new RutaMasRapida().elegir(List.of(), sinViento()));
        EtapaRuta carga = etapa("E4", ECI, UNAL, 10, 0, 0.1, 8, TipoDroneEtapa.CARGA);
        FabricaDronesPorEtapa soloLigero = new FabricaDronesPorEtapa(List.of(new FabricaDroneLigero()));

        assertThrows(IllegalArgumentException.class, () -> soloLigero.crear(carga));
    }

    private static GestorMisiones gestor(List<EventoEtapaRuta> eventos) {
        return new GestorMisiones(new RutaMasRapida(), fabricaDrones(), List.of(eventos::add));
    }

    private static FabricaDronesPorEtapa fabricaDrones() {
        return new FabricaDronesPorEtapa(List.of(new FabricaDroneLigero(), new FabricaDroneCarga()));
    }

    private static CondicionesViento sinViento() {
        return new CondicionesViento(0, 0);
    }

    private static TramoRuta tramo(String id, Sede origen, Sede destino, double distancia, double rumbo,
                                   double riesgo, double carga, TipoDroneEtapa tipo) {
        return new TramoRuta(etapa(id, origen, destino, distancia, rumbo, riesgo, carga, tipo),
                new CalculadorRutaInterSede());
    }

    private static EtapaRuta etapa(String id, Sede origen, Sede destino, double distancia, double rumbo,
                                   double riesgo, double carga, TipoDroneEtapa tipo) {
        return new EtapaRuta(id, origen, destino, distancia, rumbo, riesgo, carga, tipo);
    }
}
