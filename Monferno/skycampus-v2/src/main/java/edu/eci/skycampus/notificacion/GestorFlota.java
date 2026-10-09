package edu.eci.skycampus.notificacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Sujeto del Observer: cambia el estado de un drone y avisa a todos los suscritos a través de la interfaz. */
public class GestorFlota {
    private final List<ObservadorDrone> observadores = new ArrayList<>();

    public void suscribir(ObservadorDrone observador) {
        Objects.requireNonNull(observador, "observador no puede ser null");
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void desuscribir(ObservadorDrone observador) {
        observadores.remove(observador);
    }

    public Drone cambiarEstado(Drone drone, EstadoDrone nuevo) {
        Objects.requireNonNull(drone, "drone no puede ser null");
        Objects.requireNonNull(nuevo, "nuevo no puede ser null");
        Drone actualizado = drone.conEstado(nuevo);
        List.copyOf(observadores).forEach(observador -> observador.onEstadoCambiado(actualizado, nuevo));
        return actualizado;
    }
}
