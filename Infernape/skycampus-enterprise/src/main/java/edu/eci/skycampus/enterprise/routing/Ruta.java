package edu.eci.skycampus.enterprise.routing;

import edu.eci.skycampus.enterprise.domain.Sede;
import java.util.List;

public interface Ruta {
    String id();

    Sede origen();

    Sede destino();

    double nivelRiesgo();

    int cantidadEtapas();

    double tiempoEstimadoHoras(CondicionesViento viento);

    List<DroneEtapa> ejecutar(ContextoEjecucionRuta contexto);
}
