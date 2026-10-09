package edu.eci.skycampus.asignacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.Paquete;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/** Regla común a todas las estrategias: disponible, batería mínima 30 % y tipo compatible con el peso. */
public final class CriterioAptitud {
    public static final int BATERIA_MINIMA = 30;

    private CriterioAptitud() {
    }

    public static Stream<Drone> aptos(List<Drone> flota, Paquete paquete) {
        Objects.requireNonNull(flota, "flota no puede ser null");
        Objects.requireNonNull(paquete, "paquete no puede ser null");
        return flota.stream().filter(drone -> isApto(drone, paquete));
    }

    public static boolean isApto(Drone drone, Paquete paquete) {
        return drone.isDisponible()
                && drone.bateria() >= BATERIA_MINIMA
                && drone.tipo().esCompatibleCon(paquete.pesoGramos());
    }
}
