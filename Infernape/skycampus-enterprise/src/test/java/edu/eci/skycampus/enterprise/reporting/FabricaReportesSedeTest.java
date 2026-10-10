package edu.eci.skycampus.enterprise.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.eci.skycampus.enterprise.analytics.MetricasEficienciaSede;
import edu.eci.skycampus.enterprise.domain.EstadoMision;
import edu.eci.skycampus.enterprise.domain.Mision;
import edu.eci.skycampus.enterprise.domain.PrioridadMision;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FabricaReportesSedeTest {
    private static final Instant INICIO = Instant.parse("2026-10-01T10:00:00Z");
    private static final Sede ECI = new Sede("ECI", "Escuela Colombiana de Ingeniería");
    private static final Sede UNAL = new Sede("UNAL", "Universidad Nacional");
    private static final MetricasEficienciaSede METRICAS_ECI = new MetricasEficienciaSede(
            ECI, 2, 1, 0.5, OptionalDouble.of(12.0), Optional.of("D-01"), 50.0);
    private static final List<Mision> MISIONES_ECI = List.of(
            new Mision("M-1", ECI, "D-01", PrioridadMision.URGENTE, EstadoMision.ENTREGADA, INICIO,
                    Optional.of(INICIO.plusSeconds(720))),
            new Mision("M-2", ECI, "D-01", PrioridadMision.NORMAL, EstadoMision.PENDIENTE, INICIO, Optional.empty()));

    @ParameterizedTest(name = "{0} produce una familia {1}")
    @CsvSource({"ECI,HTML", "UNAL,PDF", "UNIANDES,JSON", "EAFIT,CSV"})
    void cadaSedeObtieneResumenYDetalleDelMismoFormato(String sede, FormatoReporte formato) {
        FabricaReportesSede fabrica = FabricaReportesPorSede.enterprise().para(sede);

        assertEquals(formato, fabrica.crearResumen().formato());
        assertEquals(formato, fabrica.crearDetalle().formato());
    }

    @Test
    void eciGeneraHtmlConLosIndicadoresYEscapaElTexto() {
        FabricaReportesSede eci = new FabricaReportesEci();

        String resumen = eci.crearResumen().generar(METRICAS_ECI);
        String detalle = eci.crearDetalle().generar(ECI, MISIONES_ECI);

        assertTrue(resumen.contains("<h1>Escuela Colombiana de Ingeniería</h1>"));
        assertTrue(resumen.contains("Tasa de éxito: 50.0 %") && resumen.contains("Tiempo promedio: 12.0 min"));
        assertTrue(detalle.contains("<tr><td>M-1</td><td>D-01</td><td>URGENTE</td><td>ENTREGADA</td><td>12.0</td></tr>"));
        assertEquals("a&lt;b&gt; &amp; &quot;c&quot;", FabricaReportesEci.escapar("a<b> & \"c\""));
    }

    @Test
    void uniandesGeneraJsonConNullParaLoQueNoExiste() {
        FabricaReportesSede uniandes = new FabricaReportesUniandes();
        MetricasEficienciaSede sinEntregas = new MetricasEficienciaSede(
                ECI, 1, 0, 0.0, OptionalDouble.empty(), Optional.of("D-01"), 0.0);

        assertEquals("{\"sede\":\"ECI\",\"totalMisiones\":1,\"tasaExito\":0.0000,\"tiempoPromedioMin\":null,"
                + "\"droneMasUtilizado\":\"D-01\",\"porcentajeUrgentes\":0.0000}",
                uniandes.crearResumen().generar(sinEntregas));
        assertEquals("{\"sede\":\"ECI\",\"misiones\":[{\"id\":\"M-1\",\"drone\":\"D-01\",\"prioridad\":\"URGENTE\","
                + "\"estado\":\"ENTREGADA\",\"minutos\":12.0},{\"id\":\"M-2\",\"drone\":\"D-01\","
                + "\"prioridad\":\"NORMAL\",\"estado\":\"PENDIENTE\",\"minutos\":null}]}",
                uniandes.crearDetalle().generar(ECI, MISIONES_ECI));
        assertEquals("\"a\\\"b\\\\\"", FabricaReportesUniandes.texto("a\"b\\"));
    }

    @Test
    void eafitGeneraCsvConEncabezadoYUnaFilaPorMision() {
        FabricaReportesSede eafit = new FabricaReportesEafit();

        assertEquals("sede;tasa_exito_pct;tiempo_promedio_min;drone_mas_utilizado;urgentes_pct\n"
                + "ECI;50.0;12.0;D-01;50.0\n", eafit.crearResumen().generar(METRICAS_ECI));
        assertEquals("mision;drone;prioridad;estado;minutos\nM-1;D-01;URGENTE;ENTREGADA;12.0\n"
                + "M-2;D-01;NORMAL;PENDIENTE;\n", eafit.crearDetalle().generar(ECI, MISIONES_ECI));
        assertEquals("\"a;\"\"b\"\"\"", FabricaReportesEafit.celda("a;\"b\""));
    }

    @Test
    void unalGeneraUnPdfConXrefCorrecto() {
        String pdf = new FabricaReportesUnal().crearResumen().generar(METRICAS_ECI);

        assertTrue(pdf.startsWith("%PDF-1.4\n") && pdf.endsWith("%%EOF\n"));
        assertTrue(pdf.contains("(Resumen de eficiencia - Escuela Colombiana de Ingenieria) '"));
        int startxref = Integer.parseInt(pdf.substring(pdf.lastIndexOf("startxref\n") + 10, pdf.lastIndexOf("\n%%EOF")));
        assertTrue(pdf.startsWith("xref", startxref));
        assertEquals(pdf.length(), pdf.getBytes(StandardCharsets.ISO_8859_1).length);
        assertTrue(new FabricaReportesUnal().crearDetalle().generar(ECI, MISIONES_ECI).contains("(M-1  D-01  URGENTE"));
    }

    @Test
    void elDetalleRechazaMisionesDeOtraSede() {
        Mision deUnal = new Mision("M-9", UNAL, "D-02", PrioridadMision.NORMAL, EstadoMision.PENDIENTE, INICIO,
                Optional.empty());
        ReporteDetalle detalle = new FabricaReportesEci().crearDetalle();

        assertThrows(IllegalArgumentException.class, () -> detalle.generar(ECI, List.of(deUnal)));
    }

    @Test
    void agregarUnaSedeSoloRequiereRegistrarSuFabrica() {
        FabricaReportesPorSede red = new FabricaReportesPorSede(Map.of("UPB", new FabricaReportesEafit()));

        assertEquals(FormatoReporte.CSV, red.para("UPB").crearResumen().formato());
        assertThrows(IllegalArgumentException.class, () -> FabricaReportesPorSede.enterprise().para("SEDE-X"));
    }
}
