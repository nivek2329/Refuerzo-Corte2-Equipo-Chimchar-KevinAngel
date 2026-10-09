package edu.eci.skycampus.asignacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoMision;
import edu.eci.skycampus.modelo.Mision;
import edu.eci.skycampus.modelo.SolicitudReparto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Contexto del Strategy: crea la misión con el drone que elija la estrategia recibida, sin conocer cuál es. */
public class GestorMisiones {
    private static final String PREFIJO_MISION = "M-";

    private final EstrategiaAsignacion estrategia;

    public GestorMisiones(EstrategiaAsignacion estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "estrategia no puede ser null");
    }

    public Optional<Mision> crearMision(List<Drone> flota, SolicitudReparto solicitud, LocalDateTime ahora) {
        Objects.requireNonNull(flota, "flota no puede ser null");
        Objects.requireNonNull(solicitud, "solicitud no puede ser null");
        Objects.requireNonNull(ahora, "ahora no puede ser null");
        return estrategia.seleccionar(flota, solicitud.paquete())
                .map(drone -> new Mision(PREFIJO_MISION + solicitud.id(), drone, solicitud,
                        EstadoMision.PENDIENTE, ahora));
    }
}
