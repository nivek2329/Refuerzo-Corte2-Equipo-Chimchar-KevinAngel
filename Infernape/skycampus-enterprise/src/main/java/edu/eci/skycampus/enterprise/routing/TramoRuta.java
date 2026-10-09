package edu.eci.skycampus.enterprise.routing;

import edu.eci.skycampus.enterprise.domain.Sede;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class TramoRuta implements Ruta {
    private final EtapaRuta etapa;
    private final CalculadorRutaInterSede calculador;

    public TramoRuta(EtapaRuta etapa, CalculadorRutaInterSede calculador) {
        this.etapa = Objects.requireNonNull(etapa, "etapa no puede ser null");
        this.calculador = Objects.requireNonNull(calculador, "calculador no puede ser null");
    }

    public EtapaRuta etapa() { return etapa; }

    @Override public String id() { return etapa.id(); }
    @Override public Sede origen() { return etapa.origen(); }
    @Override public Sede destino() { return etapa.destino(); }
    @Override public double nivelRiesgo() { return etapa.nivelRiesgo(); }
    @Override public int cantidadEtapas() { return 1; }

    @Override
    public double tiempoEstimadoHoras(CondicionesViento viento) {
        Objects.requireNonNull(viento, "viento no puede ser null");
        return calculador.tiempoEstimadoHoras(etapa.distanciaKm(), velocidadDron(), viento.velocidadKmH(),
                etapa.rumboGrados(), viento.direccionDesdeGrados());
    }

    @Override
    public List<DroneEtapa> ejecutar(ContextoEjecucionRuta contexto) {
        Objects.requireNonNull(contexto, "contexto no puede ser null");
        DroneEtapa drone = contexto.fabricaDrones().crear(etapa);
        notificarLlegada(contexto, drone);
        return List.of(drone);
    }

    private void notificarLlegada(ContextoEjecucionRuta contexto, DroneEtapa drone) {
        long duracionMs = Math.round(tiempoEstimadoHoras(contexto.viento()) * 3_600_000);
        Instant llegada = contexto.inicio().plusMillis(duracionMs);
        EventoEtapaRuta evento = new EventoEtapaRuta(id(), drone.id(), origen().codigo(), destino().codigo(),
                contexto.inicio(), llegada);
        contexto.observadores().forEach(observador -> observador.alIniciarEtapa(evento));
    }

    private double velocidadDron() {
        return etapa.tipoDroneRequerido().velocidadNominalKmH();
    }
}
