package edu.eci.skycampus.enterprise.reporting;

public final class FabricaReportesUniandes extends FabricaReportesSede {
    @Override
    protected FormatoReporte formatoCompatible() { return FormatoReporte.JSON; }
}
