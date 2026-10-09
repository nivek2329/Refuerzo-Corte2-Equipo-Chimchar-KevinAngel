package edu.eci.skycampus.notificacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;

/** Observer: recibe cada cambio de estado de un drone. */
@FunctionalInterface
public interface ObservadorDrone {
    void onEstadoCambiado(Drone drone, EstadoDrone nuevo);
}
