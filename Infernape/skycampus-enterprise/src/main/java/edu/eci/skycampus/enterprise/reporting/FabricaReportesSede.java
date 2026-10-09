package edu.eci.skycampus.enterprise.reporting;

public abstract class FabricaReportesSede {
    public final ReporteSede crearResumen(String codigoSede) {
        return crear(codigoSede, TipoReporte.RESUMEN);
    }

    public final ReporteSede crearDetalle(String codigoSede) {
        return crear(codigoSede, TipoReporte.DETALLE);
    }

    protected abstract FormatoReporte formatoCompatible();

    private ReporteSede crear(String codigoSede, TipoReporte tipo) {
        return new ReporteSede(codigoSede, formatoCompatible(), tipo);
    }
}
