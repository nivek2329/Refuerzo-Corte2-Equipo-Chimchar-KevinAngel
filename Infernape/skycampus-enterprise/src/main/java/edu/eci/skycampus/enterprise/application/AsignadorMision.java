package edu.eci.skycampus.enterprise.application;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.EstadoMision;
import edu.eci.skycampus.enterprise.domain.EstrategiaAsignacion;
import edu.eci.skycampus.enterprise.domain.ObservadorDrone;
import edu.eci.skycampus.enterprise.domain.RepositorioFlota;
import edu.eci.skycampus.enterprise.domain.ServicioClima;
import edu.eci.skycampus.enterprise.domain.SolicitudAsignacion;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class AsignadorMision {
    private final RepositorioFlota repositorio;
    private final ServicioClima clima;
    private final EstrategiaAsignacion estrategia;
    private final ObservadorDrone notificador;

    public AsignadorMision(RepositorioFlota repositorio, ServicioClima clima,
                           EstrategiaAsignacion estrategia, ObservadorDrone notificador) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio no puede ser null");
        this.clima = Objects.requireNonNull(clima, "servicio de clima no puede ser null");
        this.estrategia = Objects.requireNonNull(estrategia, "estrategia no puede ser null");
        this.notificador = Objects.requireNonNull(notificador, "notificador no puede ser null");
    }

    public Optional<Drone> asignar(SolicitudAsignacion solicitud) {
        Objects.requireNonNull(solicitud, "solicitud no puede ser null");
        if (!clima.condicionesAptas(solicitud.origen(), solicitud.destino())) {
            return Optional.empty();
        }

        List<Drone> candidatos = repositorio.findDisponibles(solicitud.sede()).stream()
                .filter(Drone::disponible)
                .filter(drone -> drone.capacidadCargaKg() >= solicitud.pesoPaqueteKg())
                .toList();
        if (candidatos.isEmpty()) {
            return Optional.empty();
        }
        Optional<Drone> asignado = estrategia.elegir(candidatos, solicitud);
        if (asignado.isEmpty()) {
            return Optional.empty();
        }

        Drone drone = asignado.orElseThrow();
        if (!candidatos.contains(drone)) {
            throw new IllegalStateException("la estrategia seleccionó un drone que no es candidato");
        }
        repositorio.registrarAsignacion(solicitud.misionId(), drone.id());
        notificador.onEstadoCambiado(solicitud.misionId(), drone, EstadoMision.EN_VUELO);
        return asignado;
    }
}
