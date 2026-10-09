package edu.eci.skycampus.notificacion;

import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Registra cada cambio con fecha y hora; el reloj se inyecta para que las pruebas sean deterministas. */
public class SistemaLog implements ObservadorDrone {
    private final Clock reloj;
    private final List<String> registros = new ArrayList<>();

    public SistemaLog(Clock reloj) {
        this.reloj = Objects.requireNonNull(reloj, "reloj no puede ser null");
    }

    @Override
    public void onEstadoCambiado(Drone drone, EstadoDrone nuevo) {
        registros.add(LocalDateTime.now(reloj) + " | " + drone.id() + " | " + nuevo);
    }

    public List<String> registros() {
        return List.copyOf(registros);
    }
}
