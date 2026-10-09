package edu.eci.skycampus.enterprise.reporting;

public final class FabricaReportesUnal extends FabricaReportesSede {
    @Override
    protected FormatoReporte formatoCompatible() { return FormatoReporte.PDF; }
}
