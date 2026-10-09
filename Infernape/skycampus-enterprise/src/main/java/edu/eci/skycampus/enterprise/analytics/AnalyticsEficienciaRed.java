package edu.eci.skycampus.enterprise.analytics;

import edu.eci.skycampus.enterprise.domain.Mision;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnalyticsEficienciaRed {
    public ResultadoAnalyticsRed calcular(List<Sede> sedes, List<Mision> misiones) {
        Objects.requireNonNull(sedes, "sedes no puede ser null");
        Objects.requireNonNull(misiones, "misiones no puede ser null");
        return new ResultadoAnalyticsRed(
                sedes.stream().collect(Collectors.toMap(sede -> sede, sede -> Optional.empty())),
                List.of());
    }
}
