package edu.eci.skycampus.modelo;

import java.util.Objects;

/**
 * Drone de la flota v2. La disponibilidad se deriva del estado (no se guarda un boolean aparte),
 * para que nunca puedan contradecirse.
 */
public record Drone(String id, TipoDrone tipo, int bateria, EstadoDrone estado, int minutosVueloAcumulados) {
    private static final int BATERIA_MINIMA_VALIDA = 0;
    private static final int BATERIA_MAXIMA_VALIDA = 100;

    public Drone {
        Objects.requireNonNull(id, "id no puede ser null");
        Objects.requireNonNull(tipo, "tipo no puede ser null");
        Objects.requireNonNull(estado, "estado no puede ser null");
        if (bateria < BATERIA_MINIMA_VALIDA || bateria > BATERIA_MAXIMA_VALIDA) {
            throw new IllegalArgumentException(
                    "batería debe estar entre " + BATERIA_MINIMA_VALIDA + " y " + BATERIA_MAXIMA_VALIDA);
        }
        if (minutosVueloAcumulados < 0) {
            throw new IllegalArgumentException("minutosVueloAcumulados no puede ser negativo");
        }
    }

    public boolean isDisponible() {
        return estado == EstadoDrone.DISPONIBLE;
    }

    /** Devuelve una copia en el nuevo estado; el propio dominio rechaza las transiciones no permitidas. */
    public Drone transicionarA(EstadoDrone nuevoEstado) {
        Objects.requireNonNull(nuevoEstado, "nuevoEstado no puede ser null");
        if (!estado.puedePasarA(nuevoEstado)) {
            throw new IllegalStateException("transición no permitida para " + id + ": " + estado + " → " + nuevoEstado);
        }
        return new Drone(id, tipo, bateria, nuevoEstado, minutosVueloAcumulados);
    }
}
