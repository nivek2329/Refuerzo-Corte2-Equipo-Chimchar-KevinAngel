package edu.eci.skycampus.enterprise.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class EstrategiaMayorBateria implements EstrategiaAsignacion {
    @Override
    public Optional<Drone> elegir(List<Drone> candidatos, SolicitudAsignacion solicitud) {
        return candidatos.stream()
                .filter(drone -> solicitud.prioridad() != PrioridadMision.URGENTE || drone.express())
                .max(Comparator.comparingInt(Drone::bateria).thenComparing(Drone::id, Comparator.reverseOrder()));
    }
}
