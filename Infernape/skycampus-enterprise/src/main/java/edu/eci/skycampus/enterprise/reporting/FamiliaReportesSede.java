package edu.eci.skycampus.enterprise.reporting;

import java.util.Objects;

public record FamiliaReportesSede(ReporteSede resumen, ReporteSede detalle) {
    public FamiliaReportesSede {
        Objects.requireNonNull(resumen, "resumen no puede ser null");
        Objects.requireNonNull(detalle, "detalle no puede ser null");
        if (!resumen.codigoSede().equals(detalle.codigoSede()) || resumen.formato() != detalle.formato()
                || resumen.tipo() != TipoReporte.RESUMEN || detalle.tipo() != TipoReporte.DETALLE) {
            throw new IllegalArgumentException("los reportes deben formar una familia coherente para la sede");
        }
    }
}
