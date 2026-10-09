package edu.eci.skycampus.enterprise.reporting;

public final class FabricaReportesEafit extends FabricaReportesSede {
    @Override
    protected FormatoReporte formatoCompatible() { return FormatoReporte.CSV; }
}
