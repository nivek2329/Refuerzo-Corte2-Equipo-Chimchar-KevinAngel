package edu.eci.skycampus.asignacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.Paquete;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Reparte el desgaste: elige el apto con menos minutos de vuelo acumulados. */
public class AsignacionMenorUso implements EstrategiaAsignacion {
    @Override
    public Optional<Drone> seleccionar(List<Drone> flota, Paquete paquete) {
        return CriterioAptitud.aptos(flota, paquete)
                .min(Comparator.comparingInt(Drone::minutosVueloAcumulados));
    }
}
