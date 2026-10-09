package edu.eci.skycampus.enterprise.reporting;

import java.util.Objects;

public record ReporteSede(String codigoSede, FormatoReporte formato, TipoReporte tipo) {
    public ReporteSede {
        Objects.requireNonNull(codigoSede, "codigoSede no puede ser null");
        Objects.requireNonNull(formato, "formato no puede ser null");
        Objects.requireNonNull(tipo, "tipo no puede ser null");
        if (codigoSede.isBlank()) throw new IllegalArgumentException("codigoSede es obligatorio");
    }
}
