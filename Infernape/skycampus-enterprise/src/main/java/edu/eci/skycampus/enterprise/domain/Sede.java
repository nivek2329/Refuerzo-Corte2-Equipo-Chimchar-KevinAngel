package edu.eci.skycampus.enterprise.domain;

import java.util.Objects;

public record Sede(String codigo, String universidad) {
    public Sede {
        Objects.requireNonNull(codigo, "codigo de sede no puede ser null");
        Objects.requireNonNull(universidad, "universidad no puede ser null");
        if (codigo.isBlank() || universidad.isBlank()) {
            throw new IllegalArgumentException("codigo y universidad de sede son obligatorios");
        }
    }
}
