package edu.eci.skycampus;

import java.util.Objects;
import java.util.Set;

/** Valida las reglas básicas para asignar un drone y registrar un destino del MVP. */
public class ValidadorMision {
    private static final int BATERIA_MINIMA_PARA_MISION = 30;
    private static final Set<String> DESTINOS_VALIDOS = Set.of(
            "Bloque A", "Bloque B", "Bloque C", "Bloque D", "Biblioteca");

    public boolean tieneBateriaSuficiente(Drone drone) {
        Objects.requireNonNull(drone, "drone no puede ser null");
        return drone.bateria() >= BATERIA_MINIMA_PARA_MISION;
    }

    public void validarDestino(String destino) {
        Objects.requireNonNull(destino, "destino no puede ser null");
        if (!DESTINOS_VALIDOS.contains(destino)) {
            throw new DestinoInvalidoException("Destino no válido: " + destino);
        }
    }

    public boolean droneEstaDisponible(Drone drone) {
        Objects.requireNonNull(drone, "drone no puede ser null");
        return drone.disponible();
    }
}
