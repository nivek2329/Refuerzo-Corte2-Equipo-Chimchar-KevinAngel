package edu.eci.skycampus.enterprise.infrastructure.persistence;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.RepositorioFlota;
import edu.eci.skycampus.enterprise.domain.Sede;
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Adaptador JPA del puerto RepositorioFlota (reemplaza al de memoria sin tocar aplicación ni dominio). */
@Repository
public class RepositorioFlotaJpa implements RepositorioFlota {
    private final DroneJpaRepository drones;
    private final AsignacionJpaRepository asignaciones;
    private final Clock reloj;

    public RepositorioFlotaJpa(DroneJpaRepository drones, AsignacionJpaRepository asignaciones, Clock reloj) {
        this.drones = drones;
        this.asignaciones = asignaciones;
        this.reloj = reloj;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Drone> findDisponibles(Sede sede) {
        Objects.requireNonNull(sede, "sede no puede ser null");
        return drones.findBySedeCodigoAndDisponibleTrueOrderByIdAsc(sede.codigo()).stream()
                .map(DroneEntidad::aDominio)
                .toList();
    }

    @Override
    @Transactional
    public void registrarAsignacion(String misionId, String droneId) {
        DroneEntidad drone = drones.findById(droneId)
                .filter(DroneEntidad::isDisponible)
                .orElseThrow(() -> new IllegalStateException("el drone no existe o ya no está disponible"));
        drone.marcarNoDisponible();
        asignaciones.save(new AsignacionEntidad(misionId, droneId, reloj.instant()));
    }
}
