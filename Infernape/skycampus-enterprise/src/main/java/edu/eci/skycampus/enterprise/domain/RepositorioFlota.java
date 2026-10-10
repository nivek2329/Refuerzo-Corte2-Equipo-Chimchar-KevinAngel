package edu.eci.skycampus.enterprise.domain;

import java.util.List;

/** Puerto de salida: la aplicación pide drones y registra asignaciones sin saber dónde se guardan. */
public interface RepositorioFlota {
    List<Drone> findDisponibles(Sede sede);

    void registrarAsignacion(String misionId, String droneId);
}
