package edu.eci.skycampus.enterprise.domain;

import java.util.Objects;

/** Drone de la red Enterprise. Inmutable: un cambio de disponibilidad produce otra instancia. */
public record Drone(String id, Sede sede, int bateria, double capacidadCargaKg,
                    boolean disponible, boolean express) {
    public Drone {
        Objects.requireNonNull(id, "id del drone no puede ser null");
        Objects.requireNonNull(sede, "sede del drone no puede ser null");
        if (id.isBlank() || bateria < 0 || bateria > 100
                || !Double.isFinite(capacidadCargaKg) || capacidadCargaKg <= 0) {
            throw new IllegalArgumentException("los datos del drone deben ser válidos");
        }
    }

    public Drone noDisponible() {
        return new Drone(id, sede, bateria, capacidadCargaKg, false, express);
    }
}
