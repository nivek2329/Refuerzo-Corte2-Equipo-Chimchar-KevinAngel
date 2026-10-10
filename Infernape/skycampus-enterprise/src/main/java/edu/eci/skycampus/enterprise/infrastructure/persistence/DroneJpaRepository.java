package edu.eci.skycampus.enterprise.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneJpaRepository extends JpaRepository<DroneEntidad, String> {
    List<DroneEntidad> findBySedeCodigoAndDisponibleTrueOrderByIdAsc(String codigoSede);
}
