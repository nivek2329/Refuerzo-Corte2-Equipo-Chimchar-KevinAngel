package edu.eci.skycampus.modelo;

/** Ciclo v2: DISPONIBLE → EN_VUELO → ATERRIZANDO → DISPONIBLE; FALLO y MANTENIMIENTO los gestiona el técnico. */
public enum EstadoDrone {
    DISPONIBLE,
    EN_VUELO,
    ATERRIZANDO,
    EN_CARGA,
    FALLO,
    MANTENIMIENTO
}
