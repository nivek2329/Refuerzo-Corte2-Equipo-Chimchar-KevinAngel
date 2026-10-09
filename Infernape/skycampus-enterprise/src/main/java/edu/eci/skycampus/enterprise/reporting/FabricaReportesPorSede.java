package edu.eci.skycampus.enterprise.reporting;

import java.util.Map;
import java.util.Objects;

public final class FabricaReportesPorSede {
    private final Map<String, FabricaReportesSede> fabricas;

    public FabricaReportesPorSede(Map<String, FabricaReportesSede> fabricas) {
        this.fabricas = Map.copyOf(Objects.requireNonNull(fabricas, "fabricas no puede ser null"));
    }

    public FamiliaReportesSede crearFamilia(String codigoSede) {
        String sedeValidada = Objects.requireNonNull(codigoSede, "codigoSede no puede ser null");
        FabricaReportesSede seleccionada = fabricas.get(sedeValidada);
        if (seleccionada == null) throw new IllegalArgumentException("sede sin formatos de reporte configurados");
        return new FamiliaReportesSede(seleccionada.crearResumen(codigoSede), seleccionada.crearDetalle(codigoSede));
    }

    public static FabricaReportesPorSede enterprise() {
        return new FabricaReportesPorSede(Map.of("ECI", new FabricaReportesEci(), "UNAL", new FabricaReportesUnal(),
                "UNIANDES", new FabricaReportesUniandes(), "EAFIT", new FabricaReportesEafit()));
    }
}
