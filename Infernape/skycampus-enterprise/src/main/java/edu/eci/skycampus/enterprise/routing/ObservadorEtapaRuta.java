package edu.eci.skycampus.enterprise.routing;

@FunctionalInterface
public interface ObservadorEtapaRuta {
    void alIniciarEtapa(EventoEtapaRuta evento);
}
