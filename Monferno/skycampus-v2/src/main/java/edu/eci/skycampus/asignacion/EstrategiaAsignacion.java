package edu.eci.skycampus.asignacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.Paquete;

import java.util.List;
import java.util.Optional;

/** Strategy: algoritmo intercambiable para elegir el drone de una misión. */
@FunctionalInterface
public interface EstrategiaAsignacion {
    Optional<Drone> seleccionar(List<Drone> flota, Paquete paquete);
}
