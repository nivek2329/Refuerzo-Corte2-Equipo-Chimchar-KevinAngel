package edu.eci.skycampus.enterprise.infrastructure.config;

import edu.eci.skycampus.enterprise.domain.Drone;
import edu.eci.skycampus.enterprise.domain.EstadoMision;
import edu.eci.skycampus.enterprise.domain.ObservadorDrone;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Observador que registra cada cambio de estado en el log y en memoria (lo consulta el panel). */
@Component
public class RegistroNotificaciones implements ObservadorDrone {
    private static final Logger LOG = LoggerFactory.getLogger(RegistroNotificaciones.class);
    private final List<String> eventos = new CopyOnWriteArrayList<>();

    @Override
    public void onEstadoCambiado(String misionId, Drone drone, EstadoMision estado) {
        String evento = misionId + ":" + drone.id() + ":" + estado;
        eventos.add(evento);
        LOG.info("Misión {} con drone {} pasa a {}", misionId, drone.id(), estado);
    }

    public List<String> eventos() {
        return List.copyOf(eventos);
    }
}
