package edu.eci.skycampus.modelo;

import java.util.EnumSet;
import java.util.Set;

/**
 * Estados de un drone y transiciones permitidas. Ciclo normal: DISPONIBLE → EN_VUELO → ATERRIZANDO → DISPONIBLE.
 * Un drone en FALLO solo sale a MANTENIMIENTO (lo gestiona el técnico) y de ahí vuelve a DISPONIBLE.
 */
public enum EstadoDrone {
    DISPONIBLE,
    EN_VUELO,
    ATERRIZANDO,
    EN_CARGA,
    FALLO,
    MANTENIMIENTO;

    public boolean puedePasarA(EstadoDrone destino) {
        return siguientesPermitidos().contains(destino);
    }

    private Set<EstadoDrone> siguientesPermitidos() {
        return switch (this) {
            case DISPONIBLE -> EnumSet.of(EN_VUELO, EN_CARGA, MANTENIMIENTO, FALLO);
            case EN_VUELO -> EnumSet.of(ATERRIZANDO, FALLO);
            case ATERRIZANDO -> EnumSet.of(DISPONIBLE, EN_CARGA, FALLO);
            case EN_CARGA -> EnumSet.of(DISPONIBLE, FALLO);
            case FALLO -> EnumSet.of(MANTENIMIENTO);
            case MANTENIMIENTO -> EnumSet.of(DISPONIBLE);
        };
    }
}
