package edu.eci.skycampus.enterprise.routing;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ContextoEjecucionRuta(
        Instant inicio,
        CondicionesViento viento,
        FabricaDronesPorEtapa fabricaDrones,
        List<ObservadorEtapaRuta> observadores) {

    public ContextoEjecucionRuta {
        Objects.requireNonNull(inicio, "inicio no puede ser null");
        Objects.requireNonNull(viento, "viento no puede ser null");
        Objects.requireNonNull(fabricaDrones, "fabricaDrones no puede ser null");
        observadores = List.copyOf(observadores);
    }

    public ContextoEjecucionRuta desde(Instant nuevoInicio) {
        return new ContextoEjecucionRuta(nuevoInicio, viento, fabricaDrones, observadores);
    }
}
