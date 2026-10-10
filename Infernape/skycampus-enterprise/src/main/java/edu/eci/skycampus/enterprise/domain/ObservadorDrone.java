package edu.eci.skycampus.enterprise.domain;

public interface ObservadorDrone {
    void onEstadoCambiado(String misionId, Drone drone, EstadoMision estado);
}
