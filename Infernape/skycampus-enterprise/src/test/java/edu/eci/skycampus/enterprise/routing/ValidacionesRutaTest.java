package edu.eci.skycampus.enterprise.routing;

import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.eci.skycampus.enterprise.domain.Sede;
import edu.eci.skycampus.enterprise.reporting.FamiliaReportesSede;
import edu.eci.skycampus.enterprise.reporting.FormatoReporte;
import edu.eci.skycampus.enterprise.reporting.ReporteSede;
import edu.eci.skycampus.enterprise.reporting.TipoReporte;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ValidacionesRutaTest {
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingenieria");
    private static final Sede UNAL = new Sede("UNAL", "Universidad Nacional");
    private final CalculadorRutaInterSede calculador = new CalculadorRutaInterSede();

    @ParameterizedTest
    @MethodSource("etapasInvalidas")
    void etapaRechazaDatosInvalidos(String id, Sede origen, double distancia, double rumbo,
                                    double riesgo, double carga) {
        assertThrows(IllegalArgumentException.class,
                () -> new EtapaRuta(id, origen, UNAL, distancia, rumbo, riesgo, carga, TipoDroneEtapa.LIGERO));
    }

    @ParameterizedTest
    @MethodSource("condicionesInvalidas")
    void condicionesVientoRechazaValoresInvalidos(double velocidad, double direccion) {
        assertThrows(IllegalArgumentException.class, () -> new CondicionesViento(velocidad, direccion));
    }

    @ParameterizedTest
    @MethodSource("entradasCalculadorInvalidas")
    void calculadorRechazaEntradasNoFinitasOFueraDeRango(double distancia, double dron, double viento) {
        assertThrows(IllegalArgumentException.class,
                () -> calculador.tiempoEstimadoHoras(distancia, dron, viento, 0, 180));
    }

    @Test
    void calculadorRechazaAmbosRumbosFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> calculador.tiempoEstimadoHoras(10, 30, 0, -1, 180));
        assertThrows(IllegalArgumentException.class, () -> calculador.tiempoEstimadoHoras(10, 30, 0, 0, 360));
    }

    @Test
    void droneEtapaRechazaIdentificadorYCapacidadInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new DroneEtapa(" ", TipoDroneEtapa.LIGERO, 2));
        assertThrows(IllegalArgumentException.class, () -> new DroneEtapa("D-1", TipoDroneEtapa.LIGERO, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new DroneEtapa("D-2", TipoDroneEtapa.LIGERO, Double.NaN));
    }

    @Test
    void rutaCompuestaRechazaIdentificadorYListaVacios() {
        Ruta simple = new TramoRuta(etapa("E1", ECI, UNAL), calculador);
        assertThrows(IllegalArgumentException.class, () -> new RutaCompuesta(" ", List.of(simple)));
        assertThrows(IllegalArgumentException.class, () -> new RutaCompuesta("R-1", List.of()));
    }

    @Test
    void factoriesRechazanDuplicadosYReportesIncoherentes() {
        assertThrows(IllegalStateException.class, () -> new FabricaDronesPorEtapa(
                List.of(new FabricaDroneLigero(), new FabricaDroneLigero())));
        assertThrows(IllegalArgumentException.class, () -> new ReporteSede(" ", FormatoReporte.HTML,
                TipoReporte.RESUMEN));
    }

    @Test
    void eventoRechazaLlegadaAnteriorAlInicio() {
        Instant inicio = Instant.parse("2026-10-01T10:00:00Z");
        assertThrows(IllegalArgumentException.class, () -> new EventoEtapaRuta(
                "E1", "D-1", "ECI", "UNAL", inicio, inicio.minusSeconds(1)));
    }

    @Test
    void familiaReportesRechazaSedeFormatoOTipoIncoherente() {
        ReporteSede resumen = new ReporteSede("ECI", FormatoReporte.HTML, TipoReporte.RESUMEN);
        assertThrows(IllegalArgumentException.class, () -> new FamiliaReportesSede(resumen,
                new ReporteSede("UNAL", FormatoReporte.HTML, TipoReporte.DETALLE)));
        assertThrows(IllegalArgumentException.class, () -> new FamiliaReportesSede(resumen,
                new ReporteSede("ECI", FormatoReporte.PDF, TipoReporte.DETALLE)));
        assertThrows(IllegalArgumentException.class, () -> new FamiliaReportesSede(
                new ReporteSede("ECI", FormatoReporte.HTML, TipoReporte.DETALLE),
                new ReporteSede("ECI", FormatoReporte.HTML, TipoReporte.DETALLE)));
        assertThrows(IllegalArgumentException.class, () -> new FamiliaReportesSede(resumen,
                new ReporteSede("ECI", FormatoReporte.HTML, TipoReporte.RESUMEN)));
    }

    @Test
    void resultadoPlanificadoRechazaTiempoOConteoInconsistente() {
        Ruta ruta = new TramoRuta(etapa("E1", ECI, UNAL), calculador);
        assertThrows(IllegalArgumentException.class, () -> new ResultadoRutaPlanificada(ruta, List.of(), 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ResultadoRutaPlanificada(ruta, List.of(drone()), 0));
    }

    private static Stream<Arguments> etapasInvalidas() {
        return Stream.of(Arguments.of(" ", ECI, 10, 0, 0.1, 1), Arguments.of("E1", UNAL, 10, 0, 0.1, 1),
                Arguments.of("E1", ECI, Double.NaN, 0, 0.1, 1), Arguments.of("E1", ECI, 10, Double.NaN, 0.1, 1),
                Arguments.of("E1", ECI, 10, 0, Double.NaN, 1), Arguments.of("E1", ECI, 10, 0, 0.1, Double.NaN),
                Arguments.of("E1", ECI, 0, 0, 0.1, 1), Arguments.of("E1", ECI, 10, 360, 0.1, 1),
                Arguments.of("E1", ECI, 10, 0, -0.1, 1), Arguments.of("E1", ECI, 10, 0, 1.1, 1),
                Arguments.of("E1", ECI, 10, 0, 0.1, -1));
    }

    private static Stream<Arguments> condicionesInvalidas() {
        return Stream.of(Arguments.of(Double.NaN, 0), Arguments.of(-1, 0), Arguments.of(0, Double.NaN),
                Arguments.of(0, -1), Arguments.of(0, 360));
    }

    private static Stream<Arguments> entradasCalculadorInvalidas() {
        return Stream.of(Arguments.of(Double.NaN, 30, 0), Arguments.of(10, Double.NaN, 0),
                Arguments.of(10, 30, Double.NaN), Arguments.of(0, 30, 0),
                Arguments.of(10, 0, 0), Arguments.of(10, 30, -1));
    }

    private static EtapaRuta etapa(String id, Sede origen, Sede destino) {
        return new EtapaRuta(id, origen, destino, 10, 0, 0.1, 1, TipoDroneEtapa.LIGERO);
    }

    private static DroneEtapa drone() {
        return new DroneEtapa("D-1", TipoDroneEtapa.LIGERO, 2);
    }
}
