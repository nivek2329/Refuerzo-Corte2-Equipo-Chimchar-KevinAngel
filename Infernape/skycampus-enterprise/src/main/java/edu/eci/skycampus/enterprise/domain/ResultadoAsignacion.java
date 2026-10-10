package edu.eci.skycampus.enterprise.domain;

import java.util.Objects;
import java.util.Optional;

/** Resultado de intentar asignar: o un drone, o el motivo exacto del rechazo. Nunca ambos. */
public record ResultadoAsignacion(Optional<Drone> drone, Optional<MotivoRechazo> motivo) {
    public ResultadoAsignacion {
        Objects.requireNonNull(drone, "drone no puede ser null");
        Objects.requireNonNull(motivo, "motivo no puede ser null");
        if (drone.isPresent() == motivo.isPresent()) {
            throw new IllegalArgumentException("un resultado tiene drone o motivo, no ambos ni ninguno");
        }
    }

    public static ResultadoAsignacion asignado(Drone drone) {
        return new ResultadoAsignacion(Optional.of(drone), Optional.empty());
    }

    public static ResultadoAsignacion rechazado(MotivoRechazo motivo) {
        return new ResultadoAsignacion(Optional.empty(), Optional.of(motivo));
    }

    public boolean fueAsignado() {
        return drone.isPresent();
    }
}
