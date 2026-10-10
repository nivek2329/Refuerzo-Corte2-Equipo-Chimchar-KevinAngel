package edu.eci.skycampus.enterprise.infrastructure;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.RepositorioFlota;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Adaptador de demostración; en producción se sustituye por persistencia real. */
public final class RepositorioFlotaEnMemoria implements RepositorioFlota {
    private final ConcurrentMap<String, Drone> drones = new ConcurrentHashMap<>();

    public RepositorioFlotaEnMemoria(List<Drone> flotaInicial) {
        Objects.requireNonNull(flotaInicial, "flota inicial no puede ser null").forEach(drone ->
                drones.put(Objects.requireNonNull(drone, "drone no puede ser null").id(), drone));
    }

    @Override
    public List<Drone> findDisponibles(Sede sede) {
        Objects.requireNonNull(sede, "sede no puede ser null");
        return drones.values().stream()
                .filter(drone -> drone.sede().equals(sede) && drone.disponible())
                .sorted(Comparator.comparing(Drone::id))
                .toList();
    }

    @Override
    public void registrarAsignacion(String misionId, String droneId) {
        Objects.requireNonNull(misionId, "id de mision no puede ser null");
        Objects.requireNonNull(droneId, "id de drone no puede ser null");
        drones.compute(droneId, (id, drone) -> {
            if (drone == null || !drone.disponible()) {
                throw new IllegalStateException("el drone no existe o ya no está disponible");
            }
            return drone.noDisponible();
        });
    }
}
