package edu.eci.skycampus.enterprise.routing;

import java.util.List;
import java.util.Objects;

public record ResultadoRutaPlanificada(Ruta ruta, List<DroneEtapa> drones, double tiempoEstimadoHoras) {
    public ResultadoRutaPlanificada {
        Objects.requireNonNull(ruta, "ruta no puede ser null");
        drones = List.copyOf(drones);
        if (!Double.isFinite(tiempoEstimadoHoras) || tiempoEstimadoHoras <= 0
                || drones.size() != ruta.cantidadEtapas()) {
            throw new IllegalArgumentException("resultado de ruta no es consistente");
        }
    }
}
