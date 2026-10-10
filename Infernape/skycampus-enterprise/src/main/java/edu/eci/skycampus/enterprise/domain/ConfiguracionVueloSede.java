package edu.eci.skycampus.enterprise.domain;

import java.util.Objects;

/**
 * RF-12: el coordinador configura el radio máximo de vuelo de su sede.
 * Resolución de la tensión con RNF-09: el valor configurado se guarda tal cual (es la intención del coordinador),
 * pero el radio que se aplica es el menor entre lo configurado y lo autorizado por la Aerocivil.
 */
public record ConfiguracionVueloSede(Sede sede, double radioConfiguradoKm) {
    public ConfiguracionVueloSede {
        Objects.requireNonNull(sede, "sede no puede ser null");
        if (!Double.isFinite(radioConfiguradoKm) || radioConfiguradoKm <= 0) {
            throw new IllegalArgumentException("el radio configurado debe ser positivo");
        }
    }

    public double radioEfectivoKm(LimitesAerocivil limites) {
        return Math.min(radioConfiguradoKm, Objects.requireNonNull(limites, "limites no puede ser null").radioMaximoKm());
    }

    /** true si la Aerocivil recortó lo que pidió el coordinador (la interfaz debe avisarle). */
    public boolean recortadaPorAerocivil(LimitesAerocivil limites) {
        return radioEfectivoKm(limites) < radioConfiguradoKm;
    }

    public boolean permiteTramo(double distanciaKm, int alturaMetros, boolean zonaUrbana, LimitesAerocivil limites) {
        return distanciaKm > 0 && distanciaKm <= radioEfectivoKm(limites)
                && limites.permiteAltura(alturaMetros, zonaUrbana);
    }
}
