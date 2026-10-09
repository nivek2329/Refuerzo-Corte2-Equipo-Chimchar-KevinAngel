package edu.eci.skycampus.modelo;

import java.time.LocalDateTime;
import java.util.Objects;

/** Una misión siempre tiene drone: nace PENDIENTE cuando se le asigna uno (igual que SC-01 en Chimchar). */
public record Mision(String id, Drone drone, SolicitudReparto solicitud, EstadoMision estado, LocalDateTime creadaEn) {
    public Mision {
        Objects.requireNonNull(id, "id no puede ser null");
        Objects.requireNonNull(drone, "drone no puede ser null");
        Objects.requireNonNull(solicitud, "solicitud no puede ser null");
        Objects.requireNonNull(estado, "estado no puede ser null");
        Objects.requireNonNull(creadaEn, "creadaEn no puede ser null");
    }

    public Prioridad prioridad() {
        return solicitud.paquete().prioridad();
    }

    public int pesoPaqueteGramos() {
        return solicitud.paquete().pesoGramos();
    }
}
