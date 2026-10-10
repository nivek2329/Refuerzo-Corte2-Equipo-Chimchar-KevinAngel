package edu.eci.skycampus.enterprise.domain;

import java.util.List;
import java.util.Optional;

public interface EstrategiaAsignacion {
    Optional<Drone> elegir(List<Drone> candidatos, SolicitudAsignacion solicitud);
}
