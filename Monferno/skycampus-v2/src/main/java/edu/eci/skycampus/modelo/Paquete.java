package edu.eci.skycampus.modelo;

import java.util.Objects;

public record Paquete(int pesoGramos, TipoCarga tipoCarga, Prioridad prioridad) {
    public Paquete {
        Objects.requireNonNull(tipoCarga, "tipoCarga no puede ser null");
        Objects.requireNonNull(prioridad, "prioridad no puede ser null");
        if (pesoGramos <= 0) {
            throw new IllegalArgumentException("pesoGramos debe ser mayor que 0");
        }
    }

    public boolean isUrgente() {
        return prioridad == Prioridad.URGENTE;
    }
}
