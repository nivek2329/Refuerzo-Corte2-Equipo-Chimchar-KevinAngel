package edu.eci.skycampus.enterprise.application;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.EstadoMision;
import edu.eci.skycampus.enterprise.domain.EstrategiaAsignacion;
import edu.eci.skycampus.enterprise.domain.MotivoRechazo;
import edu.eci.skycampus.enterprise.domain.ObservadorDrone;
import edu.eci.skycampus.enterprise.domain.RepositorioFlota;
import edu.eci.skycampus.enterprise.domain.RepositorioSedes;
import edu.eci.skycampus.enterprise.domain.ResultadoAsignacion;
import edu.eci.skycampus.enterprise.domain.ServicioAerocivil;
import edu.eci.skycampus.enterprise.domain.ServicioClima;
import edu.eci.skycampus.enterprise.domain.SolicitudAsignacion;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Caso de uso de asignación Enterprise. Orden de las verificaciones (cada una corta el flujo y explica el motivo):
 * sede activa → clima → Aerocivil → drones disponibles → capacidad → estrategia.
 * Solo depende de puertos del dominio; nunca de infraestructura.
 */
public final class AsignadorMision {
    private final RepositorioFlota repositorio;
    private final ServicioClima clima;
    private final ServicioAerocivil aerocivil;
    private final RepositorioSedes sedes;
    private final EstrategiaAsignacion estrategia;
    private final ObservadorDrone notificador;

    public AsignadorMision(RepositorioFlota repositorio, ServicioClima clima, ServicioAerocivil aerocivil,
                           RepositorioSedes sedes, EstrategiaAsignacion estrategia, ObservadorDrone notificador) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio no puede ser null");
        this.clima = Objects.requireNonNull(clima, "servicio de clima no puede ser null");
        this.aerocivil = Objects.requireNonNull(aerocivil, "servicio de Aerocivil no puede ser null");
        this.sedes = Objects.requireNonNull(sedes, "repositorio de sedes no puede ser null");
        this.estrategia = Objects.requireNonNull(estrategia, "estrategia no puede ser null");
        this.notificador = Objects.requireNonNull(notificador, "notificador no puede ser null");
    }

    /** Atajo del reto 04: el drone asignado o vacío. */
    public Optional<Drone> asignar(SolicitudAsignacion solicitud) {
        return evaluar(solicitud).drone();
    }

    public ResultadoAsignacion evaluar(SolicitudAsignacion solicitud) {
        Objects.requireNonNull(solicitud, "solicitud no puede ser null");
        Optional<MotivoRechazo> bloqueo = bloqueoPrevio(solicitud);
        if (bloqueo.isPresent()) {
            return ResultadoAsignacion.rechazado(bloqueo.get());
        }
        List<Drone> disponibles = repositorio.findDisponibles(solicitud.sede()).stream()
                .filter(Drone::disponible)
                .toList();
        if (disponibles.isEmpty()) {
            return ResultadoAsignacion.rechazado(MotivoRechazo.SIN_DRONES_DISPONIBLES);
        }
        List<Drone> candidatos = disponibles.stream()
                .filter(drone -> drone.capacidadCargaKg() >= solicitud.pesoPaqueteKg())
                .toList();
        if (candidatos.isEmpty()) {
            return ResultadoAsignacion.rechazado(MotivoRechazo.PAQUETE_DEMASIADO_PESADO);
        }
        return estrategia.elegir(candidatos, solicitud)
                .map(drone -> confirmar(solicitud, candidatos, drone))
                .orElseGet(() -> ResultadoAsignacion.rechazado(MotivoRechazo.SIN_DRONES_DISPONIBLES));
    }

    private Optional<MotivoRechazo> bloqueoPrevio(SolicitudAsignacion solicitud) {
        if (!sedes.estaActiva(solicitud.sede())) {
            return Optional.of(MotivoRechazo.SEDE_INACTIVA);
        }
        if (!clima.condicionesAptas(solicitud.origen(), solicitud.destino())) {
            return Optional.of(MotivoRechazo.CLIMA_ADVERSO);
        }
        if (!aerocivil.autorizaRuta(solicitud.origen(), solicitud.destino())) {
            return Optional.of(MotivoRechazo.AEROCIVIL_RECHAZA);
        }
        return Optional.empty();
    }

    private ResultadoAsignacion confirmar(SolicitudAsignacion solicitud, List<Drone> candidatos, Drone drone) {
        if (!candidatos.contains(drone)) {
            throw new IllegalStateException("la estrategia seleccionó un drone que no es candidato");
        }
        repositorio.registrarAsignacion(solicitud.misionId(), drone.id());
        notificador.onEstadoCambiado(solicitud.misionId(), drone, EstadoMision.EN_VUELO);
        return ResultadoAsignacion.asignado(drone);
    }
}
