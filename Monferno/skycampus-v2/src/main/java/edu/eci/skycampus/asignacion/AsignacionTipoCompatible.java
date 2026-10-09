package edu.eci.skycampus.asignacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.Paquete;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Usa el tipo de menor capacidad que puede llevar la carga (no gasta un CARGO en un sobre); desempata por batería. */
public class AsignacionTipoCompatible implements EstrategiaAsignacion {
    private static final Comparator<Drone> MENOR_CAPACIDAD_Y_MAYOR_BATERIA =
            Comparator.comparingInt((Drone drone) -> drone.tipo().capacidadGramos())
                    .thenComparing(Comparator.comparingInt(Drone::bateria).reversed());

    @Override
    public Optional<Drone> seleccionar(List<Drone> flota, Paquete paquete) {
        return CriterioAptitud.aptos(flota, paquete)
                .min(MENOR_CAPACIDAD_Y_MAYOR_BATERIA);
    }
}
