package edu.eci.skycampus.enterprise.routing;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class RutaMasRapida implements EstrategiaOptimizacionRuta {
    @Override
    public Ruta elegir(List<Ruta> alternativas, CondicionesViento viento) {
        List<Ruta> rutas = ValidacionAlternativasRutas.validar(alternativas);
        Objects.requireNonNull(viento, "viento no puede ser null");
        return rutas.stream().min(Comparator.comparingDouble((Ruta ruta) -> ruta.tiempoEstimadoHoras(viento))
                .thenComparingDouble(Ruta::nivelRiesgo).thenComparing(Ruta::id)).orElseThrow();
    }
}
