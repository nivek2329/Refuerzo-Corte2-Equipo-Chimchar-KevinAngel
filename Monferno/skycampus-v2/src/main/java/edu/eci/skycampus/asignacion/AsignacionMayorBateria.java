package edu.eci.skycampus.asignacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.Paquete;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AsignacionMayorBateria implements EstrategiaAsignacion {
    @Override
    public Optional<Drone> seleccionar(List<Drone> flota, Paquete paquete) {
        return CriterioAptitud.aptos(flota, paquete)
                .max(Comparator.comparingInt(Drone::bateria));
    }
}
