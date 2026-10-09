package edu.eci.skycampus.enterprise.routing;

import java.util.List;

public interface EstrategiaOptimizacionRuta {
    Ruta elegir(List<Ruta> alternativas, CondicionesViento viento);
}
