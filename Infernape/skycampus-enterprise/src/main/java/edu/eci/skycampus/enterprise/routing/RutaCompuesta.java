package edu.eci.skycampus.enterprise.routing;

import edu.eci.skycampus.enterprise.domain.Sede;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record RutaCompuesta(String id, List<Ruta> etapas) implements Ruta {
    public RutaCompuesta {
        Objects.requireNonNull(id, "id de ruta no puede ser null");
        etapas = List.copyOf(etapas);
        validarEtapas(id, etapas);
    }

    @Override public Sede origen() { return etapas.get(0).origen(); }
    @Override public Sede destino() { return etapas.get(etapas.size() - 1).destino(); }

    @Override
    public double nivelRiesgo() {
        return etapas.stream().mapToDouble(Ruta::nivelRiesgo).max().orElseThrow();
    }

    @Override
    public int cantidadEtapas() {
        return etapas.stream().mapToInt(Ruta::cantidadEtapas).sum();
    }

    @Override
    public double tiempoEstimadoHoras(CondicionesViento viento) {
        Objects.requireNonNull(viento, "viento no puede ser null");
        return etapas.stream().mapToDouble(etapa -> etapa.tiempoEstimadoHoras(viento)).sum();
    }

    @Override
    public List<DroneEtapa> ejecutar(ContextoEjecucionRuta contexto) {
        Objects.requireNonNull(contexto, "contexto no puede ser null");
        List<DroneEtapa> drones = new ArrayList<>();
        Instant inicioEtapa = contexto.inicio();
        for (Ruta etapa : etapas) inicioEtapa = ejecutarEtapa(etapa, contexto, inicioEtapa, drones);
        return List.copyOf(drones);
    }

    private Instant ejecutarEtapa(Ruta etapa, ContextoEjecucionRuta contexto, Instant inicio,
                                  List<DroneEtapa> drones) {
        ContextoEjecucionRuta contextoEtapa = contexto.desde(inicio);
        drones.addAll(etapa.ejecutar(contextoEtapa));
        long milisegundos = Math.round(etapa.tiempoEstimadoHoras(contexto.viento()) * 3_600_000);
        return inicio.plusMillis(milisegundos);
    }

    private static void validarEtapas(String id, List<Ruta> etapas) {
        Objects.requireNonNull(etapas, "etapas no puede ser null");
        etapas.forEach(etapa -> Objects.requireNonNull(etapa, "etapa no puede ser null"));
        if (id.isBlank() || etapas.isEmpty()) throw new IllegalArgumentException("ruta compuesta requiere id y etapas");
        for (int indice = 1; indice < etapas.size(); indice++) validarContinuidad(etapas, indice);
    }

    private static void validarContinuidad(List<Ruta> etapas, int indice) {
        Ruta anterior = Objects.requireNonNull(etapas.get(indice - 1), "etapa no puede ser null");
        Ruta actual = Objects.requireNonNull(etapas.get(indice), "etapa no puede ser null");
        if (!anterior.destino().equals(actual.origen())) {
            throw new IllegalArgumentException("las etapas consecutivas deben compartir sede");
        }
    }
}
