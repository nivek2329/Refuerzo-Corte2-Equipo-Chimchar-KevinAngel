package edu.eci.skycampus.enterprise.infrastructure;

import edu.eci.skycampus.enterprise.domain.ServicioAerocivil;
import java.util.Objects;
import java.util.function.BiPredicate;

/** Adaptador local configurable; en producción se reemplaza por el cliente HTTP de la Aerocivil. */
public final class ServicioAerocivilSimulado implements ServicioAerocivil {
    private final BiPredicate<String, String> regla;

    public ServicioAerocivilSimulado(BiPredicate<String, String> regla) {
        this.regla = Objects.requireNonNull(regla, "regla de la Aerocivil no puede ser null");
    }

    @Override
    public boolean autorizaRuta(String origen, String destino) {
        Objects.requireNonNull(origen, "origen no puede ser null");
        Objects.requireNonNull(destino, "destino no puede ser null");
        return regla.test(origen, destino);
    }
}
