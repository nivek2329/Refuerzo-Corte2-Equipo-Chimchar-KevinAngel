package edu.eci.skycampus.notificacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;

import java.util.ArrayList;
import java.util.List;

/** Historial, en orden de llegada, de los avisos de cambio de estado que ve el operador. */
public class PanelOperador implements ObservadorDrone {
    private final List<String> avisos = new ArrayList<>();

    @Override
    public void onEstadoCambiado(Drone drone, EstadoDrone nuevo) {
        avisos.add(drone.id() + " · " + nuevo);
    }

    public List<String> avisos() {
        return List.copyOf(avisos);
    }
}
