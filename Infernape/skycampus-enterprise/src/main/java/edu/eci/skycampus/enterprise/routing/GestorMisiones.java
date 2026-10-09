package edu.eci.skycampus.enterprise.routing;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class GestorMisiones {
    private final EstrategiaOptimizacionRuta estrategia;
    private final FabricaDronesPorEtapa fabricaDrones;
    private final List<ObservadorEtapaRuta> observadores;

    public GestorMisiones(EstrategiaOptimizacionRuta estrategia, FabricaDronesPorEtapa fabricaDrones,
                          List<ObservadorEtapaRuta> observadores) {
        this.estrategia = Objects.requireNonNull(estrategia, "estrategia no puede ser null");
        this.fabricaDrones = Objects.requireNonNull(fabricaDrones, "fabricaDrones no puede ser null");
        this.observadores = List.copyOf(observadores);
    }

    public ResultadoRutaPlanificada planificar(List<Ruta> alternativas, CondicionesViento viento, Instant inicio) {
        Objects.requireNonNull(inicio, "inicio no puede ser null");
        Ruta ruta = estrategia.elegir(alternativas, viento);
        ContextoEjecucionRuta contexto = new ContextoEjecucionRuta(inicio, viento, fabricaDrones, observadores);
        List<DroneEtapa> drones = ruta.ejecutar(contexto);
        return new ResultadoRutaPlanificada(ruta, drones, ruta.tiempoEstimadoHoras(viento));
    }
}
