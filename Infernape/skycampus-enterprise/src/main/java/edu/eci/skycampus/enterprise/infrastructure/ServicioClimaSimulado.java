package edu.eci.skycampus.enterprise.infrastructure;

import edu.eci.skycampus.enterprise.domain.ServicioClima;
import java.util.Objects;
import java.util.function.BiPredicate;

/** Adaptador local configurable; puede reemplazarse por un cliente HTTP sin tocar aplicación ni dominio. */
public final class ServicioClimaSimulado implements ServicioClima {
    private final BiPredicate<String, String> condicionesAptas;

    public ServicioClimaSimulado(BiPredicate<String, String> condicionesAptas) {
        this.condicionesAptas = Objects.requireNonNull(condicionesAptas, "regla climática no puede ser null");
    }

    @Override
    public boolean condicionesAptas(String origen, String destino) {
        Objects.requireNonNull(origen, "origen no puede ser null");
        Objects.requireNonNull(destino, "destino no puede ser null");
        return condicionesAptas.test(origen, destino);
    }
}
