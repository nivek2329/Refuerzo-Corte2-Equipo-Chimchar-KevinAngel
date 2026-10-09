package edu.eci.skycampus.enterprise.reporting;

public final class FabricaReportesEci extends FabricaReportesSede {
    @Override
    protected FormatoReporte formatoCompatible() { return FormatoReporte.HTML; }
}
