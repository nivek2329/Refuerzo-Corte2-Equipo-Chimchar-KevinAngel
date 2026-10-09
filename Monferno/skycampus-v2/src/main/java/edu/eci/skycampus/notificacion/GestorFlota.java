package edu.eci.skycampus.notificacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Sujeto del Observer: cambia el estado de un drone y avisa a todos los suscritos a través de la interfaz.
 * Solo notifica cambios reales; las transiciones inválidas las rechaza el dominio ({@link Drone#transicionarA}).
 * No guarda la flota: quien llama debe usar el drone devuelto, porque {@link Drone} es inmutable.
 */
public class GestorFlota {
    private final List<ObservadorDrone> observadores = new ArrayList<>();

    public void suscribir(ObservadorDrone observador) {
        Objects.requireNonNull(observador, "observador no puede ser null");
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void desuscribir(ObservadorDrone observador) {
        Objects.requireNonNull(observador, "observador no puede ser null");
        observadores.remove(observador);
    }

    public Drone cambiarEstado(Drone drone, EstadoDrone nuevo) {
        Objects.requireNonNull(drone, "drone no puede ser null");
        Objects.requireNonNull(nuevo, "nuevo no puede ser null");
        if (drone.estado() == nuevo) {
            return drone;
        }
        Drone actualizado = drone.transicionarA(nuevo);
        List.copyOf(observadores).forEach(observador -> observador.onEstadoCambiado(actualizado, nuevo));
        return actualizado;
    }
}
