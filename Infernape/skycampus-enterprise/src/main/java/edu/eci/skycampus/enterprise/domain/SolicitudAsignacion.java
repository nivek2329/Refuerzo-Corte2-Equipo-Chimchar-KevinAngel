package edu.eci.skycampus.enterprise.domain;

import java.util.Objects;

public record SolicitudAsignacion(String misionId, Sede sede, String origen, String destino,
                                  double pesoPaqueteKg, PrioridadMision prioridad) {
    public SolicitudAsignacion {
        Objects.requireNonNull(misionId, "id de mision no puede ser null");
        Objects.requireNonNull(sede, "sede no puede ser null");
        Objects.requireNonNull(origen, "origen no puede ser null");
        Objects.requireNonNull(destino, "destino no puede ser null");
        Objects.requireNonNull(prioridad, "prioridad no puede ser null");
        if (misionId.isBlank() || origen.isBlank() || destino.isBlank()
                || !Double.isFinite(pesoPaqueteKg) || pesoPaqueteKg <= 0) {
            throw new IllegalArgumentException("la solicitud de asignación debe tener datos válidos");
        }
    }
}
