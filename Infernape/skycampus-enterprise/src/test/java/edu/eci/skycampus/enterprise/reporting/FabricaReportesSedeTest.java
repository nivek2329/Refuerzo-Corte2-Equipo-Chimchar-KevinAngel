package edu.eci.skycampus.enterprise.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FabricaReportesSedeTest {
    @Test
    void fabricaEnterpriseCreaFamiliasCoherentesPorSede() {
        assertFamilia("ECI", FormatoReporte.HTML);
        assertFamilia("UNAL", FormatoReporte.PDF);
        assertFamilia("UNIANDES", FormatoReporte.JSON);
        assertFamilia("EAFIT", FormatoReporte.CSV);
    }

    @Test
    void fabricaRechazaSedeNoConfigurada() {
        assertThrows(IllegalArgumentException.class,
                () -> FabricaReportesPorSede.enterprise().crearFamilia("SEDE-X"));
    }

    private static void assertFamilia(String sede, FormatoReporte formato) {
        FamiliaReportesSede familia = FabricaReportesPorSede.enterprise().crearFamilia(sede);
        assertEquals(formato, familia.resumen().formato());
        assertEquals(formato, familia.detalle().formato());
        assertEquals(TipoReporte.RESUMEN, familia.resumen().tipo());
        assertEquals(TipoReporte.DETALLE, familia.detalle().tipo());
    }
}
