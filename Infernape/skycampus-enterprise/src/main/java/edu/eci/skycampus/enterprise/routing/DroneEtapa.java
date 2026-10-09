package edu.eci.skycampus.enterprise.routing;

import java.util.Objects;

public record DroneEtapa(String id, TipoDroneEtapa tipo, double capacidadCargaKg) {
    public DroneEtapa {
        Objects.requireNonNull(id, "id de drone no puede ser null");
        Objects.requireNonNull(tipo, "tipo de drone no puede ser null");
        if (id.isBlank() || !Double.isFinite(capacidadCargaKg) || capacidadCargaKg <= 0) {
            throw new IllegalArgumentException("identificador y capacidad del drone deben ser válidos");
        }
    }
}
