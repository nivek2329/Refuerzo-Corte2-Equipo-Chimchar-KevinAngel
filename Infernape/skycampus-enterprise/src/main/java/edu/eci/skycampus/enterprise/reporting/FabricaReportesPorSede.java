package edu.eci.skycampus.enterprise.reporting;

import java.util.Map;
import java.util.Objects;

/** Elige la fábrica de la sede. Agregar una sede = una fábrica nueva y una entrada en este mapa. */
public final class FabricaReportesPorSede {
    private final Map<String, FabricaReportesSede> fabricas;

    public FabricaReportesPorSede(Map<String, FabricaReportesSede> fabricas) {
        this.fabricas = Map.copyOf(Objects.requireNonNull(fabricas, "fabricas no puede ser null"));
    }

    public FabricaReportesSede para(String codigoSede) {
        FabricaReportesSede fabrica = fabricas.get(Objects.requireNonNull(codigoSede, "codigoSede no puede ser null"));
        if (fabrica == null) throw new IllegalArgumentException("sede sin formatos de reporte configurados");
        return fabrica;
    }

    public static FabricaReportesPorSede enterprise() {
        return new FabricaReportesPorSede(Map.of("ECI", new FabricaReportesEci(), "UNAL", new FabricaReportesUnal(),
                "UNIANDES", new FabricaReportesUniandes(), "EAFIT", new FabricaReportesEafit()));
    }
}
