package edu.eci.skycampus.notificacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;

import java.util.ArrayList;
import java.util.List;

/** Solo reacciona cuando un drone entra en FALLO: genera una orden para el técnico de mantenimiento. */
public class AlertaTecnico implements ObservadorDrone {
    private final List<String> ordenes = new ArrayList<>();

    @Override
    public void onEstadoCambiado(Drone drone, EstadoDrone nuevo) {
        if (nuevo == EstadoDrone.FALLO) {
            ordenes.add("Revisar " + drone.id() + " (" + drone.tipo() + "): entró en FALLO");
        }
    }

    public List<String> ordenes() {
        return List.copyOf(ordenes);
    }
}
