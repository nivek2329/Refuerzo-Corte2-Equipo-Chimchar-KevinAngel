package edu.eci.skycampus.enterprise.routing;

import java.util.List;
import java.util.Objects;

final class ValidacionAlternativasRutas {
    private ValidacionAlternativasRutas() { }

    static List<Ruta> validar(List<Ruta> alternativas) {
        Objects.requireNonNull(alternativas, "alternativas no puede ser null");
        if (alternativas.isEmpty()) throw new IllegalArgumentException("debe existir al menos una ruta alternativa");
        alternativas.forEach(ruta -> Objects.requireNonNull(ruta, "ruta alternativa no puede ser null"));
        return List.copyOf(alternativas);
    }
}
