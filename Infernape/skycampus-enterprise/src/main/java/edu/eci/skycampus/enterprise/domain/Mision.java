package edu.eci.skycampus.enterprise.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public record Mision(
        String id,
        Sede sede,
        String droneId,
        PrioridadMision prioridad,
        EstadoMision estado,
        Instant creadaEn,
        Optional<Instant> entregadaEn) {

    public Mision {
        Objects.requireNonNull(id, "id de mision no puede ser null");
        Objects.requireNonNull(sede, "sede no puede ser null");
        Objects.requireNonNull(droneId, "droneId no puede ser null");
        Objects.requireNonNull(prioridad, "prioridad no puede ser null");
        Objects.requireNonNull(estado, "estado no puede ser null");
        Objects.requireNonNull(creadaEn, "creadaEn no puede ser null");
        Objects.requireNonNull(entregadaEn, "entregadaEn no puede ser null");
        validarIntervaloEntrega(creadaEn, entregadaEn);
        validarEstadoEntrega(estado, entregadaEn);
    }

    private static void validarIntervaloEntrega(Instant creadaEn, Optional<Instant> entregadaEn) {
        entregadaEn.ifPresent(instante -> {
            if (instante.isBefore(creadaEn)) {
                throw new IllegalArgumentException("la entrega no puede ocurrir antes de crear la mision");
            }
        });
    }

    private static void validarEstadoEntrega(EstadoMision estado, Optional<Instant> entregadaEn) {
        if ((estado == EstadoMision.ENTREGADA) != entregadaEn.isPresent()) {
            throw new IllegalArgumentException("solo una mision entregada debe tener fecha de entrega");
        }
    }

    public Optional<Duration> tiempoEntrega() {
        return entregadaEn.map(instante -> Duration.between(creadaEn, instante));
    }
}
