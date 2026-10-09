package edu.eci.skycampus.asignacion;

import edu.eci.skycampus.externo.ApiMeteorologica;
import edu.eci.skycampus.modelo.Drone;
import edu.eci.skycampus.modelo.EstadoDrone;
import edu.eci.skycampus.modelo.Prioridad;
import edu.eci.skycampus.modelo.SolicitudReparto;
import edu.eci.skycampus.notificacion.GestorFlota;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AsignadorMision {
    private final ApiMeteorologica clima;
    private final GestorFlota gestorFlota;
    private final EstrategiaAsignacion estrategiaNormal;
    private final EstrategiaAsignacion estrategiaUrgente;

    public AsignadorMision(ApiMeteorologica clima, GestorFlota gestorFlota,
                           EstrategiaAsignacion estrategiaNormal, EstrategiaAsignacion estrategiaUrgente) {
        this.clima = Objects.requireNonNull(clima, "clima no puede ser null");
        this.gestorFlota = gestorFlota;
        this.estrategiaNormal = estrategiaNormal;
        this.estrategiaUrgente = estrategiaUrgente;
    }

    public Optional<Drone> asignar(List<Drone> flota, SolicitudReparto solicitud) {
        Objects.requireNonNull(solicitud, "solicitud no puede ser null");
        int peso = solicitud.paquete().pesoGramos();
        if (peso > 2000) {
            throw new IllegalArgumentException(
                    "el paquete pesa " + peso + " g y supera la capacidad máxima de la flota (2000 g)");
        }
        if (!clima.esApto()) {
            return Optional.empty();
        }
        EstrategiaAsignacion estrategia =
                solicitud.paquete().prioridad() == Prioridad.URGENTE ? estrategiaUrgente : estrategiaNormal;
        return estrategia.seleccionar(flota, solicitud.paquete())
                .map(drone -> gestorFlota.cambiarEstado(drone, EstadoDrone.EN_VUELO));
    }
}
