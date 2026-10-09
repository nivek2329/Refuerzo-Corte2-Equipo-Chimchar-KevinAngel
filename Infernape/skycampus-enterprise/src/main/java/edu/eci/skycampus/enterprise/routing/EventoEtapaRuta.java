package edu.eci.skycampus.enterprise.routing;

import java.time.Instant;
import java.util.Objects;

public record EventoEtapaRuta(
        String etapaId,
        String droneId,
        String origen,
        String destino,
        Instant inicio,
        Instant llegadaEstimada) {

    public EventoEtapaRuta {
        Objects.requireNonNull(etapaId, "etapaId no puede ser null");
        Objects.requireNonNull(droneId, "droneId no puede ser null");
        Objects.requireNonNull(origen, "origen no puede ser null");
        Objects.requireNonNull(destino, "destino no puede ser null");
        Objects.requireNonNull(inicio, "inicio no puede ser null");
        Objects.requireNonNull(llegadaEstimada, "llegadaEstimada no puede ser null");
        if (llegadaEstimada.isBefore(inicio)) {
            throw new IllegalArgumentException("la llegada estimada no puede preceder al inicio");
        }
    }
}
